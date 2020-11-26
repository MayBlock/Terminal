package cn.newcraft.terminal.network.packet;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.event.network.NetworkEvent;
import cn.newcraft.terminal.network.Sender;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        try {
            Event.callEvent(new NetworkEvent.ClientDisconnectEvent(sender));
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
        Socket socket = sender.getSocket();

        Terminal.getServer().getSenderMap().remove(sender.getId());

        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("DISCONNECT");
        b.writeUTF(reason);
        try {
            sender.sendByte(b.toByteArray(), sender.getSocket().getOutputStream());
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
        socket.close();
    }
}
