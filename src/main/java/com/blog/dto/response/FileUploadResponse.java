package com.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {

    private String filename;
    private String originalName;
    private String path;
    private long size;
    private String contentType;
}
