package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.theme.Theme;

import java.awt.*;

public class TextColor {

    private String code;
    private Color color;
    private String string;

    public TextColor(String str) {
        if (str.contains("§0")) {
            code = "§0";
            color = new Color(0, 0, 0);
        } else if (str.contains("§1")) {
            code = "§1";
            color = new Color(40, 100, 255);
        } else if (str.contains("§2")) {
            code = "§2";
            color = new Color(0, 150, 0);
        } else if (str.contains("§3")) {
            code = "§3";
            color = new Color(50, 100, 150);
        } else if (str.contains("§4")) {
            code = "§4";
            color = new Color(150, 0, 0);
        } else if (str.contains("§5")) {
            code = "§5";
            color = new Color(200, 0, 200);
        } else if (str.contains("§6")) {
            code = "§6";
            color = new Color(255, 200, 0);
        } else if (str.contains("§7")) {
            code = "§7";
            color = new Color(170, 170, 170);
        } else if (str.contains("§8")) {
            code = "§8";
            color = new Color(100, 100, 100);
        } else if (str.contains("§9")) {
            code = "§9";
            color = new Color(90, 90, 255);
        } else if (str.contains("§f")) {
            code = "§f";
            color = new Color(255, 255, 255);
        } else if (str.contains("§a")) {
            code = "§a";
            color = new Color(0, 255, 0);
        } else if (str.contains("§e")) {
            code = "§e";
            color = new Color(255, 255, 0);
        } else if (str.contains("§b")) {
            code = "§b";
            color = new Color(0, 255, 255);
        } else if (str.contains("§c")) {
            code = "§c";
            color = new Color(255, 50, 50);
        } else if (str.contains("§d")) {
            code = "§d";
            color = new Color(255, 0, 255);
        } else if (str.contains("§r")) {
            code = "§r";
            color = Theme.getCurrentTheme().getTextForeground();
        }
        if (code != null) {
            string = str.replaceAll(code, "");
        } else {
            string = str;
        }
    }

    public String getCode() {
        return code;
    }

    public Color getColor() {
        return color;
    }

    public String getString() {
        return string;
    }

    public static String codeTo(String replacement, boolean consoleCode) {
        if (consoleCode) {
            return replacement
                    .replaceAll("§0", "\033[90;0m")
                    .replaceAll("§1", "\033[94m")
                    .replaceAll("§2", "\033[32m")
                    .replaceAll("§3", "\033[36m")
                    .replaceAll("§4", "\033[91m")
                    .replaceAll("§5", "\033[35")
                    .replaceAll("§6", "\033[93m")
                    .replaceAll("§7", "\033[37m")
                    .replaceAll("§8", "\033[38m")
                    .replaceAll("§9", "\033[94m")
                    .replaceAll("§f", "\033[30m")
                    .replaceAll("§a", "\033[92m")
                    .replaceAll("§e", "\033[93m")
                    .replaceAll("§b", "\033[36m")
                    .replaceAll("§c", "\033[31m")
                    .replaceAll("§d", "\033[95m")
                    .replaceAll("§r", "\033[0m")
                    .replaceAll("§l", "\033[1m")
                    .replaceAll("§n", "\033[4m")
                    .replaceAll("§m", "\033[9m")
                    .replaceAll("§o", "\033[3m")
                    .replaceAll("§", "")
                    + "\033[0m";
        }
        return replacement
                .replaceAll("§0", "")
                .replaceAll("§1", "")
                .replaceAll("§2", "")
                .replaceAll("§3", "")
                .replaceAll("§4", "")
                .replaceAll("§5", "")
                .replaceAll("§6", "")
                .replaceAll("§7", "")
                .replaceAll("§8", "")
                .replaceAll("§9", "")
                .replaceAll("§f", "")
                .replaceAll("§a", "")
                .replaceAll("§e", "")
                .replaceAll("§b", "")
                .replaceAll("§c", "")
                .replaceAll("§d", "")
                .replaceAll("§r", "")
                .replaceAll("§l", "")
                .replaceAll("§n", "")
                .replaceAll("§m", "")
                .replaceAll("§o", "")
                .replaceAll("§", "");
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
