package com.krly;

import com.krly.api.IMessageHandler;
import com.krly.api.comsumer.MessageHandlerProxy;
import com.krly.api.impl.AbstractDeviceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AbstractDeviceServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(AbstractDeviceServiceApplication.class, args);

		IMessageHandler messageHandler = ctx.getBean(MessageHandlerProxy.class);
		AbstractDeviceService.getInstance().setMessageHandler(messageHandler);

		String zkAddress = ctx.getEnvironment().getProperty("app.zkAddress");

		//
		TCPTransceiverMonitor tcpTransceiverMonitor = TCPTransceiverMonitor.getInstance();
		tcpTransceiverMonitor.start(zkAddress);
	}
}
