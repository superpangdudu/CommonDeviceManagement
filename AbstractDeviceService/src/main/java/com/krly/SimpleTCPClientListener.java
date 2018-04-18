package com.krly;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Created by Administrator on 2018/4/16.
 */
public interface SimpleTCPClientListener {
    void onConnected(Channel channel, String host, int port);
    void onClosed(Channel channel, String host, int port);
    void onMessage(Channel channel, String host, int port, byte[] data);
}
