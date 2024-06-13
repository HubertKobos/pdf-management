package com.example.exctractionService;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ExctractionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExctractionServiceApplication.class, args);
	}

}
