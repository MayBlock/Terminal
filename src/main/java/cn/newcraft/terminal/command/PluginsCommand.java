package cn.newcraft.terminal.command;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.configuration.file.YamlConfiguration;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;

import java.io.*;
import java.net.URLClassLoader;

public class PluginsCommand extends CommandManager {

    private static File file;
    private YamlConfiguration yml;
    private Class<?> LoadMain;
    private URLClassLoader urlClassLoader;

    public PluginsCommand() {
        super("plugins", "查看当前所有已加载插件", "plugins");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (ServerConfig.cfg.getYml().getBoolean("server.enable_plugin")) {
            screen.sendMessage("已加载插件 (" + PluginManager.getPluginLists().size() + "个)");
            screen.sendMessage(PluginManager.getPluginLists().toString());
        } else {
            screen.sendMessage("当前未启用插件功能！");
        }
    }
}
