package cn.newcraft.terminal.command;

import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.screen.Screen;

import java.io.IOException;
import java.util.Properties;

public class SystemCommand extends CommandManager {

    public SystemCommand() {
        super("system", "对系统进行操作", "system <help/info/runCmd> [args...]");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        Properties prop = System.getProperties();
        if (args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "help":
                    screen.sendMessage("查看系统信息：/system info");
                    screen.sendMessage("执行你当前系统的系统命令：/system runCmd <command>");
                    break;
                case "info":
                    screen.sendMessage("\n操作系统：" + prop.getProperty("os.name"));
                    screen.sendMessage("Java信息：" + prop.getProperty("java.vm.name") + " (" + prop.getProperty("java.runtime.version") + ")");
                    screen.sendMessage("用户名：" + prop.getProperty("user.name"));
                    screen.sendMessage("执行其他系统操作请输入命令 \"system help\"\n");
                    break;
                case "runcmd":
                    if (args.length >= 3) {
                        StringBuilder text = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            text.append(args[i]).append(" ");
                        }
                        String string = text.toString().substring(0, text.toString().length() - 1);
                        new Thread(() -> {
                            try {
                                Method.runCmd(string);
                                screen.sendMessage("执行了系统命令 " + string);
                            } catch (IOException e) {
                                screen.sendMessage("命令 " + string + " 不存在！");
                            }
                        }).start();
                    } else {
                        screen.sendMessage("用法：system runCmd <command>");
                    }
                    break;
                default:
                    screen.sendMessage("用法：" + getUsage());
            }
        }
    }
}
