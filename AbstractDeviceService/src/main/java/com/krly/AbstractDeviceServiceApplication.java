package com.krly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AbstractDeviceServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(AbstractDeviceServiceApplication.class, args);

		String zkAddress = ctx.getEnvironment().getProperty("app.zkAddress");

		//
		TCPTransceiverMonitor tcpTransceiverMonitor = TCPTransceiverMonitor.getInstance();
		tcpTransceiverMonitor.start(zkAddress);
	}
}
