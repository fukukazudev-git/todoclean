package com.example.todoclean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.todoclean")

public class TodoCleanApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoCleanApplication.class, args);
	
	}

}
