package com.blog.controller;

import com.blog.common.ApiResponse;
import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleResponse;
import com.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/create")
    public ApiResponse<ArticleResponse> createArticle(@Valid @RequestBody ArticleCreateRequest request,
                                                        Authentication authentication) {
        String authorId = (String) authentication.getPrincipal();
        ArticleResponse response = articleService.createArticle(request, authorId);
        return ApiResponse.success(response, "文章创建成功");
    }

    @PutMapping("/{articleId}")
    public ApiResponse<ArticleResponse> updateArticle(@PathVariable String articleId,
                                                        @Valid @RequestBody ArticleUpdateRequest request) {
        ArticleResponse response = articleService.updateArticle(articleId, request);
        return ApiResponse.success(response, "文章更新成功");
    }

    @DeleteMapping("/{articleId}")
    public ApiResponse<Void> deleteArticle(@PathVariable String articleId) {
        articleService.deleteArticle(articleId);
        return ApiResponse.success(null, "文章删除成功");
    }

    @GetMapping("/{articleId}")
    public ApiResponse<ArticleResponse> getArticleById(@PathVariable String articleId,
                                                         @RequestParam(defaultValue = "true") String view) {
        boolean incrementView = !"false".equals(view);
        ArticleResponse response = articleService.getArticleById(articleId, incrementView);
        return ApiResponse.success(response);
    }

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getArticleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String authorId) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Map<String, Object> response = articleService.getArticleList(page, pageSize, status,
                categoryId, type, keyword, authorId);
        return ApiResponse.success(response);
    }

    @GetMapping("/hot")
    public ApiResponse<List<ArticleResponse>> getHotArticles(
            @RequestParam(defaultValue = "10") int limit) {
        if (limit < 1) limit = 1;
        if (limit > 50) limit = 50;

        List<ArticleResponse> response = articleService.getHotArticles(limit);
        return ApiResponse.success(response);
    }

    @PostMapping("/{articleId}/like")
    public ApiResponse<ArticleResponse> likeArticle(@PathVariable String articleId) {
        ArticleResponse response = articleService.likeArticle(articleId);
        return ApiResponse.success(response, "点赞成功");
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> response = articleService.getStatistics();
        return ApiResponse.success(response);
    }
}
