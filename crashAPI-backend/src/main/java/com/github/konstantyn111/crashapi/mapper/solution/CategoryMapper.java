package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    void insert(Category category);
    void update(Category category);
    void delete(String categoryId);
    Optional<Category> findById(String categoryId);
    List<Category> findAll();
}