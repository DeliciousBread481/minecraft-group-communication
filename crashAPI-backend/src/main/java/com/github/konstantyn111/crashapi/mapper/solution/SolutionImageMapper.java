// SolutionImageMapper.java
package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SolutionImageMapper {
    @Insert("INSERT INTO solution_images (solution_id, image_order, image_url) " +
            "VALUES (#{solutionId}, #{imageOrder}, #{imageUrl})")
    void insert(SolutionImage image);

    @Delete("DELETE FROM solution_images WHERE solution_id = #{solutionId}")
    void deleteBySolutionId(String solutionId);

    @Select("SELECT * FROM solution_images WHERE solution_id = #{solutionId} ORDER BY image_order")
    List<SolutionImage> findBySolutionId(String solutionId);
}