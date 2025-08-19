package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userMapper.findByUsername(username);

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));

        // ✅ 修改：只取角色名集合
        Set<String> roleNames = userMapper.findRolesByUserId(user.getId());

        return new CustomUserDetails(user, roleNames);
    }
}
