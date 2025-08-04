package com.github.konstantyn111.crashapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@MapperScan("com.github.konstantyn111.crashapi.mapper")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class CrashApiApplication {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CrashApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CrashApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner startupNotifier() {
        return args -> log.info("""
                        
                        ===============================================================
                          应用启动成功!\s
                          CrashAPI 服务已就绪\s
                          当前访问地址不兼容，不可用!\t
                          访问地址: http://localhost:{}/swagger-ui.html\s
                          当前时间: {}\s
                        ===============================================================""",
                getServerPort(),
                java.time.LocalDateTime.now()
        );
    }

    private int getServerPort() {
        try {
            return Integer.parseInt(
                    System.getProperty("server.port",
                            System.getenv().getOrDefault("SERVER_PORT", "9090")
                    ));
        } catch (NumberFormatException e) {
            return 9090;
        }
    }
}