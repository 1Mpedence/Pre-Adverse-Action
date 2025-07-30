package com.harsh.pre_adverse_action.pre_adverse_action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PreAdverseActionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreAdverseActionApplication.class, args);
	}

}
