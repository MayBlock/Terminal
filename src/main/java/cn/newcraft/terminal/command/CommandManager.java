package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.exception.InvalidCommandException;
import cn.newcraft.terminal.plugin.PluginManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class CommandManager extends CommandInfo {


    private static HashMap<String, CommandManager> commands = new HashMap<>();
    private static HashMap<String, List<CommandInfo>> commandsInfo = new HashMap<>();

    public CommandManager(String command, String desc, String usage) {
        super(command, desc, usage);
    }

    public CommandManager(String command, String desc, String usage, String... aliases) {
        super(command, desc, usage, aliases);
    }

    public static HashMap<String, List<CommandInfo>> getCommandsInfo() {
        return commandsInfo;
    }

    public static HashMap<String, CommandManager> getCommands() {
        return commands;
    }

    public static String isExist(String command) {
        if (CommandManager.getCommands().get(command) != null) {
            return command;
        }
        for (String plugin : PluginManager.getPlugins().keySet()) {
            System.out.println((plugin + ":" + command).toLowerCase());
            if (CommandManager.getCommands().get((plugin + ":" + command).toLowerCase()) != null) {
                return (plugin + ":" + command).toLowerCase();
            } else if (CommandManager.getCommands().get("terminal:" + command.toLowerCase()) != null) {
                return "terminal:" + command.toLowerCase();
            }
        }
        for (String cmd : getCommands().keySet()) {
            String[] aliases = getCommands().get(cmd).getAliases();
            if (aliases != null && Arrays.asList(aliases).contains(command)) {
                return cmd;
            }
        }
        return null;
    }

    public abstract void onCommand(Screen screen, String[] args);

    public static void regCommand(Plugin plugin, CommandManager name) {
        String command = (plugin.getPluginName() + ":" + name.getCommand()).toLowerCase();
        if (commands.get(command) != null) {
            try {
                throw new InvalidCommandException("The " + command + " command has been registered!");
            } catch (InvalidCommandException e) {
                Terminal.printException(CommandManager.class, e);
            }
            return;
        }
        commands.put(command, name);
        List<CommandInfo> list = commandsInfo.get(plugin.getPluginName()) == null ? Lists.newArrayList() : commandsInfo.get(plugin.getPluginName());
        list.add(new CommandInfo(command, name.getDesc(), name.getUsage()));
        commandsInfo.put(plugin.getPluginName(), list);
    }
}
