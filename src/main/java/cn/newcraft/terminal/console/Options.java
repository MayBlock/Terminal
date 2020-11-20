package cn.newcraft.terminal.console;

import cn.newcraft.terminal.config.ServerConfig;

import java.net.URL;
import java.util.TimeZone;

public class Options {

    private String version = "1.3.1";
    private String canonicalVersion = "1.3.1.201120_release";
    private int apiVersion = 4;
    private TimeZone timeZone = TimeZone.getDefault();

    public String getVersion() {
        return version;
    }

    public URL getImageResource() {
        return this.getClass().getResource("/console.png");
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public int getMaxConcurrent() {
        return ServerConfig.cfg.getYml().getInt("server.max_concurrent");
    }

    public String getCanonicalVersion() {
        return canonicalVersion;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Deprecated
    public Theme getDefaultTheme() {
        return Theme.getThemeMap().get(ServerConfig.cfg.getYml().getString("server.default_theme"));
    }

    public void setTimeZone(TimeZone timeZone) {
        TimeZone.setDefault(timeZone);
    }

    public void setDefaultTimeZone(TimeZone timeZone) {
        ServerConfig.cfg.getYml().set("server.timezone", timeZone.getID());
        TimeZone.setDefault(timeZone);
    }
}
