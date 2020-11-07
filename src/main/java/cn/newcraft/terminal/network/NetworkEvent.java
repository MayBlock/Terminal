package cn.newcraft.terminal.network;

import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;

import java.net.Socket;

public class NetworkEvent extends Event {

    public static class ClientConnectedEvent extends NetworkEvent {

        private Sender sender;

        public ClientConnectedEvent(Sender sender) {
            this.sender = sender;
        }

        public Sender getSender() {
            return sender;
        }
    }

    public static class ClientConnectEvent extends NetworkEvent implements Cancellable {

        private String channel;
        private Socket socket;
        private boolean cancelled = false;

        public ClientConnectEvent(String channel, Socket socket) {
            this.channel = channel;
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        public String getChannel() {
            return channel;
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

    public static class ClientDisconnectEvent extends NetworkEvent {

        private Sender sender;

        public ClientDisconnectEvent(Sender sender) {
            this.sender = sender;
        }

        public Sender getSender() {
            return sender;
        }
    }

    public static class ServerReceivedEvent extends NetworkEvent {

        private Sender sender;
        private Object input;

        public ServerReceivedEvent(Sender sender, Object input) {
            this.sender = sender;
            this.input = input;
        }

        public Object getInput() {
            return input;
        }

        public Sender getSender() {
            return sender;
        }
    }

    public static class ServerStartEvent extends NetworkEvent {

        public ServerStartEvent() {
        }
    }

    public static class ServerStopEvent extends NetworkEvent {

        public ServerStopEvent() {
        }
    }
}
