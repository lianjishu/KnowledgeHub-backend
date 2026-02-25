package com.blog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    @Value("${upload.max-file-size:10485760}")
    private long maxFileSize;

    @Value("${upload.allowed-file-types:jpg,jpeg,png,gif,webp}")
    private String allowedFileTypes;

    private List<String> getAllowedFileTypesList() {
        return Arrays.asList(allowedFileTypes.split(","));
    }

    public boolean isValidFileType(String extension) {
        return getAllowedFileTypesList().contains(extension.toLowerCase());
    }

    public boolean isValidFileSize(long size) {
        return size <= maxFileSize;
    }

    public String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public FileSaveResult saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        // Create date-based directory structure
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path saveDir = Paths.get(uploadDir, datePath);
        Files.createDirectories(saveDir);

        // Save file
        Path filePath = saveDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Build relative path for response
        String relativePath = "/uploads/" + datePath + "/" + uniqueFilename;

        return FileSaveResult.builder()
                .filename(uniqueFilename)
                .originalName(originalFilename)
                .path(relativePath)
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();
    }

    public boolean deleteFile(String path) {
        if (!path.startsWith("/uploads/")) {
            return false;
        }
        try {
            String localPathStr = uploadDir + path.substring("/uploads".length());
            Path localPath = Paths.get(localPathStr);
            return Files.deleteIfExists(localPath);
        } catch (IOException e) {
            return false;
        }
    }

    public Path getLocalPath(String path) {
        if (!path.startsWith("/uploads/")) {
            return null;
        }
        String localPathStr = uploadDir + path.substring("/uploads".length());
        return Paths.get(localPathStr);
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class FileSaveResult {
        private String filename;
        private String originalName;
        private String path;
        private long size;
        private String contentType;
    }
}
