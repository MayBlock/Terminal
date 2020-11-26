package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

public class PluginsCommand extends CommandManager {

    public PluginsCommand() {
        super("plugins", "查看当前所有已加载插件", "plugins");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (ServerConfig.cfg.getYml().getBoolean("server.enable_plugin")) {
            Set<String> keys = PluginManager.getPlugins().keySet();
            List<String> plugins = Lists.newArrayList();
            screen.sendMessage("已加载插件 (" + PluginManager.getPlugins().size() + "个)");
            for (String key : keys) {
                plugins.add(PluginManager.getPlugins().get(key).getName());
            }
            screen.sendMessage(plugins.toString());
        } else {
            screen.sendMessage("当前未启用插件功能！");
        }
    }
}
