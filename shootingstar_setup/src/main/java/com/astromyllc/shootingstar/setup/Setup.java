package com.astromyllc.shootingstar.setup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Setup {
	public static void main(String[] args) {
		SpringApplication.run(Setup.class, args);
	}
}
