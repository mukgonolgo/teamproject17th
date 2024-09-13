// com.test.project.config.JpaConfig.java
package com.test.project.eat_friends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GuestBookApplication {
	public static void main(String[] args) {
		SpringApplication.run(GuestBookApplication.class, args);
	}
}
