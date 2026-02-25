package com.blog.service;

import com.blog.dto.request.CommentCreateRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.entity.Comment;
import com.blog.exception.BusinessException;
import com.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentResponse createComment(CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setUserName(request.getUserName());
        comment.setUserEmail(request.getUserEmail());
        comment.setContent(request.getContent());
        comment.setStatus(request.getStatus() != null ? request.getStatus() : "pending");
        comment.setIp(request.getIp() != null ? request.getIp() : "");
        comment.setUserAgent(request.getUserAgent() != null ? request.getUserAgent() : "");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    public CommentResponse checkComment(String commentId, String status) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new BusinessException("评论不存在");
        }

        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new BusinessException("无效的审核状态");
        }

        Comment comment = commentOpt.get();
        comment.setStatus(status);
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    public void deleteComment(String commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new BusinessException("评论不存在");
        }
        commentRepository.deleteById(commentId);
    }

    public Map<String, Object> getCommentList(int page, int pageSize, String articleId, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        Page<Comment> commentPage;
        if (articleId != null && status != null) {
            commentPage = commentRepository.findByArticleIdAndStatus(articleId, status, pageable);
        } else if (articleId != null) {
            commentPage = commentRepository.findByArticleId(articleId, pageable);
        } else if (status != null) {
            commentPage = commentRepository.findByStatus(status, pageable);
        } else {
            commentPage = commentRepository.findAll(pageable);
        }

        List<CommentResponse> responses = commentPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", responses);
        result.put("pagination", Map.of(
                "page", page,
                "page_size", pageSize,
                "total", commentPage.getTotalElements(),
                "total_pages", commentPage.getTotalPages()
        ));
        return result;
    }

    public CommentResponse replyComment(String commentId, CommentCreateRequest request) {
        Optional<Comment> parentOpt = commentRepository.findById(commentId);
        if (parentOpt.isEmpty()) {
            throw new BusinessException("原评论不存在");
        }

        // Create reply as a new comment on the same article
        Comment reply = new Comment();
        reply.setArticleId(parentOpt.get().getArticleId());
        reply.setUserName(request.getUserName());
        reply.setUserEmail(request.getUserEmail());
        reply.setContent(request.getContent());
        reply.setStatus("approved");
        reply.setIp(request.getIp() != null ? request.getIp() : "");
        reply.setUserAgent(request.getUserAgent() != null ? request.getUserAgent() : "");
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());

        reply = commentRepository.save(reply);
        return toResponse(reply);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", commentRepository.count());
        stats.put("pending", commentRepository.countByStatus("pending"));
        stats.put("approved", commentRepository.countByStatus("approved"));
        stats.put("rejected", commentRepository.countByStatus("rejected"));
        return stats;
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .userName(comment.getUserName())
                .userEmail(comment.getUserEmail())
                .content(comment.getContent())
                .status(comment.getStatus())
                .ip(comment.getIp())
                .userAgent(comment.getUserAgent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
