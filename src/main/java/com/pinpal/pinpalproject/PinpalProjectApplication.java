package com.pinpal.pinpalproject;

import com.pinpal.pinpalproject.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PinpalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PinpalProjectApplication.class, args);
    }

}
