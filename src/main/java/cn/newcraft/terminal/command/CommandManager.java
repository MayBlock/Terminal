package cn.newcraft.terminal.command;

import cn.newcraft.terminal.exception.InvalidCommandException;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.Plugin;
import cn.newcraft.terminal.util.Method;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

public abstract class CommandManager extends CommandInfo {


    private static HashMap<String, CommandManager> commands = new HashMap<>();
    private static HashMap<String, List<CommandInfo>> commandsInfo = new HashMap<>();

    public CommandManager(String command, String desc, String usage) {
        super(command, desc, usage);
    }

    public static HashMap<String, List<CommandInfo>> getCommandsInfo() {
        return commandsInfo;
    }

    public static HashMap<String, CommandManager> getCommands() {
        return commands;
    }

    public static boolean isExist(String command) {
        return CommandManager.getCommands().get(command.toLowerCase()) != null;
    }

    public abstract void onCommand(Screen screen, String[] args);

    public static void regCommand(Plugin plugin, CommandManager command) {
        if (commands.get(command.getCommand()) != null) {
            try {
                throw new InvalidCommandException("The " + command.getCommand() + " command has been registered!");
            } catch (InvalidCommandException e) {
                Method.printException(CommandManager.class, e);
            }
            return;
        }
        commands.put(command.getCommand(), command);
        List<CommandInfo> list = commandsInfo.get(plugin.getPluginName()) == null ? Lists.newArrayList() : commandsInfo.get(plugin.getPluginName());
        list.add(new CommandInfo(command.getCommand(), command.getDesc(), command.getUsage()));
        commandsInfo.put(plugin.getPluginName(), list);
    }
}
