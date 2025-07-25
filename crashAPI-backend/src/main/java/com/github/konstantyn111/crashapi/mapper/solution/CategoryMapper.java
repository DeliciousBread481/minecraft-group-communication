package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories WHERE id = #{categoryId}")
    Optional<Category> findById(String categoryId);

    @Select("SELECT * FROM categories")
    List<Category> findAll();
}