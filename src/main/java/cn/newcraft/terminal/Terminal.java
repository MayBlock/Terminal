package cn.newcraft.terminal;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.console.Theme;
import cn.newcraft.terminal.internal.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.console.Options;
import cn.newcraft.terminal.network.Server;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.screen.TextColor;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;

public class Terminal {

    private static int port;
    private static Server server;
    private static Logger logger;
    private static boolean debug;
    private static boolean internet = false;
    private static Terminal terminal;
    private static final Options options = new Options();
    private static Screen screen;
    private static Update update;
    private static final String name = "Terminal";
    private static String programName;

    public static Terminal getTerminal() {
        return terminal;
    }

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

    public static Server getServer() {
        return server;
    }

    public static Theme getTheme(String id) {
        return Theme.getThemeMap().get(id);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isInternetEnabled() {
        return internet;
    }

    public static Options getOptions() {
        return options;
    }

    public static int getPort() {
        return port;
    }

    public static Logger getLogger() {
        return logger;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public void setInternet(boolean b) {
        this.internet = b;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void setDebug(boolean b) {
        debug = b;
    }

    public synchronized static void dispatchCommand(String command) {
        new SendCommand(command.split(" "));
    }

    public static void main(String[] str) {
        terminal = new Terminal();
        try {
            ServerConfig.init();
            ThemeConfig.init();
            PluginManager.spawnFile();
            Initialization init = new Initialization();
            init.initScreen();
            logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
            screen.sendMessage(
                    "---------------------------------\n" +
                            "Terminal\n" +
                            "       Welcome!\n" +
                            "---------------------------------\n");
            screen.sendMessage(TextColor.RED + TextColor.BOLD + TextColor.ITALIC + "Terminal starting...");
            String s = terminal.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
            programName = s.substring(s.lastIndexOf("/") + 1);
            port = ServerConfig.getPort();
            if (port == 0) {
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
                init.initTerminal();
            }
        } catch (Exception ex) {
            printSeriousException(
                    "A serious error occurred while Terminal was running! （" + ex.toString() + "）",
                    "Please submit a report on \"https://github.com/MayBlock/Terminal/issues/new/choose\" to resolve this issue.");
        }
    }

    public static void reboot() {
        screen.sendMessage("Terminal rebooting...");
        new PluginManager(PluginManager.Status.DISABLE);
        new Thread(() -> {
            new Thread(() -> {
                try {
                    Method.runCmd(ServerConfig.cfg.getYml().getString("server.reboot_script")
                            .replace("{path}", new File("").getAbsolutePath())
                            .replace("{name}", Terminal.getProgramName()));
                } catch (IOException ex) {
                    printException(Terminal.class, ex);
                }
            }).start();
            if (server.isEnabled()) {
                server.shutdown();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    public static void reboot(String script) {
        screen.sendMessage("Terminal rebooting...");
        new PluginManager(PluginManager.Status.DISABLE);
        new Thread(() -> {
            new Thread(() -> {
                try {
                    Method.runCmd(script
                            .replace("{path}", new File("").getAbsolutePath())
                            .replace("{name}", Terminal.getProgramName()));
                } catch (IOException ex) {
                    printException(Terminal.class, ex);
                }
            }).start();
            if (server.isEnabled()) {
                server.shutdown();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    public static void shutdown() {
        screen.sendMessage("Terminal stopping...");
        new PluginManager(PluginManager.Status.DISABLE);
        new Thread(() -> {
            if (server.isEnabled()) {
                server.shutdown();
            }
            screen.onDisable();
            screen = null;
            System.exit(0);
        }).start();
    }

    public static void printException(Class clazz, Throwable ex) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        ex.printStackTrace(ps);
        try {
            String output = os.toString("UTF-8");
            Terminal.getScreen().sendMessage(TextColor.RED + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生错误，以下为错误报告\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 错误名称：" + ex.getMessage() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生的类：" + clazz.getName() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生时间：" + Method.getCurrentTime(Terminal.getOptions().getTimeZone()) + "\n\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 异常输出：\n" + output);
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    public static void printSeriousException(String... reasons) {
        if (server != null && server.isEnabled()) {
            server.shutdown();
        }
        if (screen == null) {
            for (String reason : reasons) {
                System.err.println(Prefix.TERMINAL_ERROR.getPrefix() + " " + reason);
            }
        } else {
            screen.setComponentEnabled(false);
            for (String reason : reasons) {
                screen.sendMessage(TextColor.RED + Prefix.TERMINAL_ERROR.getPrefix() + " " + reason);
            }
        }
    }
}
