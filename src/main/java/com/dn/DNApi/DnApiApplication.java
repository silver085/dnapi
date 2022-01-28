package com.dn.DNApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DnApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DnApiApplication.class, args);
	}

}
