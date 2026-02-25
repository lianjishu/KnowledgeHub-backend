package com.blog.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;
    private Map<String, Object> errors;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(400)
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, Map<String, Object> errors) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
