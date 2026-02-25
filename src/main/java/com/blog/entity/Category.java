package com.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @Indexed(name = "category_name_unique", unique = true)
    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("parent_id")
    private String parentId;

    @Field("level")
    private Integer level = 1;

    @Field("type")
    private String type = "blog";

    @Field("sort_order")
    private Integer sortOrder = 0;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Transient field for children
    private transient List<Category> children = new ArrayList<>();
}
