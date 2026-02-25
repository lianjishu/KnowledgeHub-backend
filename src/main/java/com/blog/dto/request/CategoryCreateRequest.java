package com.blog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称最多50字符")
    private String name;

    @Size(max = 200, message = "描述最多200字符")
    private String description = "";

    private String parentId;

    @Min(value = 1, message = "层级最小为1")
    @Max(value = 3, message = "层级最大为3")
    private Integer level = 1;

    private String type = "blog";

    private Integer sortOrder = 0;
}
