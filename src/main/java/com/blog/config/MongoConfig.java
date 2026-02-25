package com.blog.config;

import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Comment;
import com.blog.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    // @PostConstruct
    public void initIndexes() {
        // User indexes
        IndexOperations userOps = mongoTemplate.indexOps(User.class);
        userOps.ensureIndex(new Index("username", Sort.Direction.ASC).unique());

        // Article indexes
        IndexOperations articleOps = mongoTemplate.indexOps(Article.class);
        articleOps.ensureIndex(new Index()
                .on("category_id", Sort.Direction.ASC)
                .on("type", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC));
        articleOps.ensureIndex(new Index()
                .on("author_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC));
        articleOps.ensureIndex(new Index()
                .on("status", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC));

        // Category indexes
        IndexOperations categoryOps = mongoTemplate.indexOps(Category.class);
        categoryOps.ensureIndex(new Index("name", Sort.Direction.ASC).unique());
        categoryOps.ensureIndex(new Index("type", Sort.Direction.ASC));
        categoryOps.ensureIndex(new Index("parent_id", Sort.Direction.ASC));
        categoryOps.ensureIndex(new Index("sort_order", Sort.Direction.ASC));

        // Comment indexes
        IndexOperations commentOps = mongoTemplate.indexOps(Comment.class);
        commentOps.ensureIndex(new Index()
                .on("article_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC));
        commentOps.ensureIndex(new Index()
                .on("status", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC));
        commentOps.ensureIndex(new Index("user_email", Sort.Direction.ASC).sparse());
    }
}
