package cn.newcraft.terminal.console;

import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;

public class SendCommand {

    public SendCommand(String[] command) {
        if (!CommandManager.isExist(command[0])) {
            Terminal.getScreen().sendMessage("命令不存在，输入 \"help\" 获取命令帮助");
            return;
        }
        try {
            CommandManager.getCommands().get(command[0].toLowerCase()).onCommand(Terminal.getScreen(), command);
        } catch (Exception e) {
            Method.printException(this.getClass(), e);
        }
    }
}
