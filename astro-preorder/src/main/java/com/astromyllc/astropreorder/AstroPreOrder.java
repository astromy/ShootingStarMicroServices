package com.astromyllc.astropreorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AstroPreOrder {

	public static void main(String[] args) {
		SpringApplication.run(AstroPreOrder.class, args);
	}

}
