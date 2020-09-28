package cn.newcraft.terminal;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.internal.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.console.Options;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.thread.ServerThread;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import org.apache.log4j.PropertyConfigurator;

import java.awt.Color;
import java.io.*;

public class Terminal {

    private static int port;
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

    public static Options getOptions() {
        return options;
    }

    public static int getPort() {
        return port;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void setDebug(boolean b) {
        debug = b;
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
            Initialization init = new Initialization();
            init.initScreen();
            PropertyConfigurator.configure(Terminal.class.getResource("/log4j.properties"));
            screen.sendMessage(
                    "---------------------------------\n" +
                            "Terminal\n" +
                            "       Welcome!\n" +
                            "---------------------------------\n ");
            screen.sendMessage("Terminal starting...");
            String s = instance.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
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
            printException(Terminal.class, ex);
        }
    }

    public static void reboot() {
        screen.sendMessage("Terminal rebooting...");
        new Thread(() -> {
            try {
                Method.runCmd(ServerConfig.cfg.getYml().getString("server.reboot_script")
                        .replace("{path}", new File("").getAbsolutePath())
                        .replace("{name}", Terminal.getProgramName()));
            } catch (IOException ex) {
                printException(Terminal.class, ex);
            }
        }).start();
        new Thread(() -> {
            new PluginManager(PluginManager.Status.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenders().size(); i++) {
                    try {
                        ServerThread.getSenders().get(i).disconnect("Server Closed");
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

    public static void reboot(String script) {
        screen.sendMessage("Terminal rebooting...");
        new Thread(() -> {
            try {
                Method.runCmd(script
                        .replace("{path}", new File("").getAbsolutePath())
                        .replace("{name}", Terminal.getProgramName()));
            } catch (IOException ex) {
                printException(Terminal.class, ex);
            }
        }).start();
        new Thread(() -> {
            new PluginManager(PluginManager.Status.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenders().size(); i++) {
                    try {
                        ServerThread.getSenders().get(i).disconnect("Server Closed");
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
            new PluginManager(PluginManager.Status.DISABLE);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getSenders().size(); i++) {
                    try {
                        ServerThread.getSenders().get(i).disconnect("Server Closed");
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

    public static void printException(Class clazz, Throwable ex) {
        ex.printStackTrace();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        ex.printStackTrace(ps);
        try {
            String output = os.toString("UTF-8");
            Terminal.getScreen().sendMessage("\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生错误，以下为错误报告\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 错误名称：" + ex.getMessage() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生的类：" + clazz.getName() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生时间：" + Method.getCurrentTime(Terminal.getOptions().getTimeZone()) + "\n\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 异常输出：\n" + output);
        } catch (UnsupportedEncodingException ignored) {
        }
    }
}
