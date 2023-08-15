package com.astromyllc.shootingstar.setup;

import com.astromyllc.shootingstar.setup.model.*;
import com.astromyllc.shootingstar.setup.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class Setup {
	public static void main(String[] args) {
		SpringApplication.run(Setup.class, args);
	}
}
