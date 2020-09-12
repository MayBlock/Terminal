package cn.newcraft.terminal.console;

import cn.newcraft.terminal.operate.DisconnectOperate;
import cn.newcraft.terminal.operate.OperateManager;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.*;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.plugin.Plugin;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginEnum;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.thread.Server;
import cn.newcraft.terminal.thread.ServerReceived;
import cn.newcraft.terminal.thread.ServerThread;

import java.util.TimeZone;

public class Initialization {

    public static boolean isInitialization = false;
    public static Screen screen = Terminal.getScreen();

    public static void init(String in) {
        try {
            int port = Integer.parseInt(in);
            if (port >= 1 && port <= 65535) {
                if (Method.isLocalPortUsing(port)) {
                    screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "当前已经被占用，请尝试使用其他的端口！");
                    if (Method.isReallyHeadless()) {
                        Terminal.getScreen().getConsoleScreen().stop();
                    }
                    return;
                }
                screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 成功设置端口为 " + port);
                ServerConfig.setPort(port);
                Terminal.getInstance().getSetting().setPort(port);
                onTerminal();
            } else {
                screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + in + "无效，端口设置只能为 1 - 65535之间！");
            }
        } catch (NumberFormatException ex) {
            screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 你输入的不是一个数字，请重试！");
        }
    }

    public static void onTerminal() {
        try {
            screen.setComponentEnabled(false);
            Terminal.getInstance().setDebug(ServerConfig.cfg.getYml().getBoolean("server.debug"));
            int port = ServerConfig.getPort();
            Terminal.getInstance().getSetting().setPort(port);
            if (!Method.isConnect()) {
                screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 你的电脑尚未联网，无法启动终端！");
                if (Method.isReallyHeadless()) {
                    Terminal.getScreen().getConsoleScreen().stop();
                }
                return;
            }
            if (port >= 1 && port <= 65535) {
                if (Method.isLocalPortUsing(port)) {
                    screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "当前已经被占用，请尝试使用其他的端口！");
                    screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 请检查 server.yml 配置文件，输入正确的端口后重新启动终端！");
                    if (Method.isReallyHeadless()) {
                        Terminal.getScreen().getConsoleScreen().stop();
                    }
                    return;
                }
            } else {
                screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 端口" + port + "无效，端口设置只能为 1 - 65535之间！");
                screen.sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 请检查 server.yml 配置文件，输入正确的端口后重新启动终端！");
                if (Method.isReallyHeadless()) {
                    Terminal.getScreen().getConsoleScreen().stop();
                }
                return;
            }
            new PluginManager(PluginEnum.LOAD);
            Plugin plugin = new Plugin("Terminal");
            /* regCommands start */
            CommandManager.regCommand(plugin, new ConnectCommand());
            CommandManager.regCommand(plugin, new DebugCommand());
            CommandManager.regCommand(plugin, new HelpCommand());
            CommandManager.regCommand(plugin, new PluginsCommand());
            CommandManager.regCommand(plugin, new ReloadCommand());
            CommandManager.regCommand(plugin, new StopCommand());
            CommandManager.regCommand(plugin, new SystemCommand());
            CommandManager.regCommand(plugin, new ShellCommand());
            /* regCommands stop */

            ServerReceived.registerIncomingPluginChannel(plugin, new Server());
            OperateManager.regOperate(new DisconnectOperate());

            TimeZone.setDefault(TimeZone.getTimeZone(ServerConfig.cfg.getYml().getString("server.timezone")));
            new ServerThread(port);
            ServerThread.getServer().startServer();
            screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 创建监听连接进程完毕！");
            screen.setComponentEnabled(true);
            isInitialization = false;
            new PluginManager(PluginEnum.ENABLE);
            screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 终端已全部初始化完毕！ (Version: " + Terminal.getInstance().getSetting().getVersion() + ")\n ");
            screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 你可输入命令 \"help\" 获取命令帮助");
            screen.sendMessage(Prefix.TERMINAL.getPrefix() + " 输入命令 \"stop\" 可以安全关闭Terminal！");
            Terminal.getScreen().onInitComplete();
        } catch (Exception e) {
            Method.printException(Initialization.class, e);
        }
    }
}
