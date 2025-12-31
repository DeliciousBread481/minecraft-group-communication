package com.github.konstantyn111.crashapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig {

    @Value("${security.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 设置允许的源
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // 设置允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // 设置允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", "Authorization", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // 设置暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        
        // 允许凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        // 注册配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}