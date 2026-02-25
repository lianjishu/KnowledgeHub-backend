package com.blog.service;

import com.blog.entity.User;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitAdmin implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查管理员用户是否存在
        if (userRepository.findByUsername("admin").isEmpty()) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));  // 默认密码
            admin.setRole("admin");
            admin.setStatus("active");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setLastLoginTime(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("管理员账户已创建: admin / admin123");
        } else {
            System.out.println("管理员账户已存在");
        }
    }
}