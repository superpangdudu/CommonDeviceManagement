package com.krly.transceiver;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/4/17.
 */
public class ChannelHolder {
    private static volatile ChannelHolder INSTANCE = null;

    private ChannelHolder() {
    }

    public static ChannelHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (ChannelHolder.class) {
                if (INSTANCE == null)
                    INSTANCE = new ChannelHolder();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    private Map<Integer, Channel> tokenToChannelMap = new ConcurrentHashMap<>();
    private Map<Channel, Integer> channelToTokenMap = new ConcurrentHashMap<>();

    //===================================================================================
    public int getToken(Channel channel) {
        Integer token = channelToTokenMap.get(channel);
        if (token == null)
            return 0;
        return token;
    }

    public Channel getChannel(int token) {
        return tokenToChannelMap.get(token);
    }

    public void addChannel(int token, Channel channel) {
        tokenToChannelMap.put(token, channel);
        channelToTokenMap.put(channel, token);
    }

    public void removeChannel(int token) {
        Channel channel = tokenToChannelMap.remove(token);
        if (channel == null)
            return;
        channelToTokenMap.remove(channel);
    }

    public void removeChannel(Channel channel) {
        Integer token = channelToTokenMap.remove(channel);
        if (token == null)
            return;
        tokenToChannelMap.remove(token);
    }
}
