package com.example.watermarkService;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class WatermarkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermarkServiceApplication.class, args);
	}

}
