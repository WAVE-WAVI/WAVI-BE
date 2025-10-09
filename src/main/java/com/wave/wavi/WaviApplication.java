package com.wave.wavi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class WaviApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaviApplication.class, args);
    }

}
