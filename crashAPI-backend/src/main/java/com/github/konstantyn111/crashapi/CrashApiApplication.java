package com.github.konstantyn111.crashapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@MapperScan("com.github.konstantyn111.crashapi.mapper")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class CrashApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrashApiApplication.class, args);
    }

}
