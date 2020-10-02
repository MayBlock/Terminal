package cn.newcraft.terminal.event.network;

import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;

import java.net.Socket;

public class ClientConnectEvent extends Event implements Cancellable {

    private String chancel;
    private Socket socket;
    private boolean cancelled = false;

    public ClientConnectEvent(String chancel, Socket socket) {
        this.chancel = chancel;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getChancel() {
        return chancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
