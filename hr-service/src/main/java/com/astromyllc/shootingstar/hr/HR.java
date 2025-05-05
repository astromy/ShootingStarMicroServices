package com.astromyllc.shootingstar.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry

public class HR {
    public static void main(String[] args) {
            SpringApplication.run(HR.class, args);
    }
}
