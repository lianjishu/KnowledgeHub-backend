package com.blog.service;

import com.blog.dto.request.CategoryCreateRequest;
import com.blog.dto.request.CategoryUpdateRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.entity.Category;
import com.blog.exception.BusinessException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("分类名称已存在");
        }

        // Validate parent exists if provided
        if (request.getParentId() != null && !categoryRepository.existsById(request.getParentId())) {
            throw new BusinessException("父分类不存在");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription() != null ? request.getDescription() : "");
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel() != null ? request.getLevel() : 1);
        category.setType(request.getType() != null ? request.getType() : "blog");
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setChildren(new ArrayList<>());

        category = categoryRepository.save(category);
        return toResponse(category);
    }

    public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new BusinessException("分类不存在");
        }

        Category category = categoryOpt.get();

        if (request.getName() != null) {
            // Check name uniqueness
            Optional<Category> existingByName = categoryRepository.findByName(request.getName());
            if (existingByName.isPresent() && !existingByName.get().getId().equals(categoryId)) {
                throw new BusinessException("分类名称已存在");
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getParentId() != null) {
            // Cannot set parent to self
            if (categoryId.equals(request.getParentId())) {
                throw new BusinessException("不能将自己设为父分类");
            }
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new BusinessException("父分类不存在");
            }
            category.setParentId(request.getParentId());
        }
        if (request.getLevel() != null) {
            category.setLevel(request.getLevel());
        }
        if (request.getType() != null) {
            category.setType(request.getType());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);
        return toResponse(category);
    }

    public void deleteCategory(String categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new BusinessException("分类不存在");
        }

        // Check if has children
        if (categoryRepository.countByParentId(categoryId) > 0) {
            throw new BusinessException("请先删除子分类");
        }

        // Check if has articles
        if (articleRepository.countByCategoryId(categoryId) > 0) {
            throw new BusinessException("请先删除该分类下的文章");
        }

        categoryRepository.deleteById(categoryId);
    }

    public CategoryResponse getCategoryById(String categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new BusinessException("分类不存在");
        }
        return toResponse(categoryOpt.get());
    }

    public List<CategoryResponse> getCategoryList(String type, Integer level) {
        List<Category> categories;

        if (type != null && level != null) {
            categories = categoryRepository.findByTypeAndLevel(type, level);
        } else if (type != null) {
            categories = categoryRepository.findByTypeOrderBySortOrderAsc(type);
        } else {
            categories = categoryRepository.findAll();
        }

        return categories.stream()
                .sorted(Comparator.comparing(Category::getSortOrder))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getCategoryTree(String type) {
        List<Category> categories = (type != null)
                ? categoryRepository.findByType(type)
                : categoryRepository.findAll();

        // Build map by parentId
        Map<String, List<Category>> childrenMap = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? "" : c.getParentId()));

        // Get root categories
        List<Category> roots = categories.stream()
                .filter(c -> c.getParentId() == null)
                .sorted(Comparator.comparing(Category::getSortOrder))
                .collect(Collectors.toList());

        // Build tree recursively
        return roots.stream()
                .map(root -> buildTree(root, childrenMap))
                .collect(Collectors.toList());
    }

    private CategoryResponse buildTree(Category category, Map<String, List<Category>> childrenMap) {
        CategoryResponse response = toResponse(category);

        List<Category> children = childrenMap.getOrDefault(category.getId(), Collections.emptyList());
        List<CategoryResponse> childResponses = children.stream()
                .sorted(Comparator.comparing(Category::getSortOrder))
                .map(child -> buildTree(child, childrenMap))
                .collect(Collectors.toList());
        response.setChildren(childResponses);

        return response;
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .level(category.getLevel())
                .type(category.getType())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .children(new ArrayList<>())
                .build();
    }
}
