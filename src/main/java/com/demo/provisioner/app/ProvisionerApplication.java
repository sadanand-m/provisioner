package com.demo.provisioner.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= "com.demo.*")

public class ProvisionerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProvisionerApplication.class, args);
	}

}
