package com.astromyllc.shootingstar.adminpta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminPTA {
    public static void main(String[] args) {
        SpringApplication.run(AdminPTA.class,args);
    }
}
