package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
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
                    //screen.sendMessage("修改当前/其他用户密码：/system changepassword [user] <newPassword>");
                    screen.sendMessage("执行你当前系统的系统命令：/system runCmd <command>");
                    break;
                case "info":
                    screen.sendMessage("\n系统：" + prop.getProperty("os.name") + " (" + prop.getProperty("java.vm.name") + ")");
                    screen.sendMessage("用户名：" + prop.getProperty("user.name"));
                    screen.sendMessage("执行其他系统操作请输入命令 \"system help\"\n");
                    break;
                case "runcmd":
                    if (args.length >= 3) {
                        StringBuilder text = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            text.append(args[i]).append(" ");
                        }
                        new Thread(() -> {
                            try {
                                Method.runCmd(text.toString());
                                screen.sendMessage("执行了系统命令 " + text.toString());
                            } catch (IOException e) {
                                Terminal.printException(this.getClass(), e);
                            }
                        }).start();
                    } else {
                        screen.sendMessage("用法：system runCmd <command>");
                    }
                    break;
                default:
                    screen.sendMessage("用法：" + getUsage());
            }
            /*if (args[1].equalsIgnoreCase("help")) {
                screen.sendMessage("查看系统信息：/system info");
                screen.sendMessage("修改当前/其他用户密码：/system changepassword [user] <newPassword>");
                return;
            }
            if (args[1].equalsIgnoreCase("info")) {
                screen.sendMessage("\n系统：" + prop.getProperty("os.name") + " (" + prop.getProperty("java.vm.name") + ")");
                screen.sendMessage("用户名：" + prop.getProperty("user.name"));
                screen.sendMessage("执行其他系统操作请输入命令 \"system help\"\n");
                return;
            } else if (args[1].equalsIgnoreCase("changepassword")) {
                if (args.length == 3) {
                    Method.runCmd("net user " + prop.getProperty("user.name") + " " + args[2]);
                    screen.sendMessage("尝试修改用户 " + prop.getProperty("user.name") + " 的密码，新密码为：" + args[2]);
                } else if (args.length == 4) {
                    Method.runCmd("net user " + args[2] + " " + args[3]);
                    screen.sendMessage("尝试修改用户 " + args[2] + " 的密码，新密码为：" + args[3]);
                } else {
                    screen.sendMessage("用法： \"system changepassword [user] <newPassword>\"");
                }
                return;
            }

             */
        }
    }
}
