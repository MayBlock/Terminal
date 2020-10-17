package cn.newcraft.terminal.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.event.Event;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class SendCommand {

    public SendCommand(String[] command) {
        ConsoleEvent.SendCommandEvent commandEvent = new ConsoleEvent.SendCommandEvent(command);
        try {
            Event.callEvent(commandEvent);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
        if (commandEvent.isCancelled()) return;
        String exitsCommand = CommandManager.isExist(command[0]);
        if (exitsCommand == null) {
            Terminal.getScreen().sendMessage("命令不存在，输入 \"help\" 获取命令帮助");
            return;
        }
        try {
            CommandManager.getCommands().get(exitsCommand).onCommand(Terminal.getScreen(), command);
        } catch (Exception e) {
            Terminal.printException(this.getClass(), e);
        }
    }
}
