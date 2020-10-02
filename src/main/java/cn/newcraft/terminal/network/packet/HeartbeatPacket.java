package cn.newcraft.terminal.network.packet;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.network.Sender;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class HeartbeatPacket implements Packet {

    @Override
    public void onPacket(Sender sender) throws IOException {
        if (!sender.getSocket().getKeepAlive()) sender.getSocket().setKeepAlive(true);
        if (!sender.getSocket().getOOBInline()) sender.getSocket().setOOBInline(true);
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("TEST_CONNECT");
        try {
            sender.sendByte(b.toByteArray(), false);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
    }
}
