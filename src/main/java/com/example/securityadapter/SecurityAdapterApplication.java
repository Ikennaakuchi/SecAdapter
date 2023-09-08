package com.example.securityadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SecurityAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityAdapterApplication.class, args);
	}

}
