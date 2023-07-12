package com.mw.security;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SecurityApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry
//						.addMapping("/api/v1/auth")
//						.allowedOrigins("http://localhost:3000")
//						.exposedHeaders(CorsConfiguration.ALL)
//
//				;
//			}
//		};
//	}

}
