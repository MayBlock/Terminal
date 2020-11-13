package cn.newcraft.terminal.console;

import cn.newcraft.terminal.screen.TextColor;

public enum Prefix {

    TERMINAL("[Terminal]"),
    TERMINAL_ERROR(TextColor.RED + "[Terminal/ERROR]"),
    TERMINAL_WARN(TextColor.YELLOW + "[Terminal/WARN]"),
    PLUGIN_MANAGER("[PluginManager]"),
    PLUGIN_MANAGER_ERROR(TextColor.RED + "[PluginManager/ERROR]"),
    PLUGIN_MANAGER_WARN(TextColor.YELLOW + "[PluginManager/WARN]"),
    DEBUG(TextColor.GRAY + "[DEBUG]"),
    SERVER_THREAD("[Server Thread]"),
    SERVER_THREAD_ERROR(TextColor.RED + "[Server Thread/ERROR]"),
    SERVER_THREAD_WARN(TextColor.YELLOW + "[Server Thread/WARN]");

    private String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
