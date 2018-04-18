package com.krly.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.krly.TCPTransceiverMonitor;
import com.krly.api.IAbstractDevice;
import com.krly.api.comsumer.MessageHandlerProxy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;


@Service
public class AbstractDeviceImpl implements IAbstractDevice {
    @Autowired
    private MessageHandlerProxy messageHandlerProxy;

    // TODO to use jedis pool
    private Jedis jedis;

    public AbstractDeviceImpl() {
        jedis = new Jedis("127.0.0.1", 6379);
    }

    //===================================================================================
    @Override
    public void onMessage(byte[] data, String host, int port) {
        //messageHandlerProxy.onMessage();
    }

    @Override
    public void onClosed(int token) {
        jedis.hdel(String.valueOf(token));
    }

    @Override
    public int write(int token, byte[] message) {
        // 1. get transceiver which the device with the token connected
        // 2. transmit the message to the transceiver

        String transceiverHost = jedis.hget(String.valueOf(token), "transceiverHost");
        if (StringUtils.isEmpty(transceiverHost))
            return -1;

        String[] parts = transceiverHost.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        TCPTransceiverMonitor transceiverManagement = TCPTransceiverMonitor.getInstance();
        Channel channel = transceiverManagement.getTransceiverChannel(host, port);
        if (channel == null)
            return -1;

        ByteBuf buf = Unpooled.buffer(message.length);
        buf.writeBytes(message);
        channel.writeAndFlush(buf);

        return 0;
    }
}
