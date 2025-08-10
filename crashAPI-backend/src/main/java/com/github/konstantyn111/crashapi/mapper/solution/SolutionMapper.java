package com.github.konstantyn111.crashapi.mapper.solution;

import com.github.konstantyn111.crashapi.entity.solution.Solution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SolutionMapper {
    void insert(Solution solution);
    void update(Solution solution);
    void delete(String solutionId);
    Optional<Solution> findById(String solutionId);

    List<Solution> findByCreator(@Param("creatorId") Long creatorId,
                                 @Param("status") String status,
                                 @Param("limit") int limit,
                                 @Param("offset") int offset);

    int countByCreator(@Param("creatorId") Long creatorId,
                       @Param("status") String status);

    List<Solution> findByStatus(@Param("status") String status,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

    int countByStatus(@Param("status") String status);

    List<Solution> findPublishedSolutions(@Param("offset") int offset,
                                          @Param("pageSize") int pageSize);

    int countPublishedSolutions();

}