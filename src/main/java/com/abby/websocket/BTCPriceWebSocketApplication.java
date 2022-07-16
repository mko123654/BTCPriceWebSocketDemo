package com.abby.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BTCPriceWebSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(BTCPriceWebSocketApplication.class, args);
	}
}
