package com.krly.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.krly.api.IMessageHandler;
import com.krly.api.Message;

@Service
public class MessageHandlerImpl implements IMessageHandler {
    @Override
    public void onMessage(Message message) {
        System.out.println("onMessage called");
    }
}
