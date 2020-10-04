package cn.newcraft.terminal.plugin;

public abstract class MainPlugin extends Plugin {

    public MainPlugin(String pluginName) {
        super(pluginName);
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();
}
