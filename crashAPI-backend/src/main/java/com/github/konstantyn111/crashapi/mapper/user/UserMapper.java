package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface UserMapper {

    Set<String> findRolesByUserId(@Param("userId") Long userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void save(User user);

    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken, @Param("expiryDate") Date expiryDate);

    boolean hasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    void updateUserInfo(User user);

    Optional<User> findById(Long id);

    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    List<User> findAllWithRolesPaged(@Param("offset") int offset, @Param("limit") int limit);

    long countAllUsers();

    void removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
