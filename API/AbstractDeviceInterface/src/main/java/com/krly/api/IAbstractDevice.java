package com.krly.api;

public interface IAbstractDevice {
    void onMessage(byte[] data, String host, int port);
    void onClosed(int token);

    int write(int token, byte[] message);
}
