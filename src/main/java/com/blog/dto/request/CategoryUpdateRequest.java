package com.blog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    @Size(max = 50, message = "分类名称最多50字符")
    private String name;

    @Size(max = 200, message = "描述最多200字符")
    private String description;

    private String parentId;

    @Min(value = 1, message = "层级最小为1")
    @Max(value = 3, message = "层级最大为3")
    private Integer level;

    private String type;

    private Integer sortOrder;
}
