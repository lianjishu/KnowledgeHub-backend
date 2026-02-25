package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @Size(max = 500, message = "摘要最多500字符")
    private String summary = "";

    private String cover = "";

    @NotBlank(message = "分类ID不能为空")
    private String categoryId;

    private String type = "blog";

    private List<String> tags = List.of();

    private String status = "published";
}
