package com.krly.api.comsumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.krly.api.IMessageHandler;
import com.krly.api.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageHandlerProxy implements IMessageHandler {
    @Reference
    IMessageHandler messageHandler;

    @Override
    public void onMessage(Message message) {
        messageHandler.onMessage(message);
    }
}
