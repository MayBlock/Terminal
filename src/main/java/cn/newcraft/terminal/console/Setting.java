package cn.newcraft.terminal.console;

import cn.newcraft.terminal.config.ServerConfig;

import java.util.TimeZone;

public class Setting {

    private int port;
    private String version = "0.1.1";
    private TimeZone timeZone = TimeZone.getDefault();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public Theme getTheme(String id) {
        return Theme.themeHashMap.get(id);
    }

    public Theme getDefaultTheme() {
        return Theme.themeHashMap.get(ServerConfig.cfg.getYml().getString("server.default_theme"));
    }

    public void setTimeZone(TimeZone timeZone) {
        TimeZone.setDefault(timeZone);
    }

    public void setDefaultTimeZone(TimeZone timeZone) {
        ServerConfig.cfg.getYml().set("server.timezone", timeZone.getID());
        TimeZone.setDefault(timeZone);
    }
}
