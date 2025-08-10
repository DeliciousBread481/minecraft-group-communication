package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SolutionImageMapper {
    void insert(SolutionImage image);
    void update(SolutionImage image);
    void delete(Long imageId);
    void deleteBySolutionId(String solutionId);
    List<SolutionImage> findImagesBySolutionId(String solutionId);
    List<SolutionImage> findImagesBySolutionIds(@Param("solutionIds") List<String> solutionIds);
}