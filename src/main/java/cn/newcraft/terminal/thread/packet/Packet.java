package cn.newcraft.terminal.thread.packet;

import cn.newcraft.terminal.thread.Sender;

import java.io.IOException;

public interface Packet {

    void onPacket(Sender sender) throws IOException;
}
