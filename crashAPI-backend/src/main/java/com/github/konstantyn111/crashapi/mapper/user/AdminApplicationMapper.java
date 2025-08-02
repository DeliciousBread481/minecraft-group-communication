package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminApplicationMapper {
    void insert(AdminApplication application);
    void update(AdminApplication application);
    Optional<AdminApplication> findById(@Param("id") Long id);
    Optional<AdminApplication> findByIdWithUserInfo(@Param("id") Long id);
    boolean hasPendingApplication(@Param("userId") Long userId);
    List<AdminApplication> findPendingApplications(
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    List<AdminApplication> findPendingApplicationsWithUserInfo(
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    long countPendingApplications();
}