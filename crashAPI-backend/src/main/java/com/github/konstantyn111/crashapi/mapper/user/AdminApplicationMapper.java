package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface AdminApplicationMapper {

    void insert(AdminApplication application);

    boolean hasPendingApplication(Long userId);

    Optional<AdminApplication> findById(Long id);

    List<AdminApplication> findPendingApplicationsWithUserInfo(@Param("offset") int offset, @Param("limit") int limit);

    long countPendingApplications();

    void update(AdminApplication application);

    Optional<AdminApplication> findLatestByUserId(Long userId);
}
