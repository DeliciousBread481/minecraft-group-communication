package com.github.konstantyn111.crashapi.config;

import com.github.konstantyn111.crashapi.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类，定义应用程序的安全策略。
 * <p>
 * 主要配置包括：
 * 1. 安全过滤器链的定义
 * 2. 密码编码器的设置
 * 3. 认证管理器的配置
 * 4. JWT认证过滤器的集成
 * </p>
 * <p>
 * 启用基于令牌的无状态认证机制。
 * </p>
 */
@Configuration
@EnableWebSecurity  // 启用Spring Security的Web安全支持
@RequiredArgsConstructor
public class SecurityConfig {

    // 自定义用户详情服务，用于加载用户信息
    private final CustomUserDetailsService userDetailsService;

    // JWT认证过滤器，处理请求的令牌验证
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链，定义应用程序的安全规则。
     * <p>
     * 主要配置：
     * 1. 禁用CSRF保护（适用于无状态API）
     * 2. 设置请求授权规则：
     *    - 认证相关API允许匿名访问
     *    - 其他所有请求需要认证
     * 3. 配置无状态会话管理
     * 4. 添加JWT认证过滤器
     * </p>
     *
     * @param http HttpSecurity配置对象
     * @return 配置好的安全过滤器链
     * @throws Exception 如果配置过程中发生错误
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（REST API通常不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置请求授权
                .authorizeHttpRequests(authorize -> authorize
                        // 认证相关端点允许所有访问
                        .requestMatchers("/api/auth/**").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 配置会话管理为无状态（不使用session）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 在UsernamePasswordAuthenticationFilter前添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置密码编码器Bean。
     * <p>
     * 使用BCrypt强哈希算法进行密码加密。
     * </p>
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器Bean。
     * <p>
     * 设置认证时使用的：
     * 1. 用户详情服务（从数据源加载用户）
     * 2. 密码编码器（验证密码哈希）
     * </p>
     *
     * @param http HttpSecurity对象，用于获取AuthenticationManagerBuilder
     * @return 配置好的认证管理器
     * @throws Exception 如果配置过程中发生错误
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 获取AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // 配置用户详情服务和密码编码器
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }
}