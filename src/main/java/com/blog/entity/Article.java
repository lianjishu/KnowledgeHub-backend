package com.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("summary")
    private String summary;

    @Field("cover")
    private String cover;

    @Field("category_id")
    private String categoryId;

    @Field("type")
    private String type = "blog";

    @Field("tags")
    private List<String> tags;

    @Field("author_id")
    private String authorId;

    @Field("view_count")
    private Integer viewCount = 0;

    @Field("like_count")
    private Integer likeCount = 0;

    @Field("status")
    private String status = "published";

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Transient fields for response
    private transient String categoryName;
    private transient String authorName;
}
