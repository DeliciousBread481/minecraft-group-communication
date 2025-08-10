package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SolutionStepMapper {
    void insert(SolutionStep step);
    void update(SolutionStep step);
    void delete(Long stepId);
    void deleteBySolutionId(String solutionId);
    List<SolutionStep> findStepsBySolutionId(String solutionId);
    List<SolutionStep> findStepsBySolutionIds(@Param("solutionIds") List<String> solutionIds);
}