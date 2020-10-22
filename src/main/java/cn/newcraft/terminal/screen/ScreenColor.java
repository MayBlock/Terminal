package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Theme;
import cn.newcraft.terminal.util.Method;

import java.awt.*;

public class ScreenColor {

    private String code;
    private Color color;

    public ScreenColor(String str) {
        if (str.contains("§0")) {
            code = "§0";
            color = new Color(0, 0, 0);
            return;
        } else if (str.contains("§1")) {
            code = "§1";
            color = new Color(40, 100, 255);
            return;
        } else if (str.contains("§2")) {
            code = "§2";
            color = new Color(0, 150, 0);
            return;
        } else if (str.contains("§3")) {
            code = "§3";
            color = new Color(50, 100, 150);
            return;
        } else if (str.contains("§4")) {
            code = "§4";
            color = new Color(200, 0, 0);
            return;
        } else if (str.contains("§5")) {
            code = "§5";
            color = new Color(200, 50, 200);
            return;
        } else if (str.contains("§6")) {
            code = "§6";
            color = new Color(255, 200, 0);
            return;
        } else if (str.contains("§7")) {
            code = "§7";
            color = new Color(170, 170, 170);
            return;
        } else if (str.contains("§8")) {
            code = "§8";
            color = new Color(100, 100, 100);
            return;
        } else if (str.contains("§9")) {
            code = "§9";
            color = new Color(90, 90, 255);
            return;
        } else if (str.contains("§f")) {
            code = "§f";
            color = new Color(255, 255, 255);
            return;
        } else if (str.contains("§a")) {
            code = "§a";
            color = new Color(0, 255, 0);
            return;
        } else if (str.contains("§e")) {
            code = "§e";
            color = new Color(255, 255, 0);
            return;
        } else if (str.contains("§b")) {
            code = "§b";
            color = new Color(0, 255, 255);
            return;
        } else if (str.contains("§c")) {
            code = "§c";
            color = new Color(255, 50, 50);
            return;
        } else if (str.contains("§d")) {
            code = "§d";
            color = new Color(255, 0, 255);
            return;
        } else if (str.contains("§r")) {
            code = "§r";
            color = Theme.getCurrentTheme().getTextForeground();
            return;
        }
    }

    public String getCode() {
        return code;
    }

    public Color getColor() {
        return color;
    }

    public static final String BLACK = "§0";
    public static final String DARK_BLUE = "§1";
    public static final String DARK_GREEN = "§2";
    public static final String DARK_AQUA = "§3";
    public static final String DARK_RED = "§4";
    public static final String DARK_PURPLE = "§5";
    public static final String ORANGE = "§6";
    public static final String GRAY = "§7";
    public static final String DARK_GRAY = "§8";
    public static final String BLUE = "§9";
    public static final String GREEN = "§a";
    public static final String AQUA = "§b";
    public static final String RED = "§c";
    public static final String LIGHT_PURPLE = "§d";
    public static final String YELLOW = "§e";
    public static final String WHITE = "§f";

    public static final String BOLD = "§l";
    public static final String UNDERLINE = "§n";
    public static final String STRIKE = "§m";
    public static final String ITALIC = "§o";
    public static final String RESET = "§r";
}
