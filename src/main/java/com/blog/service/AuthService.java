package com.blog.service;

import com.blog.dto.request.ChangePasswordRequest;
import com.blog.dto.response.LoginResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.repository.UserRepository;
import com.blog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // Update last login time
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createTime(user.getCreatedAt())
                .lastLoginTime(user.getLastLoginTime())
                .build();

        return LoginResponse.builder()
                .user(userInfo)
                .token(token)
                .build();
    }

    public LoginResponse.UserInfo register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("admin");
        user.setStatus("active");
        user.setAvatar("");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createTime(user.getCreatedAt())
                .build();
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException("用户不存在");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserResponse getUserInfo(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar() != null ? user.getAvatar() : "")
                .role(user.getRole())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public Optional<User> findUserById(String userId) {
        return userRepository.findById(userId);
    }
}
