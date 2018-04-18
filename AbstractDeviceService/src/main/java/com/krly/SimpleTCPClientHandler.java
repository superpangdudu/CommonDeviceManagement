package com.krly;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SimpleTCPClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private String host;
    private int port;
    private SimpleTCPClientListener listener;

    public SimpleTCPClientHandler(String host, int port, SimpleTCPClientListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    //===================================================================================
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        listener.onConnected(ctx.channel(), host, port);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        listener.onClosed(ctx.channel(), host, port);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);

        listener.onMessage(ctx.channel(), host, port, data);
    }
}
