package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 角色数据访问接口
 * <p>
 * 提供角色信息的查询和存储操作。
 * </p>
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据角色名称查询角色信息
     * <p>
     * 通过角色名称精确匹配查询角色实体。
     * </p>
     *
     * @param name 角色名称（如ROLE_ADMIN）
     * @return 包含角色实体的Optional对象
     */
    @Select("SELECT id, name, description FROM roles WHERE name = #{name}")
    Optional<Role> findByName(String name);

    /**
     * 保存新角色记录
     * <p>
     * 插入新的角色记录到数据库，自动生成主键ID。
     * </p>
     *
     * @param role 要保存的角色实体
     */
    @Insert("INSERT INTO roles (name, description) VALUES (#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Role role);
}