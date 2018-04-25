package com.krly;

import com.krly.api.order.Order;
import com.krly.dao.mapper.OrderMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(OrderServiceApplication.class, args);

		OrderMapper mapper = ctx.getBean(OrderMapper.class);
		mapper.selectByPrimaryKey(1);
	}
}
