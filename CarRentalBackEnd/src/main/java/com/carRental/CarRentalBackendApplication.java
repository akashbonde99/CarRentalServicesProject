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
//one of the annotations - @SpringBootConfiguration => it's Spring boot config class , where you can add @Bean methods to declare spring beans
public class CarRentalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalBackendApplication.class, args);
	}

	// configure ModelMapper class as a spring bean
	@Bean // exactly equivalent to - <bean id......../>
	ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration() // get default config
				.setPropertyCondition(Conditions.isNotNull()) // transfer only not null props from src-> dest
				.setMatchingStrategy(MatchingStrategies.STRICT);// transfer the props form src -> dest which match by
																// name & data type

		return mapper;
	}

	// configure password encoder bean
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
