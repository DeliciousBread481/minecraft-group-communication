package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SolutionStepMapper {

    @Insert("INSERT INTO solution_steps (solution_id, step_order, content) " +
            "VALUES (#{solutionId}, #{stepOrder}, #{content})")
    void insert(SolutionStep step);

    @Delete("DELETE FROM solution_steps WHERE solution_id = #{solutionId}")
    void deleteBySolutionId(String solutionId);

    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "solutionId", column = "solution_id"),
            @Result(property = "stepOrder", column = "step_order"),
            @Result(property = "content", column = "content")
    })
    @Select("SELECT * FROM solution_steps WHERE solution_id = #{solutionId} ORDER BY step_order")
    List<SolutionStep> findBySolutionId(String solutionId);
}