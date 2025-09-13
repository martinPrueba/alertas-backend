package com.kim21.alertas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlertasApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertasApplication.class, args);

		String usuario = System.getProperty("user.name");
		System.out.println("Usuario actual: " + usuario);

	}

}
