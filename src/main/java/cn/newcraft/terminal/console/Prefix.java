package cn.newcraft.terminal.console;

public enum Prefix {

    TERMINAL("[Terminal]"),
    TERMINAL_ERROR("[Terminal/ERROR]"),
    PLUGIN_MANAGER("[PluginSystem]"),
    PLUGIN_MANAGER_ERROR("[PluginSystem/ERROR]"),
    DEBUG("[DEBUG]"),
    SERVER_THREAD("[Server Thread]"),
    CLIENT_THREAD("[Client Thread]"),
    SERVER_THREAD_ERROR("[Server Thread/ERROR]"),
    CLIENT_THREAD_ERROR("[Client Thread/ERROR]");

    private String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
