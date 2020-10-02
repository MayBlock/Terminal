package cn.newcraft.terminal.event.network;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.thread.Sender;

public class ClientConnectedEvent extends Event {

    private Sender sender;

    public ClientConnectedEvent(Sender sender) {
        this.sender = sender;
    }

    public Sender getSender() {
        return sender;
    }
}
