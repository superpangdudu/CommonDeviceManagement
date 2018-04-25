package com.krly;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TCPTransceiverMonitor implements SimpleTCPClientListener {
    private static Logger logger = LoggerFactory.getLogger(TCPTransceiverMonitor.class);

    private static volatile TCPTransceiverMonitor INSTANCE = null;

    private TCPTransceiverMonitor() {
    }

    public static TCPTransceiverMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (TCPTransceiverMonitor.class) {
                if (INSTANCE == null)
                    INSTANCE = new TCPTransceiverMonitor();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    @Override
    public void onConnected(Channel channel, String host, int port) {
        logger.info("onConnected: {}, {}, {}", channel, host, port);

        String key = host + ":" + port;
        transceiverChannelMap.put(key, channel);
    }

    @Override
    public void onClosed(Channel channel, String host, int port) {
        logger.info("onClosed: {}, {}, {}", channel, host, port);

        String key = host + ":" + port;
        transceiverChannelMap.remove(key);
    }

    @Override
    public void onMessage(Channel channel, String host, int port, byte[] data) {

    }

    //===================================================================================
    public Channel getTransceiverChannel(String host, int port) {
        String key = host + ":" + port;
        return transceiverChannelMap.get(key);
    }

    //===================================================================================
    private Map<String, Channel> transceiverChannelMap = new ConcurrentHashMap<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private boolean isRunning = false;

    //===================================================================================
    public void start(String zkAddress) {
        if (isRunning == true)
            return;

        new Thread(() -> {
            CuratorFramework client =
                    CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 10));
            client.start();

            try {
                String rootPath = "/transceiver";
                Stat stat = client.checkExists().forPath(rootPath);
                if (stat == null) {
                    client.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(rootPath);
                }

                //
                PathChildrenCache cache = new PathChildrenCache(client, rootPath, true);
                cache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                            ChildData data = event.getData();
                            String path = data.getPath();
                            path = path.replace(rootPath + "/", "");
                            String[] args = path.split(":");

                            //
                            startTransceiverClient(args[0], Integer.parseInt(args[1]));

                        } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {

                        }
                    }
                });
                cache.start();

                //
                List<String> transceiverList = client.getChildren().forPath(rootPath);
                if (transceiverList != null) {
                    for (String childPath : transceiverList) {
                        String[] args = childPath.split(":");
                        String host = args[0];
                        int port = Integer.parseInt(args[1]);

                        startTransceiverClient(host, port);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

        isRunning = true;
    }

    private void startTransceiverClient(String host, int port) {
        SimpleTCPClient simpleTCPClient = new SimpleTCPClient(host, port, this);
        simpleTCPClient.start(eventLoopGroup);
    }
}
