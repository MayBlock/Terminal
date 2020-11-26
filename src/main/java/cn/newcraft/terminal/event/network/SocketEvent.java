package cn.newcraft.terminal.event.network;

import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.network.Sender;
import cn.newcraft.terminal.network.packet.Packet;

public class SocketEvent extends Event {

    public static class SendByteToClientEvent extends SocketEvent implements Cancellable {

        private Sender sender;
        private byte[] bytes;
        private boolean cancellable = false;

        public SendByteToClientEvent(Sender sender, byte[] bytes) {
            this.sender = sender;
            this.bytes = bytes;
        }

        public Sender getSender() {
            return sender;
        }

        public byte[] getBytes() {
            return bytes;
        }

        @Override
        public boolean isCancelled() {
            return cancellable;
        }

        @Override
        public void setCancelled(boolean b) {
            cancellable = b;
        }
    }

    public static class SendPacketToClientEvent extends SocketEvent implements Cancellable {

        private Sender sender;
        private Packet packet;
        private boolean cancellable = false;

        public SendPacketToClientEvent(Sender sender, Packet packet) {
            this.sender = sender;
            this.packet = packet;
        }

        public Sender getSender() {
            return sender;
        }

        public Packet getPacket() {
            return packet;
        }

        @Override
        public boolean isCancelled() {
            return cancellable;
        }

        @Override
        public void setCancelled(boolean b) {
            cancellable = b;
        }
    }
}
