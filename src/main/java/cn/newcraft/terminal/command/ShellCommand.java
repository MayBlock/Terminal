package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.operate.OperateManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.thread.Sender;
import cn.newcraft.terminal.thread.ServerThread;
import cn.newcraft.terminal.util.Method;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.util.HashMap;

public class ShellCommand extends CommandManager {

    public ShellCommand() {
        super("shell", "远程发送数据至现以连接至终端的远程服务器", "shell help");
    }

    private HashMap<Integer, ByteArrayDataOutput> bytes = new HashMap<>();

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("help")) {
            if (args.length >= 3 && args[2].equalsIgnoreCase("operate")) {
                screen.sendMessage("---- 所有可执行操作 ----");
                screen.sendMessage("");
                if (OperateManager.getOperateNames().size() <= 0) {
                    screen.sendMessage("暂无任何可执行操作！");
                    return;
                }
                for (int i = 0; i < OperateManager.getOperateNames().size(); i++) {
                    String name = OperateManager.getOperateNames().get(i);
                    OperateManager o = OperateManager.getRegOperate().get(name);
                    screen.sendMessage(i + 1 + ". " + name + " - " + o.getDesc());
                }
                return;
            }
            screen.sendMessage("---- " + getCommand() + " 命令用法 ----");
            screen.sendMessage("");
            screen.sendMessage(getCommand() + " help - 查看Remote命令所有帮助");
            screen.sendMessage(getCommand() + " help operate - 查看所有的可执行操作");
            screen.sendMessage(getCommand() + " <id> <operate> - 执行指定操作");
            screen.sendMessage(getCommand() + " <id> add <byte> - 添加自定义Byte数据");
            screen.sendMessage(getCommand() + " <id> send - 添加自定义String数据");
            screen.sendMessage("");
            return;
        }
        if (args.length >= 3) {
            Sender sender = ServerThread.getIntegerSocketHashMap().get(Integer.parseInt(args[1]));
            if (sender == null) {
                screen.sendMessage("名为ID " + args[1] + " 的客户端不存在！");
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
                    } catch (IOException e) {
                        Method.printException(this.getClass(), e);
                    }
                    break;
                default:
                    if (OperateManager.getRegOperate().get(args[2]) == null) {
                        screen.sendMessage("操作 " + args[2] + " 不存在！");
                        break;
                    }
                    OperateManager.getRegOperate().get(args[2]).onOperate(Terminal.getScreen(), sender);
            }
            return;
        }
        screen.sendMessage("用法：" + getUsage());
    }
}
