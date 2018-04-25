package com.krly.transceiver;

import com.krly.api.comsumer.AbstractDeviceProxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UDPTransceiver {
    private final String host;
    private final int port;
    private final AbstractDeviceProxy abstractDeviceProxy;
    private boolean isRunning;

    public UDPTransceiver(String host, int port, AbstractDeviceProxy abstractDeviceProxy) {
        this.host = host;
        this.port = port;

        this.abstractDeviceProxy = abstractDeviceProxy;
    }

    public synchronized void start() throws Exception {
        if (isRunning)
            throw new Exception("UDP Transceiver is already running");

        isRunning = true;

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                        byte[] data = new byte[msg.content().readableBytes()];
                        msg.content().readBytes(data);

                        abstractDeviceProxy.onChannelRead(data, ctx.channel(), host, port);

                        System.out.println(msg.content());
                    }
                })
                .bind(host, port);
    }
}
