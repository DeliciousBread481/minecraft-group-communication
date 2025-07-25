// SolutionMapper.java
package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.Solution;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SolutionMapper {
    @Insert("INSERT INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by, created_at, updated_at) " +
            "VALUES (#{id}, #{categoryId}, #{title}, #{difficulty}, #{version}, #{description}, #{notes}, #{status}, #{createdBy}, #{createdAt}, #{updatedAt})")
    void insert(Solution solution);

    @Update("UPDATE solutions SET " +
            "category_id = #{categoryId}, " +
            "title = #{title}, " +
            "difficulty = #{difficulty}, " +
            "version = #{version}, " +
            "description = #{description}, " +
            "notes = #{notes}, " +
            "status = #{status}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Solution solution);

    @Delete("DELETE FROM solutions WHERE id = #{solutionId}")
    void delete(String solutionId);

    @Select("SELECT * FROM solutions WHERE id = #{solutionId}")
    Optional<Solution> findById(String solutionId);

    @Select("SELECT * FROM solutions WHERE created_by = #{creatorId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Solution> findByCreator(@Param("creatorId") Long creatorId,
                                 @Param("status") String status,
                                 @Param("limit") int limit,
                                 @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM solutions WHERE created_by = #{creatorId} " +
            "AND (#{status} IS NULL OR status = #{status})")
    int countByCreator(@Param("creatorId") Long creatorId, @Param("status") String status);

    @Select("SELECT * FROM solutions WHERE status = #{status} " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Solution> findByStatus(@Param("status") String status,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM solutions WHERE status = #{status}")
    int countByStatus(@Param("status") String status);
}