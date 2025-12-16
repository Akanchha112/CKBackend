package com.example.cloudBalance.cloudBalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CloudBalanceApplication {

	public static void main(String[] args) {
        SpringApplication.run(CloudBalanceApplication.class, args);
	}
}
