package com.github.konstantyn111.crashapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于自定义Spring MVC设置。
 * <p>
 * 主要配置跨域资源共享(CORS)策略，允许指定的前端应用访问后端API。
 * </p>
 * <p>
 * 通过实现{@link WebMvcConfigurer}接口，可以自定义Spring MVC的配置。
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 允许的跨域来源列表，从配置文件中注入。
     * <p>
     * 配置项示例：security.allowed-origins=http://localhost:3000
     * </p>
     */
    @Value("${security.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * 配置跨域资源共享(CORS)策略。
     * <p>
     * 为所有端点(/**)设置以下规则：
     * 1. 允许指定的来源访问
     * 2. 允许常见的HTTP方法
     * 3. 允许所有请求头
     * 4. 允许携带凭证（如cookies）
     * 5. 设置预检请求缓存时间
     * </p>
     *
     * @param registry CORS注册器，用于配置跨域规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}