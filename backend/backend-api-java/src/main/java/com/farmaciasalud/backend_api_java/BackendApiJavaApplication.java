package com.farmaciasalud.backend_api_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.farmaciasalud")
@EnableJpaRepositories("com.farmaciasalud")
@ComponentScan("com.farmaciasalud")
public class BackendApiJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApiJavaApplication.class, args);
	}

}
