package cn.newcraft.terminal.network.packet;

import cn.newcraft.terminal.network.Sender;

import java.io.IOException;

public interface Packet {

    void onPacket(Sender sender) throws IOException;
}
