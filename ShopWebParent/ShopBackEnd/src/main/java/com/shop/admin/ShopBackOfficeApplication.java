package com.shop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan({ "com.shop.common.entity", "com.shop.admin.user" })
public class ShopBackOfficeApplication {

    public static void main(String[] args) {
	SpringApplication.run(ShopBackOfficeApplication.class, args);
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
	return new WebMvcConfigurer() {
	    public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedMethods("*")
			.allowedOrigins("http://localhost:3000");
	    }
	};
    }
}
