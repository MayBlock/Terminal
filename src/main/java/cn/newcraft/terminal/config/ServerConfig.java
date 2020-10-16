package cn.newcraft.terminal.config;

public class ServerConfig extends ConfigManager {

    public static ServerConfig cfg;

    public ServerConfig() throws Exception {
        super("server", "./config");
    }

    public static void init() throws Exception {
        ServerConfig.cfg = new ServerConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("server.port", null);
        cfg.getYml().addDefault("server.enable_plugin", true);
        cfg.getYml().addDefault("server.debug", false);
        cfg.getYml().addDefault("server.max_connect", 100);
        cfg.getYml().addDefault("server.reboot_script", "java -jar {path}/{name}");
        cfg.getYml().addDefault("server.timezone", System.getProperty("user.timezone"));
        cfg.getYml().addDefault("server.default_theme", "white");
        cfg.getYml().addDefault("server.max_input_cache", 10);
        cfg.save();
    }

    public static void setPort(int port) {
        cfg.getYml().set("server.port", port);
        cfg.save();
    }

    public static Integer getPort() {
        return cfg.getYml().getInt("server.port");
    }

}
