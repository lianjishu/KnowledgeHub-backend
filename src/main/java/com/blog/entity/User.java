package com.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(name = "user_username_unique", unique = true)
    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @Field("avatar")
    private String avatar;

    @Field("role")
    private String role = "user";

    @Field("status")
    private String status = "active";

    @Field("last_login_time")
    private LocalDateTime lastLoginTime;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
