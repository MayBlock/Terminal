package cn.newcraft.terminal.thread.packet;

import cn.newcraft.terminal.thread.Sender;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HeartbeatPacket implements Packet {

    @Override
    public void onPacket(Sender sender) throws IOException {
        if (!sender.getSocket().getKeepAlive()) sender.getSocket().setKeepAlive(true);//true，若长时间没有连接则断开
        if (!sender.getSocket().getOOBInline()) sender.getSocket().setOOBInline(true);//true,允许发送紧急数据，不做处理
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("TEST_CONNECT");
        sender.sendByte(b.toByteArray(), false);
    }
}
