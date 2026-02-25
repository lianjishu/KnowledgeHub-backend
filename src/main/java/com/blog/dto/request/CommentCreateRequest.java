package com.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "文章ID不能为空")
    private String articleId;

    @NotBlank(message = "评论者姓名不能为空")
    @Size(max = 50, message = "评论者姓名最多50字符")
    private String userName;

    @Email(message = "邮箱格式不正确")
    private String userEmail;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容最多1000字符")
    private String content;

    private String status = "pending";

    private String ip = "";

    private String userAgent = "";
}
