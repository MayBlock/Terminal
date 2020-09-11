package cn.newcraft.terminal.plugin;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;

public abstract class MainPlugin extends Plugin {

    public static String name;

    public MainPlugin(String pluginName) {
        super(pluginName);
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();
}
