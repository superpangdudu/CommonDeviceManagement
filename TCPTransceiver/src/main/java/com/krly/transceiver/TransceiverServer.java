package com.krly.transceiver;

import com.krly.api.comsumer.AbstractDeviceProxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class TransceiverServer {
    private final String host;
    private final int port;
    private final AbstractDeviceProxy abstractDeviceProxy;
    private boolean isRunning;

    public TransceiverServer(String host, int port, AbstractDeviceProxy abstractDeviceProxy) {
        this.host = host;
        this.port = port;

        this.abstractDeviceProxy = abstractDeviceProxy;
    }

    public synchronized void start() throws Exception {
        if (isRunning)
            throw new Exception("Transceiver is already running");

        isRunning = true;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                            ch.pipeline().addLast(new BasicInboundHandler(abstractDeviceProxy, host, port));
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
