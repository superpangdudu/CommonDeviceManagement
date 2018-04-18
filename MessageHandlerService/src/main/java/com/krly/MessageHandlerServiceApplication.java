package com.krly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MessageHandlerServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerServiceApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(MessageHandlerServiceApplication.class, args);
	}
}
