package com.nhnacademy.javamewarnifyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JavameWarnifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavameWarnifyServiceApplication.class, args);
    }

}
