package cn.newcraft.terminal.network;

import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;

import java.net.Socket;

public class NetworkEvent {

    public static class ClientConnectedEvent extends Event {

        private Sender sender;

        public ClientConnectedEvent(Sender sender) {
            this.sender = sender;
        }

        public Sender getSender() {
            return sender;
        }
    }

    public static class ClientConnectEvent extends Event implements Cancellable {

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

    public static class ClientDisconnectEvent extends Event {

        private Sender sender;

        public ClientDisconnectEvent(Sender sender) {
            this.sender = sender;
        }

        public Sender getSender() {
            return sender;
        }
    }

    public static class ClientReceivedEvent extends Event {

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
}
