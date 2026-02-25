package com.blog.repository;

import com.blog.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByType(String type);

    List<Category> findByTypeAndLevel(String type, Integer level);

    List<Category> findByParentId(String parentId);

    List<Category> findByParentIdIsNull();

    List<Category> findByTypeOrderBySortOrderAsc(String type);

    long countByParentId(String parentId);
}
