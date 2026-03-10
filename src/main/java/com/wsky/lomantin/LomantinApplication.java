package com.wsky.lomantin;

import com.wsky.lomantin.config.PexelsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PexelsProperties.class)
public class LomantinApplication {

    public static void main(String[] args) {
        SpringApplication.run(LomantinApplication.class, args);
    }
}