package com.github.konstantyn111.crashapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.konstantyn111.crashapi.mapper")
public class CrashApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrashApiApplication.class, args);
    }

}
