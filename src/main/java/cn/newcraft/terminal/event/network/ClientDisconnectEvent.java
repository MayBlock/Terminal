package cn.newcraft.terminal.event.network;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.thread.Sender;

public class ClientDisconnectEvent extends Event {

    private Sender sender;

    public ClientDisconnectEvent(Sender sender) {
        this.sender = sender;
    }

    public Sender getSender() {
        return sender;
    }
}
