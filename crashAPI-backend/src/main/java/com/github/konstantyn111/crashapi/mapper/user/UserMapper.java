package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
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
    void updateUserInfo(User user);
    Optional<User> findByIdWithRoles(@Param("id") Long id);
    Optional<Role> findRoleByName(@Param("roleName") String roleName);
    Optional<User> findById(@Param("id") Long id);
    List<User> findAllWithRolesPaged(@Param("limit") int limit, @Param("offset") int offset);
    long countAllUsers();

    boolean hasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}