package com.cts.transaction_payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class TransactionPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionPaymentServiceApplication.class, args);
		System.out.println("worked");
	}

	@Bean
	public feign.Logger.Level feignLoggerLevel() {
		return feign.Logger.Level.FULL;

	}
}
