package com.blog.controller;

import com.blog.common.ApiResponse;
import com.blog.dto.request.CategoryCreateRequest;
import com.blog.dto.request.CategoryUpdateRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ApiResponse.success(response, "分类创建成功");
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId,
                                                          @Valid @RequestBody CategoryUpdateRequest request) {
        CategoryResponse response = categoryService.updateCategory(categoryId, request);
        return ApiResponse.success(response, "分类更新成功");
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.success(null, "分类删除成功");
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String categoryId) {
        CategoryResponse response = categoryService.getCategoryById(categoryId);
        return ApiResponse.success(response);
    }

    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getCategoryList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer level) {
        List<CategoryResponse> response = categoryService.getCategoryList(type, level);
        return ApiResponse.success(response);
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryResponse>> getCategoryTree(
            @RequestParam(required = false) String type) {
        List<CategoryResponse> response = categoryService.getCategoryTree(type);
        return ApiResponse.success(response);
    }
}
