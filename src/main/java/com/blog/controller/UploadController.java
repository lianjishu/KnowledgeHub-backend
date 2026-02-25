package com.blog.controller;

import com.blog.common.ApiResponse;
import com.blog.dto.response.FileInfoResponse;
import com.blog.dto.response.FileUploadResponse;
import com.blog.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUtil fileUtil;

    @Value("${upload.max-file-size:10485760}")
    private long maxFileSize;

    @Value("${upload.allowed-file-types:jpg,jpeg,png,gif,webp}")
    private String allowedFileTypes;

    private List<String> getAllowedFileTypesList() {
        return Arrays.asList(allowedFileTypes.split(","));
    }

    @PostMapping("/upload")
    public ApiResponse<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error(400, "文件不能为空");
            }

            // Validate file size
            if (file.getSize() > maxFileSize) {
                return ApiResponse.error(400, "文件大小超过限制 (" + maxFileSize + " 字节)");
            }

            // Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null) {
                return ApiResponse.error(400, "无法确定文件名");
            }
            String extension = fileUtil.getFileExtension(filename);
            if (!getAllowedFileTypesList().contains(extension.toLowerCase())) {
                return ApiResponse.error(400, "不支持的文件类型: " + extension);
            }

            FileUtil.FileSaveResult result = fileUtil.saveFile(file);
            log.info("文件上传成功: {}", filename);

            FileUploadResponse response = FileUploadResponse.builder()
                    .filename(result.getFilename())
                    .originalName(result.getOriginalName())
                    .path(result.getPath())
                    .size(result.getSize())
                    .contentType(result.getContentType())
                    .build();

            return ApiResponse.success(response, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error(500, "文件上传失败");
        }
    }

    @PostMapping("/upload-multiple")
    public ApiResponse<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> success = new ArrayList<>();
            List<Map<String, String>> errors = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    if (file.isEmpty()) {
                        errors.add(Map.of("filename", "unknown", "error", "文件为空"));
                        continue;
                    }

                    // Validate file size
                    if (file.getSize() > maxFileSize) {
                        errors.add(Map.of("filename",
                                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown",
                                "error", "文件大小超过限制"));
                        continue;
                    }

                    // Validate file type
                    String filename = file.getOriginalFilename();
                    if (filename == null) {
                        errors.add(Map.of("filename", "unknown", "error", "无法确定文件名"));
                        continue;
                    }
                    String extension = fileUtil.getFileExtension(filename);
                    if (!getAllowedFileTypesList().contains(extension.toLowerCase())) {
                        errors.add(Map.of("filename", filename, "error", "不支持的文件类型: " + extension));
                        continue;
                    }

                    FileUtil.FileSaveResult result = fileUtil.saveFile(file);
                    success.add(FileUploadResponse.builder()
                            .filename(result.getFilename())
                            .originalName(result.getOriginalName())
                            .path(result.getPath())
                            .size(result.getSize())
                            .contentType(result.getContentType())
                            .build());
                } catch (Exception e) {
                    log.error("文件上传失败: {}", file.getOriginalFilename(), e);
                    errors.add(Map.of("filename",
                            file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown",
                            "error", e.getMessage()));
                }
            }

            log.info("批量上传成功: {} 个文件", success.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

            return ApiResponse.success(response, "成功上传 " + success.size() + " 个文件");
        } catch (Exception e) {
            log.error("批量上传失败", e);
            return ApiResponse.error(500, "批量上传失败");
        }
    }

    @PostMapping("/delete")
    public ApiResponse<Void> deleteFile(@RequestParam("path") String path) {
        try {
            if (path == null || path.isEmpty()) {
                return ApiResponse.error(400, "文件路径不能为空");
            }

            if (!path.startsWith("/uploads/")) {
                return ApiResponse.error(400, "无效的文件路径");
            }

            boolean deleted = fileUtil.deleteFile(path);
            if (!deleted) {
                return ApiResponse.error(404, "文件不存在");
            }

            log.info("文件删除成功: {}", path);
            return ApiResponse.success(null, "文件删除成功");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return ApiResponse.error(500, "文件删除失败");
        }
    }

    @GetMapping("/info")
    public ApiResponse<FileInfoResponse> getFileInfo(@RequestParam("path") String path) {
        try {
            if (path == null || path.isEmpty()) {
                return ApiResponse.error(400, "文件路径不能为空");
            }

            if (!path.startsWith("/uploads/")) {
                return ApiResponse.error(400, "无效的文件路径");
            }

            Path localPath = fileUtil.getLocalPath(path);
            if (localPath == null || !Files.exists(localPath)) {
                return ApiResponse.error(404, "文件不存在");
            }

            long size = Files.size(localPath);
            String extension = fileUtil.getFileExtension(path);
            LocalDateTime modifiedAt = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Files.getLastModifiedTime(localPath).toMillis()),
                    ZoneId.systemDefault()
            );

            FileInfoResponse response = FileInfoResponse.builder()
                    .path(path)
                    .size(size)
                    .extension(extension)
                    .modifiedAt(modifiedAt)
                    .build();

            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
            return ApiResponse.error(500, "获取文件信息失败");
        }
    }
}
