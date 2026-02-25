package com.blog.repository;

import com.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByArticleId(String articleId, Pageable pageable);

    Page<Comment> findByStatus(String status, Pageable pageable);

    Page<Comment> findByArticleIdAndStatus(String articleId, String status, Pageable pageable);

    List<Comment> findByArticleIdOrderByCreatedAtDesc(String articleId);

    long countByStatus(String status);

    long countByArticleId(String articleId);
}
