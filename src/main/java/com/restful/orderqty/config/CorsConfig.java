package com.restful.orderqty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restful.orderqty.utils.ILConstants;

@Configuration 
public class CorsConfig { 
	@Bean 
	public WebMvcConfigurer corsConfigurer() { 
		return new WebMvcConfigurer() { 
			@Override public void addCorsMappings(CorsRegistry registry) { 
				registry.addMapping("/**").
				allowedOrigins(ILConstants.ANGULAR_URL).
				allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").
				allowedHeaders("*").
				allowCredentials(true); 
			} 
		};
	} 
}
