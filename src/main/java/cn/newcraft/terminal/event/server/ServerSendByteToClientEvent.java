package cn.newcraft.terminal.event.server;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.thread.Sender;

public class ServerSendByteToClientEvent extends Event {

    private Sender sender;
    private byte[] bytes;

    public ServerSendByteToClientEvent(Sender sender, byte[] bytes) {
        this.sender = sender;
        this.bytes = bytes;
    }

    public Sender getSender() {
        return sender;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
