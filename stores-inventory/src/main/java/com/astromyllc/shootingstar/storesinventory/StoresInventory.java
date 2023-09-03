package com.astromyllc.shootingstar.storesinventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StoresInventory {
    public static void main(String[] args) {
        SpringApplication.run(StoresInventory.class,args);
    }
}
