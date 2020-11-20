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
import java.util.Map;

public abstract class CommandManager extends CommandInfo {

    private static final Map<String, CommandManager> commands = new HashMap<>();
    private static final Map<String, List<CommandInfo>> commandsInfo = new HashMap<>();

    public CommandManager(String command, String desc, String usage) {
        super(command, desc, usage);
    }

    public CommandManager(String command, String desc, String usage, String... aliases) {
        super(command, desc, usage, aliases);
    }

    public static Map<String, List<CommandInfo>> getCommandsInfo() {
        return commandsInfo;
    }

    public static Map<String, CommandManager> getCommandMap() {
        return commands;
    }

    public static String exist(String command) {
        if (CommandManager.getCommandMap().get(command) != null) {
            return command;
        }
        if (CommandManager.getCommandMap().get("terminal:" + command.toLowerCase()) != null) {
            return "terminal:" + command.toLowerCase();
        }
        for (String plugin : PluginManager.getPlugins().keySet()) {
            if (CommandManager.getCommandMap().get((plugin + ":" + command).toLowerCase()) != null) {
                return (plugin + ":" + command).toLowerCase();
            }
        }
        for (String cmd : getCommandMap().keySet()) {
            String[] aliases = getCommandMap().get(cmd).getAliases();
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
