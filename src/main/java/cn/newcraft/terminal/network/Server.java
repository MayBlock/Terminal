package cn.newcraft.terminal.network;

import java.util.Map;

public interface Server {

    Thread getThread();

    int getPort();

    Map<Integer, Sender> getSenderMap();

    void onServer();

    void shutdown();

    boolean isEnabled();
}
