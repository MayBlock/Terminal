package cn.newcraft.terminal.event.server;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.thread.Sender;
import cn.newcraft.terminal.thread.packet.Packet;

public class ServerSendPacketToClientEvent extends Event {

    private Sender sender;
    private Packet packet;

    public ServerSendPacketToClientEvent(Sender sender, Packet packet) {
        this.sender = sender;
        this.packet = packet;
    }

    public Sender getSender() {
        return sender;
    }

    public Packet getPacket() {
        return packet;
    }
}
