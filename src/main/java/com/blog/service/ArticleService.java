package com.blog.service;

import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleResponse;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public ArticleResponse createArticle(ArticleCreateRequest request, String authorId) {
        // Validate category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new BusinessException("分类不存在");
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary() != null ? request.getSummary() : "");
        article.setCover(request.getCover() != null ? request.getCover() : "");
        article.setCategoryId(request.getCategoryId());
        article.setType(request.getType() != null ? request.getType() : "blog");
        article.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        article.setAuthorId(authorId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setStatus(request.getStatus() != null ? request.getStatus() : "published");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        article = articleRepository.save(article);
        return toResponse(article);
    }

    public ArticleResponse updateArticle(String articleId, ArticleUpdateRequest request) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            throw new BusinessException("文章不存在");
        }

        Article article = articleOpt.get();

        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getCover() != null) {
            article.setCover(request.getCover());
        }
        if (request.getCategoryId() != null) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                throw new BusinessException("分类不存在");
            }
            article.setCategoryId(request.getCategoryId());
        }
        if (request.getType() != null) {
            article.setType(request.getType());
        }
        if (request.getTags() != null) {
            article.setTags(request.getTags());
        }
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }

        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepository.save(article);
        return toResponse(article);
    }

    public void deleteArticle(String articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new BusinessException("文章不存在");
        }
        articleRepository.deleteById(articleId);
    }

    public ArticleResponse getArticleById(String articleId, boolean incrementView) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            throw new BusinessException("文章不存在");
        }

        Article article = articleOpt.get();

        if (incrementView) {
            article.setViewCount(article.getViewCount() + 1);
            article = articleRepository.save(article);
        }

        return toResponse(article);
    }

    public Map<String, Object> getArticleList(int page, int pageSize, String status,
                                                 String categoryId, String type, String keyword,
                                                 String authorId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        Query query = new Query();

        if (StringUtils.hasText(status)) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (StringUtils.hasText(categoryId)) {
            query.addCriteria(Criteria.where("category_id").is(categoryId));
        }
        if (StringUtils.hasText(type)) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (StringUtils.hasText(keyword)) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("title").regex(keyword, "i"),
                    Criteria.where("content").regex(keyword, "i")
            ));
        }
        if (StringUtils.hasText(authorId)) {
            query.addCriteria(Criteria.where("author_id").is(authorId));
        }

        long total = mongoTemplate.count(query, Article.class);
        List<Article> articles = mongoTemplate.find(query.with(pageable), Article.class);

        List<ArticleResponse> responses = articles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", responses);
        result.put("pagination", Map.of(
                "page", page,
                "page_size", pageSize,
                "total", total,
                "total_pages", (total + pageSize - 1) / pageSize
        ));
        return result;
    }

    public List<ArticleResponse> getHotArticles(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Article> articles = articleRepository.findByStatusOrderByViewCountDesc("published", pageable);
        return articles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponse likeArticle(String articleId) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            throw new BusinessException("文章不存在");
        }

        Article article = articleOpt.get();
        article.setLikeCount(article.getLikeCount() + 1);
        article = articleRepository.save(article);
        return toResponse(article);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", articleRepository.count());
        stats.put("published", articleRepository.countByStatus("published"));
        stats.put("draft", articleRepository.countByStatus("draft"));
        return stats;
    }

    private ArticleResponse toResponse(Article article) {
        ArticleResponse.ArticleResponseBuilder builder = ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .summary(article.getSummary() != null ? article.getSummary() : "")
                .cover(article.getCover() != null ? article.getCover() : "")
                .categoryId(article.getCategoryId())
                .type(article.getType())
                .tags(article.getTags() != null ? article.getTags() : new ArrayList<>())
                .authorId(article.getAuthorId())
                .viewCount(article.getViewCount() != null ? article.getViewCount() : 0)
                .likeCount(article.getLikeCount() != null ? article.getLikeCount() : 0)
                .status(article.getStatus())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt());

        // Load category name
        if (article.getCategoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(article.getCategoryId());
            categoryOpt.ifPresent(category -> builder.categoryName(category.getName()));
        }

        // Load author name
        if (article.getAuthorId() != null) {
            Optional<User> userOpt = userRepository.findById(article.getAuthorId());
            userOpt.ifPresent(user -> builder.authorName(user.getUsername()));
        }

        return builder.build();
    }
}
