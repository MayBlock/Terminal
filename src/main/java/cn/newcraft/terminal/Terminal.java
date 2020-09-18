package cn.newcraft.terminal;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.console.Initialization;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.console.Options;
import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginEnum;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.thread.ServerThread;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import org.apache.log4j.PropertyConfigurator;

import java.awt.*;
import java.io.IOException;

public class Terminal {

    private static boolean debug;
    private static Terminal instance;
    private static Options options = new Options();
    private static Screen screen;
    private static String name = "Terminal";

    public static String getName() {
        return name;
    }

    public static Screen getScreen() {
        return screen;
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
            if (ServerConfig.cfg.getYml().getString("server.default_screen") == null) {
                if (!Method.isReallyHeadless()) {
                    screen = new GraphicalScreen();
                } else screen = new ConsoleScreen();
            } else
                screen = ServerConfig.cfg.getYml().getString("server.default_screen").equalsIgnoreCase("GraphicalScreen") ? new GraphicalScreen() : new ConsoleScreen();
            screen.onScreen();
            PropertyConfigurator.configure(Terminal.class.getResource("/log4j.properties"));
            screen.sendMessage(
                    "---------------------------------\n" +
                            "Terminal\n" +
                            "       Welcome!\n" +
                            "---------------------------------\n ");
            screen.sendMessage("Terminal Starting...");
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
            new Update(getOptions().getCanonicalVersion());
        } catch (Exception ex) {
            Method.printException(Terminal.class, ex);
        }
    }

    public static void shutdown() {
        screen.sendMessage("Terminal Stopping...");
        new Thread(() -> {
            new PluginManager(PluginEnum.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getIntegerSocketHashMap().size(); i++) {
                    try {
                        ServerThread.getIntegerSocketHashMap().get(i).disconnect("Server Closed");
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
}
