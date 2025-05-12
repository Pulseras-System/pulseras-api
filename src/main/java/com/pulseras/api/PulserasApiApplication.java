package com.pulseras.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PulserasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PulserasApiApplication.class, args);
	}
}
