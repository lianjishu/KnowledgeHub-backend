package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse {

    private String id;
    private String title;
    private String content;
    private String summary;
    private String cover;
    private String categoryId;
    private String categoryName;
    private String type;
    private List<String> tags;
    private String authorId;
    private String authorName;
    private Integer viewCount;
    private Integer likeCount;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
