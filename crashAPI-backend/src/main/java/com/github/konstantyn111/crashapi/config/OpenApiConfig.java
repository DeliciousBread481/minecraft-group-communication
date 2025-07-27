package com.github.konstantyn111.crashapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//用不了不兼容，留着好看
@Configuration
public class OpenApiConfig {

    // 全局安全方案
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crash API 系统")
                        .version("1.0.0")
                        .description("Crash API 系统文档")
                        .contact(new Contact()
                                .name("技术支持")
                                .url("https://github.com/konstantyn111")
                                .email("2247380761@qq.com"))
                        .license(new License()
                                .name("使用许可")
                                .url("/license")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    // 开发者管理API分组
    @Bean
    public GroupedOpenApi developerApi() {
        return GroupedOpenApi.builder()
                .group("developer-management")
                .pathsToMatch("/api/developer/**")
                .displayName("开发者管理")
                .build();
    }

    // 管理员管理API分组
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-management")
                .pathsToMatch("/api/admin/**")
                .displayName("管理员管理")
                .build();
    }

    // 用户管理API分组
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-management")
                .pathsToMatch("/api/user/**")
                .displayName("用户管理")
                .build();
    }
}