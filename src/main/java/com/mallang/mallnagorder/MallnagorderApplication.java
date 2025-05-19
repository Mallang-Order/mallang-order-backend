package com.mallang.mallnagorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MallnagorderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallnagorderApplication.class, args);
	}

}
