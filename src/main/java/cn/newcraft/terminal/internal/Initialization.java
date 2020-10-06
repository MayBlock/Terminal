package cn.newcraft.terminal.internal;

import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.exception.UnknownException;
import cn.newcraft.terminal.network.ServerListener;
import cn.newcraft.terminal.operate.DisconnectOperate;
import cn.newcraft.terminal.operate.OperateManager;
import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.graphical.GraphicalListener;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.update.console.ConsoleUpdate;
import cn.newcraft.terminal.update.graphical.GraphicalUpdate;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.*;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.plugin.Plugin;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.network.ServerThread;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Initialization {

    public static boolean isInitialization = false;

    public void initFirst(String in) {
        try {
            int port = Integer.parseInt(in);
            if (port >= 1 && port <= 65535) {
                if (Method.isLocalPortUsing(port)) {
                    Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "当前已经被占用，请尝试使用其他的端口！");
                    if (Method.isReallyHeadless()) {
                        Terminal.getScreen().getConsoleScreen().stop();
                    }
                    return;
                }
                new Terminal().setPort(port);
                ServerConfig.setPort(port);
                Terminal.getScreen().sendMessage(Prefix.TERMINAL.getPrefix() + " 成功设置端口为 " + port);
                initTerminal();
            } else {
                Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + in + "无效，端口设置只能为 1 - 65535之间！");
            }
        } catch (NumberFormatException ex) {
            Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 你输入的不是一个数字，请重试！");
        } catch (Exception e) {
            Terminal.printException(this.getClass(), e);
        }
    }

    public void initScreen() {
        try {
            String defaultScreen = ServerConfig.cfg.getYml().getString("server.default_screen");
            if (defaultScreen == null) {
                if (!Method.isReallyHeadless()) {
                    initGraphicalScreen();
                } else {
                    initConsoleScreen();
                }
            } else switch (defaultScreen) {
                case "GraphicalScreen":
                    initGraphicalScreen();
                    break;
                case "ConsoleScreen":
                    initConsoleScreen();
                    break;
                default:
                    throw new UnknownException("Screen " + defaultScreen + " does not exist!");
            }
            Terminal.getScreen().onScreen();
        } catch (Exception e) {
            Terminal.printException(this.getClass(), e);
        }
    }

    public void initTerminal() {
        long startTime = System.currentTimeMillis();
        try {
            Terminal.getScreen().setComponentEnabled(false);
            new Terminal().setDebug(ServerConfig.cfg.getYml().getBoolean("server.debug"));
            int port = Terminal.getPort();
            if (port >= 1 && port <= 65535) {
                if (Method.isLocalPortUsing(port)) {
                    Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "当前已经被占用，请尝试使用其他的端口！");
                    Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 请检查 server.yml 配置文件，输入正确的端口后重新启动终端！");
                    if (Method.isReallyHeadless()) {
                        Terminal.getScreen().getConsoleScreen().stop();
                    }
                    return;
                }
            } else {
                Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "无效，端口设置只能为 1 - 65535之间！");
                Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 请检查 server.yml 配置文件，输入正确的端口后重新启动终端！");
                if (Method.isReallyHeadless()) {
                    Terminal.getScreen().getConsoleScreen().stop();
                }
                return;
            }
            new PluginManager(PluginManager.Status.LOAD);
            Plugin plugin = new Plugin("Terminal");
            /* regCommands start */
            CommandManager.regCommand(plugin, new ConnectCommand());
            CommandManager.regCommand(plugin, new DebugCommand());
            CommandManager.regCommand(plugin, new HelpCommand());
            CommandManager.regCommand(plugin, new PluginsCommand());
            CommandManager.regCommand(plugin, new RebootCommand());
            CommandManager.regCommand(plugin, new StopCommand());
            CommandManager.regCommand(plugin, new SystemCommand());
            CommandManager.regCommand(plugin, new SocketCommand());
            CommandManager.regCommand(plugin, new UpdateCommand());
            CommandManager.regCommand(plugin, new VersionCommand());
            /* regCommands stop */

            Event.regListener(plugin, new GraphicalListener());
            Event.regListener(plugin, new ServerListener());
            OperateManager.regOperate(new DisconnectOperate());

            TimeZone.setDefault(TimeZone.getTimeZone(ServerConfig.cfg.getYml().getString("server.timezone")));
            new ServerThread();
            ServerThread.startServerThread();
            Terminal.getScreen().sendMessage(Prefix.TERMINAL.getPrefix() + " 创建监听连接进程完毕！");
            Terminal.getScreen().setComponentEnabled(true);
            isInitialization = false;
            new PluginManager(PluginManager.Status.ENABLE);
            SimpleDateFormat formatter = new SimpleDateFormat("s.SSS");
            Terminal.getScreen().sendMessage(Prefix.TERMINAL.getPrefix() + " 终端已全部初始化完毕！" +
                    " (耗时" + formatter.format(((System.currentTimeMillis() - startTime))) + "s)\n");
            Terminal.getScreen().sendMessage(Prefix.TERMINAL.getPrefix() + " 你可输入命令 \"help\" 获取命令帮助");
            Terminal.getScreen().sendMessage(Prefix.TERMINAL.getPrefix() + " 输入命令 \"stop\" 可以安全关闭Terminal！");
            new Thread(() -> {
                Terminal.getUpdate().refreshUpdate();
                Terminal.getUpdate().checkUpdate(false);
            }).start();
            Terminal.getScreen().onInitComplete();
        } catch (Exception e) {
            Terminal.printException(this.getClass(), e);
        }
    }

    private void initConsoleScreen() throws InterruptedException {
        Terminal terminal = new Terminal();
        if (!Method.isConnect()) {
            System.out.println(Prefix.TERMINAL_ERROR.getPrefix() + " 你的电脑尚未联网，无法启动终端！");
            Thread.sleep(1000);
            System.exit(0);
            return;
        }
        terminal.setScreen(new ConsoleScreen());
        terminal.setUpdate(new ConsoleUpdate());
    }

    private void initGraphicalScreen() {
        Terminal terminal = new Terminal();
        if (!Method.isConnect()) {
            JOptionPane.showConfirmDialog(null, "你的电脑尚未联网，无法启动终端！", Prefix.TERMINAL_ERROR.getPrefix(), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        terminal.setScreen(new GraphicalScreen());
        terminal.setUpdate(new GraphicalUpdate());
    }
}
