package com.krly.api.comsumer;


import com.alibaba.dubbo.config.annotation.Reference;
import com.krly.api.IAbstractDevice;
import com.krly.transceiver.ChannelHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AbstractDeviceProxy {
    @Reference
    private IAbstractDevice abstractDevice;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    //===================================================================================
    public void onChannelClosed(Channel channel) {
        final Integer token = ChannelHolder.getInstance().getToken(channel);
        if (token == null)
            return;

        executorService.execute(() -> {
                abstractDevice.onClosed(token);
            }
        );
    }

    public void onChannelRead(byte[] data, Channel channel, String host, int port) {
        if (data == null || data.length < 4)
            return;

        int token = (data[0] & 0xFF) << 24;
        token += (data[1] & 0xFF) << 16;
        token += (data[2] & 0xFF) << 8;
        token += data[3] & 0xFF;

        // FIXME
        /*
        * A workaround here to simplify the server implementation.
        * For a distributed TCP connection management, it always works behind a load balancer,
        * we have no idea to assume which connection goes on a specified server, it determined by
        * the balancer (e.g. SLB). So a monitor is needed to keep the mapping between the server and token which
        * stands for an individual device. But we also can't assume the monitor always be fine, so here to use
        * ZooKeeper as a register.
        * A monitor watches the status of all connection management servers.
        *
        * */

        // Command message from AbstractDevice
        if (token == 0) {
            // TODO
            if (data.length <= 8) // token + token + data
                return;

            token = token = (data[4] & 0xFF) << 24;
            token += (data[5] & 0xFF) << 16;
            token += (data[6] & 0xFF) << 8;
            token += data[7] & 0xFF;

            byte[] msg = new byte[data.length - 8];
            for (int pos = 8; pos < data.length; pos++)
                msg[pos - 8] = data[pos];

            write(token, msg);

            return;
        }

        ChannelHolder.getInstance().addChannel(token, channel);

        executorService.execute(
                () -> abstractDevice.onMessage(data, host, port)
            );
    }

    //===================================================================================
    public void write(int token, byte[] message) {
        Channel channel = ChannelHolder.getInstance().getChannel(token);
        if (channel == null)
            return;

        //
        ByteBuf buf = Unpooled.buffer(message.length);
        buf.writeBytes(message);

        channel.writeAndFlush(buf);
    }
}
