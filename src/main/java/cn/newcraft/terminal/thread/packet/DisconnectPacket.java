package cn.newcraft.terminal.thread.packet;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.thread.Sender;
import cn.newcraft.terminal.thread.ServerThread;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.net.Socket;

public class DisconnectPacket implements Packet {

    private String reason = "disconnect";

    public DisconnectPacket() {
    }

    public DisconnectPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public void onPacket(Sender sender) throws IOException {
        Socket socket = sender.getSocket();

        ServerThread.removeHashInit(sender.getId());
        ServerThread.getSenders().remove(sender.getId());
        sender.getHeartThread().stop();

        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("DISCONNECT");
        b.writeUTF(reason);
        sender.sendByte(b.toByteArray(), false);

        socket.close();
    }
}
