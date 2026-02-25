package com.blog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleUpdateRequest {

    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String content;

    @Size(max = 500, message = "摘要最多500字符")
    private String summary;

    private String cover;

    private String categoryId;

    private String type;

    private List<String> tags;

    private String status;
}
