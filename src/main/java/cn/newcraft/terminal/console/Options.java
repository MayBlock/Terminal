package cn.newcraft.terminal.console;

import cn.newcraft.terminal.config.ServerConfig;

import java.util.TimeZone;

public class Options {

    private String version = "1.4.1";
    private String canonicalVersion = "1.4.1.201002_beta";
    private int apiVersion = 3;
    private TimeZone timeZone = TimeZone.getDefault();

    public String getVersion() {
        return version;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public String getCanonicalVersion() {
        return canonicalVersion;
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
