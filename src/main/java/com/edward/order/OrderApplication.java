package com.edward.order;

import com.edward.order.config.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		EnvLoader.load();

		SpringApplication.run(OrderApplication.class, args);
	}

}
