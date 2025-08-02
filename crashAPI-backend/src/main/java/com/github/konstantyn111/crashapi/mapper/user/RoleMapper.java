package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RoleMapper {
    Optional<Role> findByName(@Param("name") String name);
}