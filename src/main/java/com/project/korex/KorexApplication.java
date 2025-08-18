package com.project.korex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KorexApplication {

    public static void main(String[] args) {
        SpringApplication.run(KorexApplication.class, args);
    }

}
