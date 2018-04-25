package com.krly.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.krly.TCPTransceiverMonitor;
import com.krly.api.IAbstractDevice;
import com.krly.api.comsumer.MessageHandlerProxy;
import com.krly.utils.db.JedisPoolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service
public class AbstractDeviceImpl implements IAbstractDevice {
    @Autowired
    private MessageHandlerProxy messageHandlerProxy;

    private JedisPool jedisPool = JedisPoolUtils.newJedisPoolInstance("127.0.0.1", 6379);

    public AbstractDeviceImpl() {
    }

    //===================================================================================
    @Override
    public void onMessage(byte[] data, String host, int port) {
        //messageHandlerProxy.onMessage();
    }

    @Override
    public void onClosed(int token) {
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(String.valueOf(token));
        jedis.close();
    }

    @Override
    public int write(int token, byte[] message) {
        // 1. get transceiver which the device with the token connected
        // 2. transmit the message to the transceiver

        Jedis jedis = jedisPool.getResource();
        String transceiverHost = jedis.hget(String.valueOf(token), "transceiverHost");
        jedisPool.close();

        if (StringUtils.isEmpty(transceiverHost))
            return -1;

        String[] parts = transceiverHost.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        //
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
