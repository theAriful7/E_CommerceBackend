package com.My.E_CommerceApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class
ECommerceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceAppApplication.class, args);
	}

}
