package com.ssafy.yogiattacku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YogiattackuApplication {

	public static void main(String[] args) {
		SpringApplication.run(YogiattackuApplication.class, args);
	}

}
