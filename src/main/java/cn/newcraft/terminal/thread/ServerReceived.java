package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

public abstract class ServerReceived {

    private static HashMap<Plugin, ServerReceived> received = new HashMap<>();
    private static List<Plugin> receivedLists = Lists.newArrayList();

    public static HashMap<Plugin, ServerReceived> getReceived() {
        return received;
    }

    public static List<Plugin> getReceivedLists() {
        return receivedLists;
    }

    public abstract void onMessageReceived(Sender sender, byte[] bytes);

    public static void regIncomingPluginChannel(Plugin plugin, ServerReceived serverReceived) {
        received.put(plugin, serverReceived);
        if (!receivedLists.contains(plugin)) {
            receivedLists.add(plugin);
        }
    }
}
