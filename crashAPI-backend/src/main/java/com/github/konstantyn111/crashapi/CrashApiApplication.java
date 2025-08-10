package com.github.konstantyn111.crashapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import java.time.format.DateTimeFormatter;

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
        return args -> {
            int port = getServerPort();
            String time = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            log.info("""
                    
                      (  )   (   )  )
                         ) (   )  (  (
                         ( )  (    ) )
                         _____________
                        <_____________> ___
                        |             |/ _ \\
                        |   ABSTRACT  | | | |
                        |   LAUNCH    |_| | |
                        |___PORT:{}___|\\___/
                        |__TIME:{}__|
                          :::       :::
                         '###'     '###'
                         #####     #####
                    """,
                    String.format("%5d", port),
                    time
            );
        };
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