package com.carRental.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfiguration {

	private final CustomJwtFilter customJwtFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(org.springframework.security.config.Customizer.withDefaults()); // Enable CORS
		http.csrf(csrf -> csrf.disable());

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/api/auth/**","/auth-controller/login")
				.permitAll()

				// ADMIN
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/locations/**").hasRole("ADMIN")

				// CARS
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/cars/**").permitAll()
				.requestMatchers("/api/cars/**").hasRole("ADMIN")

				// BOOKINGS & PAYMENTS
				.requestMatchers("/api/bookings/**").hasAnyRole("ADMIN", "CUSTOMER")
				.requestMatchers("/api/payments/**").hasRole("CUSTOMER")

				.anyRequest().authenticated());

		http.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
