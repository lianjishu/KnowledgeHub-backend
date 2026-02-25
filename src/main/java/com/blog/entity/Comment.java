package com.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Field("article_id")
    private String articleId;

    @Field("user_name")
    private String userName;

    @Field("user_email")
    private String userEmail;

    @Field("content")
    private String content;

    @Field("status")
    private String status = "pending";

    @Field("ip")
    private String ip;

    @Field("user_agent")
    private String userAgent;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
