package com.krly.transceiver;

import com.krly.api.comsumer.AbstractDeviceProxy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class BasicInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private AbstractDeviceProxy abstractDeviceProxy;
    private String host;
    private int port;

    BasicInboundHandler(AbstractDeviceProxy abstractDeviceProxy, String host, int port) {
        this.abstractDeviceProxy = abstractDeviceProxy;
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive: " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive: " + ctx);
        abstractDeviceProxy.onChannelClosed(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int bytes = msg.readableBytes();
        if (bytes < 4) // the token takes 4 bytes
            return;

        byte[] data = new byte[bytes];
        msg.readBytes(data);

        abstractDeviceProxy.onChannelRead(data, ctx.channel(), host, port);
    }
}
