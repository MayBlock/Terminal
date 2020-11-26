package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.Prefix;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.operate.OperateManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.TextColor;

public class SocketCommand extends CommandManager {

    public SocketCommand() {
        super("socket", "发送数据至现以连接至终端的远程客户端", "socket help");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "help":
                    screen.sendMessage("---- " + getCommand() + " 命令用法 ----");
                    screen.sendMessage("");
                    screen.sendMessage(getCommand() + " help - 查看" + getCommand() + "命令所有帮助");
                    screen.sendMessage(getCommand() + " operate - 查看所有的可执行操作");
                    screen.sendMessage(getCommand() + " active - 查看当前服务器监听是否正常开启");
                    screen.sendMessage(getCommand() + " start/shutdown/reboot - 启动/关闭/重启服务器监听");
                    screen.sendMessage(getCommand() + " [id] <operate> - 执行指定操作");
                    screen.sendMessage("");
                    break;
                case "operate":
                    screen.sendMessage("---- 所有可执行操作 ----");
                    screen.sendMessage("");
                    if (Terminal.getOperateMap().size() <= 0) {
                        screen.sendMessage("暂无任何可执行操作！");
                        break;
                    }
                    int i = 0;
                    for (String key : Terminal.getOperateMap().keySet()) {
                        OperateManager o = Terminal.getOperateMap().get(key);
                        i++;
                        screen.sendMessage(i + ". " + key + " - " + o.getDesc());
                    }
                    break;
                case "active":
                    screen.sendMessage("当前服务器监听状态：" + Terminal.getServer().isEnabled());
                    break;
                case "start":
                    if (Terminal.getServer().isEnabled()) {
                        screen.sendMessage("启动失败，当前服务器监听已经为开启状态！");
                        break;
                    }
                    Terminal.getScreen().sendMessage("正在尝试启动服务器监听...");
                    Terminal.getServer().onServer();
                    if (Terminal.getServer().isEnabled()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " 已成功启动服务器监听线程！");
                    } else {
                        Terminal.getScreen().sendMessage("服务器启动监听线程失败，可能是由于一些未知原因造成的，请检查当前服务器监听端口未被占用或尝试再次启动！");
                    }
                    break;
                case "shutdown":
                    if (!Terminal.getServer().isEnabled()) {
                        screen.sendMessage("关闭失败，当前服务器监听已经为关闭状态！");
                        break;
                    }
                    Terminal.getScreen().sendMessage("正在尝试关闭服务器监听...");
                    Terminal.getServer().shutdown();
                    if (!Terminal.getServer().isEnabled()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " 已成功关闭服务器监听线程！");
                    } else {
                        Terminal.getScreen().sendMessage("服务器关闭监听线程失败，可能是由于一些未知原因造成的，请尝试再次关闭！");
                    }
                    break;
                case "reboot":
                    if (!Terminal.getServer().isEnabled()) {
                        screen.sendMessage("重启失败，你必须先启动服务器监听才能进行重启！");
                        break;
                    }
                    Terminal.getScreen().sendMessage("正在尝试重启服务器监听...");
                    Terminal.getServer().shutdown();
                    Terminal.getServer().onServer();
                    if (Terminal.getServer().isEnabled()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " 已成功重启服务器监听线程！");
                    } else {
                        Terminal.getScreen().sendMessage("服务器重启监听线程失败，可能是由于一些未知原因造成的，请检查当前服务器监听端口未被占用或尝试再次启动！");
                    }
                    break;
                default:
                    OperateManager manager = Terminal.getOperateMap().get(args[1]);
                    if (manager == null) {
                        screen.sendMessage(TextColor.RED + "操作 " + args[1] + " 不存在！");
                        return;
                    }
                    if (manager.isTarget()) {
                        screen.sendMessage(TextColor.RED + "该操作无法针对全局执行！");
                        return;
                    }
                    manager.onOperate(screen, null);
                    screen.sendMessage("操作 " + manager.getName() + " 已执行完毕！");
            }
            return;
        }
        screen.sendMessage("用法：" + getUsage());
    }
}
