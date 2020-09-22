package cn.newcraft.terminal;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.console.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.console.Options;
import cn.newcraft.terminal.exception.UnknownException;
import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginEnum;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.update.console.ConsoleUpdate;
import cn.newcraft.terminal.update.graphical.GraphicalUpdate;
import cn.newcraft.terminal.thread.ServerThread;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Terminal {

    private static boolean debug;
    private static Terminal instance;
    private static Options options = new Options();
    private static Screen screen;
    private static Update update;
    private static String name = "Terminal";
    private static String programName;

    public static String getName() {
        return name;
    }

    public static String getProgramName() {
        return programName;
    }

    public static Screen getScreen() {
        return screen;
    }

    public static Update getUpdate() {
        return update;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean b) {
        debug = b;
    }

    public static Options getOptions() {
        return options;
    }

    public static void dispatchCommand(String command) {
        new SendCommand(command.split(" "));
    }

    public static void main(String[] str) {
        try {
            instance = new Terminal();
            ServerConfig.init();
            ThemeConfig.init();
            PluginManager.spawnFile();
            String defaultScreen = ServerConfig.cfg.getYml().getString("server.default_screen");
            if (defaultScreen == null) {
                if (!Method.isReallyHeadless()) {
                    initGraphicalScreen();
                } else {
                    initConsoleScreen();
                }
            } else {
                switch (defaultScreen) {
                    case "GraphicalScreen":
                        initGraphicalScreen();
                        break;
                    case "ConsoleScreen":
                        initConsoleScreen();
                        break;
                    default:
                        throw new UnknownException("Screen " + defaultScreen + " does not exist!");
                }
            }
            screen.onScreen();
            PropertyConfigurator.configure(Terminal.class.getResource("/log4j.properties"));
            screen.sendMessage(
                    "---------------------------------\n" +
                            "Terminal\n" +
                            "       Welcome!\n" +
                            "---------------------------------\n ");
            screen.sendMessage("Terminal starting...");
            String s = instance.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
            programName = s.substring(s.lastIndexOf("/") + 1);
            if (ServerConfig.getPort() == 0) {
                Initialization.isInitialization = true;
                screen.sendMessage("检测到你首次启动Terminal，你需要先完成初始化操作！");
                screen.sendMessage("请输入你要开放的端口");
                if (!Method.isReallyHeadless()) {
                    screen.getGraphicalScreen().getClearLog().setEnabled(false);
                    screen.getGraphicalScreen().getTheme().setEnabled(false);
                    screen.getGraphicalScreen().getInput().setEnabled(true);
                    screen.getGraphicalScreen().getExecute().setEnabled(true);
                    screen.getGraphicalScreen().getInput().setBackground(Color.decode(ThemeConfig.cfg.getYml().getString("theme." + ServerConfig.cfg.getYml().getString("server.default_theme") + ".input.background")));
                }
            }
            if (!Initialization.isInitialization) {
                Initialization.onTerminal();
            }
        } catch (Exception ex) {
            Method.printException(Terminal.class, ex);
        }
    }

    public static void reboot() {
        screen.sendMessage("Terminal rebooting...");
        new Thread(() -> {
            File directory = new File("");
            try {
                System.out.println("java -jar " + directory.getAbsolutePath() + "/" + programName);
                Method.runCmd("java -jar " + directory.getAbsolutePath() + "/" + programName);
            } catch (IOException ex) {
                Method.printException(Terminal.class, ex);
            }
        }).start();
        new Thread(() -> {
            new PluginManager(PluginEnum.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenderHashMap().size(); i++) {
                    try {
                        ServerThread.getSenderHashMap().get(i).disconnect("Server Closed");
                    } catch (IOException ignored) {
                    }
                }
                ServerThread.getServer().stopServer();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    public static void reboot(String path) {
        screen.sendMessage("Terminal rebooting...");
        new Thread(() -> {
            File directory = new File("");
            try {
                Method.runCmd("java -jar " + directory.getAbsolutePath() + "/" + path);
            } catch (IOException ex) {
                Method.printException(Terminal.class, ex);
            }
        }).start();
        new Thread(() -> {
            new PluginManager(PluginEnum.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenderHashMap().size(); i++) {
                    try {
                        ServerThread.getSenderHashMap().get(i).disconnect("Server Closed");
                    } catch (IOException ignored) {
                    }
                }
                ServerThread.getServer().stopServer();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    public static void shutdown() {
        screen.sendMessage("Terminal stopping...");
        new Thread(() -> {
            new PluginManager(PluginEnum.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenderHashMap().size(); i++) {
                    try {
                        ServerThread.getSenderHashMap().get(i).disconnect("Server Closed");
                    } catch (IOException ignored) {
                    }
                }
                ServerThread.getServer().stopServer();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    private static void initConsoleScreen() throws InterruptedException {
        if (!Method.isConnect()) {
            System.out.println(Prefix.TERMINAL_ERROR.getPrefix() + " 你的电脑尚未联网，无法启动终端！");
            Thread.sleep(1000);
            System.exit(0);
            return;
        }
        screen = new ConsoleScreen();
        update = new ConsoleUpdate();
    }

    private static void initGraphicalScreen() {
        if (!Method.isConnect()) {
            JOptionPane.showConfirmDialog(null, "你的电脑尚未联网，无法启动终端！", Prefix.TERMINAL_ERROR.getPrefix(), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        screen = new GraphicalScreen();
        update = new GraphicalUpdate();
    }
}
