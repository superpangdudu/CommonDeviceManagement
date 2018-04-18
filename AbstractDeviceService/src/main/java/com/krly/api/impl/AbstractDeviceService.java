package com.krly.api.impl;

import com.krly.api.IMessageHandler;

public class AbstractDeviceService {
    private static volatile AbstractDeviceService INSTANCE = null;
    private IMessageHandler messageHandler;

    private AbstractDeviceService() {
    }

    public static AbstractDeviceService getInstance() {
        if (INSTANCE == null) {
            synchronized (AbstractDeviceService.class) {
                if (INSTANCE == null)
                    INSTANCE = new AbstractDeviceService();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    public void setMessageHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
