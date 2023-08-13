package com.astromyllc.shootingstar.onlineapplication;

import com.astromyllc.shootingstar.onlineapplication.controller.ApplicationController;
import com.astromyllc.shootingstar.onlineapplication.model.Applications;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"com.astromyllc.shootingstar.onlineapplication"})
public class OnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineApplication.class, args);
	}

}
