package com.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "message", "Blog Backend API",
                "status", "running",
                "health", "/health"
        );
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "healthy",
                "timestamp", System.currentTimeMillis()
        );
    }
}
