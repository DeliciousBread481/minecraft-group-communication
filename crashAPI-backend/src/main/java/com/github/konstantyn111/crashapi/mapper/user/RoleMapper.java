package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.Role;

import java.util.Optional;

public interface RoleMapper {

    Optional<Role> findByName(String name);
}
