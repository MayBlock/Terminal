package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.operate.OperateManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.network.Sender;
import cn.newcraft.terminal.screen.TextColor;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class SocketCommand extends CommandManager {

    public SocketCommand() {
        super("socket", "发送数据至现以连接至终端的远程客户端", "socket help");
    }

    private HashMap<Integer, ByteArrayDataOutput> bytes = new HashMap<>();

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
                    screen.sendMessage(getCommand() + " <id> add <byte> - 添加自定义Byte数据");
                    screen.sendMessage(getCommand() + " <id> send - 向客户端发送已添加的Byte数据 [以ByteArrayDataOutput形式发送]");
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
        if (args.length >= 3) {
            Sender sender;
            try {
                sender = Sender.getSender(Integer.parseInt(args[1]));
                if (sender == null) {
                    screen.sendMessage(TextColor.RED + "名为ID " + args[1] + " 的客户端不存在！");
                    return;
                }
            } catch (NumberFormatException e) {
                screen.sendMessage(TextColor.RED + "你输入的不是一个正确的ID！");
                return;
            }
            switch (args[2]) {
                case "add":
                    ByteArrayDataOutput b = bytes.get(sender.getId()) == null ? ByteStreams.newDataOutput() : bytes.get(sender.getId());
                    if (args.length >= 4) {
                        StringBuilder text = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            text.append(args[i]).append(" ");
                        }
                        String string = text.toString().substring(0, text.toString().length() - 1);
                        b.writeUTF(string);
                        bytes.put(sender.getId(), b);
                        screen.sendMessage("已成功添加数据：" + string);
                        screen.sendMessage("可继续添加数据，也可以使用send进行发送！");
                    } else {
                        screen.sendMessage("用法：" + getCommand() + " <id> add <byte>");
                    }
                    break;
                case "send":
                    if (bytes.get(sender.getId()) == null) {
                        screen.sendMessage("你还尚未添加任何数据！");
                        screen.sendMessage("请输入 \"" + getCommand() + " <id> add <byte>\" 来添加数据！");
                        break;
                    }
                    try {
                        sender.sendByte(bytes.get(sender.getId()).toByteArray(), false);
                        screen.sendMessage("已成功发送至 " + sender.getCanonicalName() + " 客户端");
                        bytes.remove(sender.getId());
                    } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                        Terminal.printException(this.getClass(), e);
                    }
                    break;
                default:
                    OperateManager manager = Terminal.getOperateMap().get(args[2]);
                    if (manager == null) {
                        screen.sendMessage(TextColor.RED + "操作 " + args[2] + " 不存在！");
                        break;
                    }
                    if (!manager.isTarget()) {
                        screen.sendMessage(TextColor.RED + "该操作无法针对目标执行！");
                        break;
                    }
                    manager.onOperate(screen, sender);
                    screen.sendMessage("操作 " + manager.getName() + " 已执行完毕！");
            }
            return;
        }
        screen.sendMessage("用法：" + getUsage());
    }
}
