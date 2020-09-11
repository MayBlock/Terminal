package cn.newcraft.terminal.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Theme {

    private String id;
    private String name;
    private String background;
    private String textBackground;
    private String textForeground;
    private String selection;
    private String selectedText;
    private String textBorder;

    private String inputBackground;
    private String inputForeground;
    private String inputBorder;

    protected static HashMap<String, Theme> themeHashMap = new HashMap<>();

    public Theme(String id, String name, String background,
                 String textBackground, String textForeground, String selection, String selectedText, String textBorder,
                 String inputBackground, String inputForeground, String inputBorder) {
        this.id = id;
        this.name = name;
        this.background = background;
        this.textBackground = textBackground;
        this.textForeground = textForeground;
        this.selection = selection;
        this.selectedText = selectedText;
        this.textBorder = textBorder;
        this.inputBackground = inputBackground;
        this.inputForeground = inputForeground;
        this.inputBorder = inputBorder;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getBackground() {
        return Color.decode(background.split(":")[0]);
    }

    public String getBackgroundCode() {
        return background;
    }

    public Color getTextBackground() {
        return Color.decode(textBackground.split(":")[0]);
    }

    public String getTextBackgroundCode() {
        return textBackground;
    }

    public Color getTextForeground() {
        return Color.decode(textForeground.split(":")[0]);
    }

    public String getTextForegroundCode() {
        return textForeground;
    }

    public Color getSelection() {
        return Color.decode(selection.split(":")[0]);
    }

    public String getSelectionCode() {
        return selection;
    }

    public Color getSelectedText() {
        return Color.decode(selectedText.split(":")[0]);
    }

    public String getSelectedTextCode() {
        return selectedText;
    }

    public Color getTextBorder() {
        return Color.decode(textBorder.split(":")[0]);
    }

    public String getTextBorderCode() {
        return textBorder;
    }

    public Color getInputBackground() {
        return Color.decode(inputBackground.split(":")[0]);
    }

    public String getInputBackgroundCode() {
        return inputBackground;
    }

    public Color getInputForeground() {
        return Color.decode(inputForeground.split(":")[0]);
    }

    public String getInputForegroundCode() {
        return inputForeground;
    }

    public Color getInputBorder() {
        return Color.decode(inputBorder.split(":")[0]);
    }

    public String getInputBorderCode() {
        return inputBorder;
    }

    public static void Init() {
        themeHashMap.clear();
        for (String id : ThemeConfig.cfg.getYml().getConfigurationSection("theme").getKeys(false)) {
            String name = ThemeConfig.cfg.getYml().getString("theme." + id + ".name");
            String background = ThemeConfig.cfg.getYml().getString("theme." + id + ".background");
            String textBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.background");
            String textForeground = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.foreground");
            String selection = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.selection");
            String selectedText = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.selectedText");
            String textBorder = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.border");

            String inputBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.background");
            String inputForeground = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.foreground");
            String inputBorder = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.border");
            Theme.themeHashMap.put(id, new Theme(id, name, background, textBackground, textForeground, selection, selectedText, textBorder, inputBackground, inputForeground, inputBorder));
        }
    }

    public static void changeTheme(String id) {
        String name = Terminal.getInstance().getSetting().getTheme(id).getName();
        String background = Terminal.getInstance().getSetting().getTheme(id).getBackgroundCode();
        String textBackground = Terminal.getInstance().getSetting().getTheme(id).getTextBackgroundCode();
        String textForeground = Terminal.getInstance().getSetting().getTheme(id).getTextForegroundCode();
        String selection = Terminal.getInstance().getSetting().getTheme(id).getSelectionCode();
        String selectedText = Terminal.getInstance().getSetting().getTheme(id).getSelectedTextCode();
        String textBorder = Terminal.getInstance().getSetting().getTheme(id).getTextBorderCode();

        String inputBackground = Terminal.getInstance().getSetting().getTheme(id).getInputBackgroundCode();
        String inputForeground = Terminal.getInstance().getSetting().getTheme(id).getInputForegroundCode();
        String inputBorder = Terminal.getInstance().getSetting().getTheme(id).getInputBorderCode();

        Terminal.getScreen().getGraphicalScreen().getTextArea().setBackground(Color.decode(textBackground));
        Terminal.getScreen().getGraphicalScreen().getTextArea().setForeground(Color.decode(textForeground));
        Terminal.getScreen().getGraphicalScreen().getTextArea().setSelectionColor(Color.decode(selection));
        Terminal.getScreen().getGraphicalScreen().getTextArea().setSelectedTextColor(Color.decode(selectedText));
        String[] split = textBorder.split(":");
        Terminal.getScreen().getGraphicalScreen().getTextArea().setBorder(BorderFactory.createLineBorder(Color.decode(split[0]), Integer.parseInt(split[1])));
        Terminal.getScreen().getGraphicalScreen().getInput().setBackground(Color.decode(inputBackground));
        Terminal.getScreen().getGraphicalScreen().getInput().setForeground(Color.decode(inputForeground));
        String[] split1 = inputBorder.split(":");
        Terminal.getScreen().getGraphicalScreen().getInput().setBorder(BorderFactory.createLineBorder(Color.decode(split1[0]), Integer.parseInt(split1[1])));
        Terminal.getScreen().getGraphicalScreen().getContentPane().setBackground(Color.decode(Terminal.getInstance().getSetting().getTheme(id).getBackgroundCode()));

        themeHashMap.put(id, new Theme(id, name, background, textBackground, textForeground, selection, selectedText, textBorder, inputBackground, inputForeground, inputBorder));
    }
}
