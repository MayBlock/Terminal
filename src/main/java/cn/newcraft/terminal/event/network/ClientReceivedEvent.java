package cn.newcraft.terminal.event.network;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.thread.Sender;

public class ClientReceivedEvent extends Event {

    private Sender sender;
    private byte[] bytes;

    public ClientReceivedEvent(Sender sender, byte[] bytes) {
        this.sender = sender;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Sender getSender() {
        return sender;
    }
}
