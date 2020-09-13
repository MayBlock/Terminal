package cn.newcraft.terminal.config;

import cn.newcraft.terminal.console.Theme;

import java.awt.*;

public class ThemeConfig extends ConfigManager {

    public static ThemeConfig cfg;

    public ThemeConfig() throws Exception {
        super("theme", "./config");
    }

    public static void init() throws Exception {
        ThemeConfig.cfg = new ThemeConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("theme.white.name", "羊毛白");
        cfg.getYml().addDefault("theme.white.background", "#EAEDED");
        cfg.getYml().addDefault("theme.white.copyright", "#8F8F8F");
        cfg.getYml().addDefault("theme.white.version", "#000000");

        cfg.getYml().addDefault("theme.white.text.background", "#FFFFFF");
        cfg.getYml().addDefault("theme.white.text.foreground", "#000000");
        cfg.getYml().addDefault("theme.white.text.selection", "#C2E0FF");
        cfg.getYml().addDefault("theme.white.text.selectedText", "#000000");
        cfg.getYml().addDefault("theme.white.text.border", "#FFFFFF:1");

        cfg.getYml().addDefault("theme.white.input.background", "#FFFFFF");
        cfg.getYml().addDefault("theme.white.input.foreground", "#000000");
        cfg.getYml().addDefault("theme.white.input.border", "#000000:1");


        cfg.getYml().addDefault("theme.gray.name", "深空灰");
        cfg.getYml().addDefault("theme.gray.background", "#808080");
        cfg.getYml().addDefault("theme.gray.copyright", "#C2C2C2");
        cfg.getYml().addDefault("theme.gray.version", "#000000");

        cfg.getYml().addDefault("theme.gray.text.background", "#404040");
        cfg.getYml().addDefault("theme.gray.text.foreground", "#00FF00");
        cfg.getYml().addDefault("theme.gray.text.selection", "#6666CC");
        cfg.getYml().addDefault("theme.gray.text.selectedText", "#33CCFF");
        cfg.getYml().addDefault("theme.gray.text.border", "#000000:1");

        cfg.getYml().addDefault("theme.gray.input.background", "#404040");
        cfg.getYml().addDefault("theme.gray.input.foreground", "#00FF00");
        cfg.getYml().addDefault("theme.gray.input.border", "#000000:2");
        cfg.save();
        Theme.Init();
    }
}
