package com.jc.professional_challenge_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // emails are sent in the background so they don't slow down the response
public class ProfessionalChallengeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfessionalChallengeApiApplication.class, args);
	}

}
