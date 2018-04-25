package com.krly;

import com.krly.api.comsumer.AbstractDeviceProxy;
import com.krly.transceiver.TransceiverServer;
import com.krly.transceiver.UDPTransceiver;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class TcpTransceiverApplication {
	private static final Logger logger = LoggerFactory.getLogger(TcpTransceiverApplication.class);

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(TcpTransceiverApplication.class, args);
		logger.info("Spring started");

		//
		String zkAddress = "127.0.0.1:2181";
		String host = "127.0.0.1";
		int port = 15110;

		// parsing the startup parameters
		// the first one should be the listening address of transceiver, format as 'host:port'
		// the second one if exists should be the ZooKeeper address
		if (args != null && args.length >= 1) {
			String[] parts = args[0].split(":");
			host = parts[0];
			port = Integer.parseInt(parts[1]);
		}

		if (args != null && args.length >= 2)
			zkAddress = args[1];

		logger.info("Transceiver host: {}", host);
		logger.info("Transceiver port: {}", port);

		//
		AbstractDeviceProxy abstractDeviceProxy = ctx.getBean(AbstractDeviceProxy.class);
		logger.info("ChannelEventHandler instance got: {}", abstractDeviceProxy);

		// register transceiver on ZooKeeper
		// path: /transceiver/host:port
		logger.info("CuratorFramework starting");
		CuratorFramework curatorFramework =
				CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 10));
		curatorFramework.start();

		String rootPath = "/transceiver";
		String childPath = rootPath + "/" + host + ":" + port;

		Stat stat = curatorFramework.checkExists().forPath(rootPath);
		if (stat == null) {
			logger.info("The root path on ZooKeeper for Transceiver not created, create it automatically");
			curatorFramework.create()
					.creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT)
					.forPath(rootPath);
		}

		logger.info("Create the ephemeral path for Transceiver: {}", childPath);
		curatorFramework.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(childPath);

		//
		logger.info("Starting UDP TransceiverServer, host = {}, port = {}", host, port);
		UDPTransceiver udpTransceiver = new UDPTransceiver(host, port, abstractDeviceProxy);
		udpTransceiver.start();

		//
		logger.info("Starting TCP TransceiverServer, host = {}, port = {}", host, port);
		TransceiverServer transceiverServer = new TransceiverServer(host, port, abstractDeviceProxy);
		transceiverServer.start();
	}
}
