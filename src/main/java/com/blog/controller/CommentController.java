package com.blog.controller;

import com.blog.common.ApiResponse;
import com.blog.dto.request.CommentCheckRequest;
import com.blog.dto.request.CommentCreateRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ApiResponse<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createComment(request);
        return ApiResponse.success(response, "评论提交成功，等待审核");
    }

    @PostMapping("/{commentId}/check")
    public ApiResponse<CommentResponse> checkComment(@PathVariable String commentId,
                                                       @Valid @RequestBody CommentCheckRequest request) {
        CommentResponse response = commentService.checkComment(commentId, request.getStatus());
        return ApiResponse.success(response, "审核成功");
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.success(null, "评论删除成功");
    }

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getCommentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String articleId,
            @RequestParam(required = false) String status) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Map<String, Object> response = commentService.getCommentList(page, pageSize, articleId, status);
        return ApiResponse.success(response);
    }

    @PostMapping("/{commentId}/reply")
    public ApiResponse<CommentResponse> replyComment(@PathVariable String commentId,
                                                       @Valid @RequestBody CommentCreateRequest request,
                                                       Authentication authentication) {
        // Get current user info from authentication (optional in Python code)
        CommentResponse response = commentService.replyComment(commentId, request);
        return ApiResponse.success(response, "回复成功");
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> response = commentService.getStatistics();
        return ApiResponse.success(response);
    }
}
