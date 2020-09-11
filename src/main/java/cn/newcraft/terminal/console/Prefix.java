package cn.newcraft.terminal.console;

public enum Prefix {

    TERMINAL("[Terminal]"),
    TERMINAL_ERROR("[Terminal/ERROR]"),
    TERMINAL_WARN("[Terminal/WARN]"),
    PLUGIN_MANAGER("[PluginSystem]"),
    PLUGIN_MANAGER_ERROR("[PluginSystem/ERROR]"),
    PLUGIN_MANAGER_WARN("[PluginSystem/WARN]"),
    DEBUG("[DEBUG]"),
    SERVER_THREAD("[Server Thread]"),
    CLIENT_THREAD("[Client Thread]"),
    SERVER_THREAD_ERROR("[Server Thread/ERROR]"),
    SERVER_THREAD_WARN("[Server Thread/WARN]"),
    CLIENT_THREAD_ERROR("[Client Thread/ERROR]");

    private String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
