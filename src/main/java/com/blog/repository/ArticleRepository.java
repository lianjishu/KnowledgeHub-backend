package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {

    Page<Article> findByStatus(String status, Pageable pageable);

    Page<Article> findByCategoryIdAndStatus(String categoryId, String status, Pageable pageable);

    Page<Article> findByTypeAndStatus(String type, String status, Pageable pageable);

    Page<Article> findByAuthorId(String authorId, Pageable pageable);

    @Query("{'status': ?0, $or: [{'title': {$regex: ?1, $options: 'i'}}, {'content': {$regex: ?1, $options: 'i'}}]}")
    Page<Article> searchByKeyword(String status, String keyword, Pageable pageable);

    List<Article> findByStatusOrderByViewCountDesc(String status, Pageable pageable);

    long countByStatus(String status);

    long countByCategoryId(String categoryId);
}
