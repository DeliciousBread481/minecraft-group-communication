package com.github.konstantyn111.crashapi.config;

import com.github.konstantyn111.crashapi.security.CustomUserDetailsService;
import com.github.konstantyn111.crashapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器，处理每个请求的JWT认证。
 * <p>
 * 该过滤器在每个请求中执行以下操作：
 * 1. 检查Authorization请求头是否存在有效的Bearer令牌
 * 2. 验证JWT令牌的有效性
 * 3. 加载令牌对应的用户详情
 * 4. 设置认证信息到Spring Security上下文
 * </p>
 * <p>
 * 继承自{@link OncePerRequestFilter}确保每个请求只执行一次过滤逻辑。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT服务，用于令牌解析和验证
    private final JwtService jwtService;

    // 用户详情服务，用于加载用户信息
    private final CustomUserDetailsService userDetailsService;

    /**
     * 核心过滤方法，处理JWT认证逻辑。
     * <p>
     * 处理流程：
     * 1. 从Authorization头提取JWT令牌
     * 2. 验证令牌格式（Bearer类型）
     * 3. 从令牌中提取用户名
     * 4. 如果安全上下文未认证，加载用户详情
     * 5. 验证令牌有效性
     * 6. 创建认证令牌并设置到安全上下文
     * </p>
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 如果发生servlet相关错误
     * @throws IOException 如果发生I/O错误
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 从请求头获取Authorization字段
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 检查Authorization头是否有效
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            // 无效的认证头，继续过滤器链
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT令牌（移除"Bearer "前缀）
        jwt = authHeader.substring(7);
        // 从令牌中提取用户名
        username = jwtService.extractUsername(jwt);

        // 当用户名有效且当前请求未认证时进行认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户详情
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证令牌有效性
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 创建认证令牌（凭证为null，因为JWT已包含认证信息）
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 添加请求详情
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 设置认证信息到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}