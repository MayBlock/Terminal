package cn.newcraft.terminal.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Theme {

    private String id;
    private String name;
    private String background;
    private String copyright;
    private String version;
    private String textBackground;
    private String textForeground;
    private String selection;
    private String selectedText;
    private String textBorder;

    private String scrollBackground;
    private String scrollTrack;

    private String buttonBackground;
    private String buttonForeground;
    private String buttonBorder;

    private String inputBackground;
    private String inputForeground;
    private String inputCaret;
    private String inputBorder;

    private static Theme currentTheme;

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static Theme getDefaultTheme() {
        return Theme.getThemeMap().get(ServerConfig.cfg.getYml().getString("server.default_theme"));
    }

    private static Map<String, Theme> themeHashMap = new HashMap<>();

    public static Map<String, Theme> getThemeMap() {
        return themeHashMap;
    }

    public Theme(String id, String name, String background, String copyright, String version,
                 String textBackground, String textForeground, String selection, String selectedText, String textBorder,
                 String scrollBackground, String scrollTrack,
                 String buttonBackground, String buttonForeground, String buttonBorder,
                 String inputBackground, String inputForeground, String inputCaret, String inputBorder) {
        this.id = id;
        this.name = name;
        this.background = background;
        this.copyright = copyright;
        this.version = version;
        this.textBackground = textBackground;
        this.textForeground = textForeground;
        this.selection = selection;
        this.selectedText = selectedText;
        this.textBorder = textBorder;
        this.scrollBackground = scrollBackground;
        this.scrollTrack = scrollTrack;
        this.buttonBackground = buttonBackground;
        this.buttonForeground = buttonForeground;
        this.buttonBorder = buttonBorder;
        this.inputBackground = inputBackground;
        this.inputForeground = inputForeground;
        this.inputCaret = inputCaret;
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

    public Color getVersion() {
        return Color.decode(version.split(":")[0]);
    }

    public String getVersionCode() {
        return version;
    }

    public Color getCopyright() {
        return Color.decode(copyright.split(":")[0]);
    }

    public String getCopyrightCode() {
        return copyright;
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

    public Color getScrollBackground() {
        return Color.decode(scrollBackground);
    }

    public String getScrollBackgroundCode() {
        return scrollBackground;
    }

    public Color getScrollTrack() {
        return Color.decode(scrollTrack);
    }

    public String getScrollTrackCode() {
        return scrollTrack;
    }

    public Color getButtonBackground() {
        return Color.decode(buttonBackground.split(":")[0]);
    }

    public String getButtonBackgroundCode() {
        return buttonBackground;
    }

    public Color getButtonForeground() {
        return Color.decode(buttonForeground.split(":")[0]);
    }

    public String getButtonForegroundCode() {
        return buttonForeground;
    }

    public Color getButtonBorder() {
        return Color.decode(buttonBorder.split(":")[0]);
    }

    public String getButtonBorderCode() {
        return buttonBorder;
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

    public Color getInputCaret() {
        return Color.decode(inputCaret);
    }

    public String getInputCaretCode() {
        return inputCaret;
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
            String copyright = ThemeConfig.cfg.getYml().getString("theme." + id + ".copyright");
            String version = ThemeConfig.cfg.getYml().getString("theme." + id + ".version");
            String textBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.background");
            String textForeground = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.foreground");
            String selection = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.selection");
            String selectedText = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.selectedText");
            String textBorder = ThemeConfig.cfg.getYml().getString("theme." + id + ".text.border");

            String scrollBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".scrollbar.background");
            String scrollTrack = ThemeConfig.cfg.getYml().getString("theme." + id + ".scrollbar.track");

            String buttonBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".button.background");
            String buttonForeground = ThemeConfig.cfg.getYml().getString("theme." + id + ".button.foreground");
            String buttonBorder = ThemeConfig.cfg.getYml().getString("theme." + id + ".button.border");

            String inputBackground = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.background");
            String inputForeground = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.foreground");
            String inputCaret = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.caret");
            String inputBorder = ThemeConfig.cfg.getYml().getString("theme." + id + ".input.border");
            Theme.themeHashMap.put(id, new Theme(id, name, background, copyright, version, textBackground, textForeground, selection, selectedText, textBorder, scrollBackground, scrollTrack, buttonBackground, buttonForeground, buttonBorder, inputBackground, inputForeground, inputCaret, inputBorder));
        }
    }

    public static void changeTheme(String id) {
        GraphicalScreen screen = Terminal.getScreen().getGraphicalScreen();

        String name = Terminal.getTheme(id).getName();
        String background = Terminal.getTheme(id).getBackgroundCode();
        String copyright = Terminal.getTheme(id).getCopyrightCode();
        String version = Terminal.getTheme(id).getVersionCode();
        String textBackground = Terminal.getTheme(id).getTextBackgroundCode();
        String textForeground = Terminal.getTheme(id).getTextForegroundCode();
        String selection = Terminal.getTheme(id).getSelectionCode();
        String selectedText = Terminal.getTheme(id).getSelectedTextCode();
        String textBorder = Terminal.getTheme(id).getTextBorderCode();

        String scrollBackground = Terminal.getTheme(id).getScrollBackgroundCode();
        String scrollTrack = Terminal.getTheme(id).getScrollTrackCode();

        String buttonBackground = Terminal.getTheme(id).getButtonBackgroundCode();
        String buttonForeground = Terminal.getTheme(id).getButtonForegroundCode();
        String buttonBorder = Terminal.getTheme(id).getButtonBorderCode();

        String inputBackground = Terminal.getTheme(id).getInputBackgroundCode();
        String inputForeground = Terminal.getTheme(id).getInputForegroundCode();
        String inputCaret = Terminal.getTheme(id).getInputCaretCode();
        String inputBorder = Terminal.getTheme(id).getInputBorderCode();

        screen.getTextArea().setBackground(Color.decode(textBackground));
        screen.getTextArea().setForeground(Color.decode(textForeground));
        screen.getCopyright().setForeground(Color.decode(copyright));
        screen.getVersion().setForeground(Color.decode(version));
        screen.getTextArea().setSelectionColor(Color.decode(selection));
        screen.getTextArea().setSelectedTextColor(Color.decode(selectedText));
        String[] split = textBorder.split(":");
        screen.getTextArea().setBorder(BorderFactory.createLineBorder(Color.decode(split[0]), Integer.parseInt(split[1])));
        screen.getInput().setBackground(Color.decode(inputBackground));
        screen.getInput().setForeground(Color.decode(inputForeground));
        screen.getInput().setCaretColor(Color.decode(inputCaret));
        screen.getInput().setSelectionColor(Color.decode(selection));
        screen.getInput().setSelectedTextColor(Color.decode(selectedText));
        String[] split1 = buttonBorder.split(":");
        screen.getClearLog().setBorder(BorderFactory.createLineBorder(Color.decode(split1[0]), Integer.parseInt(split1[1])));
        screen.getClearLog().setBackground(Color.decode(buttonBackground));
        screen.getClearLog().setForeground(Color.decode(buttonForeground));

        screen.getExecute().setBorder(BorderFactory.createLineBorder(Color.decode(split1[0]), Integer.parseInt(split1[1])));
        screen.getExecute().setBackground(Color.decode(buttonBackground));
        screen.getExecute().setForeground(Color.decode(buttonForeground));

        screen.getTheme().setBorder(BorderFactory.createLineBorder(Color.decode(split1[0]), Integer.parseInt(split1[1])));
        screen.getTheme().setBackground(Color.decode(buttonBackground));
        screen.getTheme().setForeground(Color.decode(buttonForeground));

        String[] split2 = inputBorder.split(":");
        screen.getInput().setBorder(BorderFactory.createLineBorder(Color.decode(split2[0]), Integer.parseInt(split2[1])));
        screen.getContentPane().setBackground(Color.decode(Terminal.getTheme(id).getBackgroundCode()));

        Theme theme = new Theme(id, name, background, copyright, version, textBackground, textForeground, selection, selectedText, textBorder, scrollBackground, scrollTrack, buttonBackground, buttonForeground, buttonBorder, inputBackground, inputForeground, inputCaret, inputBorder);
        currentTheme = theme;
        themeHashMap.put(id, theme);
        try {
            Event.callEvent(new ConsoleEvent.ChangeThemeEvent(themeHashMap.get(id)));
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(Theme.class, e);
        }
    }
}
