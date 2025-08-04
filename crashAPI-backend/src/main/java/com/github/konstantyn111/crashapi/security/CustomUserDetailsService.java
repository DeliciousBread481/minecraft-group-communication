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
 * 自定义用户详情服务
 * <p>
 * 实现Spring Security的UserDetailsService接口，提供用户认证信息的加载功能。
 * 通过用户名查询用户信息并转换为Spring Security所需的认证详情对象。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * 根据用户名加载用户认证详情
     * <p>
     * 从数据库中查询用户信息，若用户不存在则抛出UsernameNotFoundException。
     * 将用户实体包装为CustomUserDetails对象供Spring Security使用。
     * </p>
     *
     * @param username 要加载的用户名
     * @return 用户认证详情对象
     * @throws UsernameNotFoundException 当用户名对应的用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userMapper.findByUsername(username);

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(user);
    }
}