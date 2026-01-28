package com.carRental;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CarRentalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalBackendApplication.class, args);
	}

	
	@Bean 
	ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration() 
				.setPropertyCondition(Conditions.isNotNull()) 
				.setMatchingStrategy(MatchingStrategies.STRICT);

		return mapper;
	}

	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
