package cn.newcraft.terminal.console;

public enum Prefix {

    TERMINAL("[Terminal]"),
    TERMINAL_ERROR("[Terminal/ERROR]"),
    TERMINAL_WARN("[Terminal/WARN]"),
    PLUGIN_MANAGER("[PluginManager]"),
    PLUGIN_MANAGER_ERROR("[PluginManager/ERROR]"),
    PLUGIN_MANAGER_WARN("[PluginManager/WARN]"),
    DEBUG("[DEBUG]"),
    SERVER_THREAD("[Server Thread]"),
    SERVER_THREAD_ERROR("[Server Thread/ERROR]"),
    SERVER_THREAD_WARN("[Server Thread/WARN]");

    private String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
