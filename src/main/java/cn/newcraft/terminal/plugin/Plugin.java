package cn.newcraft.terminal.plugin;

import java.net.URLClassLoader;

public class Plugin {

    private String pluginName;

    public Plugin(String pluginName) {
        this.pluginName = pluginName;
    }

    public void enablePlugin() {
        new PluginManager(PluginEnum.ENABLE);
    }

    public void disablePlugin() {
        new PluginManager(PluginEnum.DISABLE);
    }

    public String getPrefix() {
        return PluginManager.getPlugins().get(pluginName).getPrefix();
    }

    public String getAuthor() {
        return PluginManager.getPlugins().get(pluginName).getAuthor();
    }

    public String getVersion() {
        return PluginManager.getPlugins().get(pluginName).getVersion();
    }

    public String getPath() {
        return PluginManager.getPlugins().get(pluginName).getPath();
    }

    public URLClassLoader getUrlClassLoader() {
        return PluginManager.getPlugins().get(pluginName).getUrlClassLoader();
    }

    public String getPluginName() {
        return pluginName;
    }
}
