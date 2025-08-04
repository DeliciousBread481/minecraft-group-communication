package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.User;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * <p>
 * 提供用户信息的增删改查操作，包括用户基本信息、角色管理和令牌管理。
 * </p>
 */
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * <p>
     * 通过用户名精确匹配查询用户实体。
     * </p>
     *
     * @param username 用户名
     * @return 包含用户实体的Optional对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * <p>
     * 通过邮箱地址精确匹配查询用户实体。
     * </p>
     *
     * @param email 邮箱地址
     * @return 包含用户实体的Optional对象
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    /**
     * 保存新用户记录
     * <p>
     * 插入新用户记录到数据库，自动生成主键ID。
     * </p>
     *
     * @param user 要保存的用户实体
     */
    @Insert("INSERT INTO users (username, email, password, nickname, avatar, created_at, updated_at, enabled, refresh_token, refresh_token_expiry) " +
            "VALUES (#{username}, #{email}, #{password}, #{nickname}, #{avatar}, #{createdAt}, #{updatedAt}, #{enabled}, #{refreshToken}, #{refreshTokenExpiry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(User user);

    /**
     * 更新用户刷新令牌
     * <p>
     * 修改用户的刷新令牌和有效期。
     * </p>
     *
     * @param id 用户ID
     * @param refreshToken 新的刷新令牌
     * @param expiryDate 新的令牌有效期
     */
    @Update("UPDATE users SET refresh_token = #{refreshToken}, refresh_token_expiry = #{expiryDate} WHERE id = #{id}")
    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken, @Param("expiryDate") Date expiryDate);

    /**
     * 根据ID查询用户（含角色信息）
     * <p>
     * 通过用户ID查询用户实体及其关联的角色信息。
     * </p>
     *
     * @param id 用户ID
     * @return 包含用户实体（含角色）的Optional对象
     */
    @Select("SELECT u.*, r.id AS role_id, r.name AS role_name, r.description AS role_description " +
            "FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id LEFT JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.id = #{id}")
    Optional<User> findByIdWithRoles(Long id);

    /**
     * 验证用户是否拥有指定角色
     * <p>
     * 检查用户角色关联表中是否存在指定角色记录。
     * </p>
     *
     * @param userId 用户ID
     * @param roleName 角色名称
     * @return 用户拥有该角色返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM user_roles ur JOIN roles r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.name = #{roleName}")
    boolean hasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    /**
     * 更新用户基本信息
     * <p>
     * 修改用户的邮箱、昵称、头像和密码信息。
     * </p>
     *
     * @param user 包含更新字段的用户实体
     */
    @Update("UPDATE users SET email = #{email}, nickname = #{nickname}, avatar = #{avatar}, " +
            "password = #{password}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateUserInfo(User user);

    /**
     * 根据ID查询用户
     * <p>
     * 通过用户ID查询用户基本信息（不含角色）。
     * </p>
     *
     * @param id 用户ID
     * @return 包含用户实体的Optional对象
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    /**
     * 为用户添加角色
     * <p>
     * 在用户角色关联表中添加新记录。
     * </p>
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 分页查询所有用户（含角色信息）
     * <p>
     * 查询所有用户及其关联的角色信息，结果按分页参数返回。
     * </p>
     *
     * @param offset 查询偏移量
     * @param limit 每页记录数
     * @return 用户实体列表（含角色信息）
     */
    @Select("SELECT u.*, r.id AS role_id, r.name AS role_name, r.description AS role_description " +
            "FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "ORDER BY u.id LIMIT #{limit} OFFSET #{offset}")
    List<User> findAllWithRolesPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计所有用户数量
     * <p>
     * 查询用户表中的总记录数。
     * </p>
     *
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    long countAllUsers();

    /**
     * 移除用户的角色
     * <p>
     * 从用户角色关联表中删除指定记录。
     * </p>
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}