package com.krly;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class SimpleTCPClient {
    private String host;
    private int port;
    private SimpleTCPClientListener listener;

    public SimpleTCPClient(String host, int port, SimpleTCPClientListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    public void start(EventLoopGroup eventLoopGroup) {
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                            ch.pipeline().addLast(new SimpleTCPClientHandler(host, port, listener));
                        }
                    });

            ChannelFuture future = bootstrap.connect();
            //future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
