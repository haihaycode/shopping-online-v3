package com.phananhtai.shoppingOnline_service;

import com.phananhtai.shoppingOnline_service.config.RememberListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShoppingOnlineServiceApplication {
	@Bean
	public ServletListenerRegistrationBean<RememberListener> sessionListener() {
		return new ServletListenerRegistrationBean<>(new RememberListener());
	}
	public static void main(String[] args) {
		SpringApplication.run(ShoppingOnlineServiceApplication.class, args);
	}

}
