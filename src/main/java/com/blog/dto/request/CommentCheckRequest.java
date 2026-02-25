package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCheckRequest {

    @NotBlank(message = "审核状态不能为空")
    private String status;
}
