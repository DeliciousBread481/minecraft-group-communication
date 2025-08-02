package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 自定义用户详情服务，实现Spring Security的{@link UserDetailsService}接口。
 * <p>
 * 该类负责根据用户名加载用户信息，并将其转换为Spring Security认证所需的{@link UserDetails}对象。
 * </p>
 * <p>
 * 使用{@link UserMapper}从数据源中检索用户信息，并包装为{@link CustomUserDetails}对象。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * 用户数据访问接口，用于从数据源查询用户信息。
     */
    private final UserMapper userMapper;

    /**
     * 根据用户名加载用户详情信息。
     * <p>
     * 此方法是Spring Security认证流程的核心部分，在用户登录时被调用。
     * </p>
     *
     * @param username 要加载的用户名（唯一标识）
     * @return 包含用户详情和权限的{@link UserDetails}对象
     * @throws UsernameNotFoundException 当指定用户名的用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据源查询用户信息
        Optional<User> userOptional = userMapper.findByUsername(username);

        // 如果用户不存在，抛出Spring Security标准异常
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));

        // 将用户实体包装为Spring Security所需的UserDetails实现
        return new CustomUserDetails(user);
    }
}