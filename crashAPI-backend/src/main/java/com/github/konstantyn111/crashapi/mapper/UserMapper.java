package com.github.konstantyn111.crashapi.mapper;

import com.github.konstantyn111.crashapi.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(@Param("email") String email);
    void save(User user);
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    void updateRefreshToken(
            @Param("id") Long id,
            @Param("refreshToken") String refreshToken,
            @Param("expiryDate") Date expiryDate
    );
}