package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.Plugin;

public class RegisteredListener {

    private final Listener listener;
    private final Plugin plugin;

    public RegisteredListener(final Listener listener, Plugin plugin) {
        this.listener = listener;
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Listener getListener() {
        return listener;
    }

}
