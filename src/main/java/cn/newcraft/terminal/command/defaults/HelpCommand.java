package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandInfo;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;
import com.google.common.collect.Lists;

import java.text.Collator;
import java.util.*;

public class HelpCommand extends CommandManager {

    public HelpCommand() {
        super("help", "获取所有命令帮助", "help <Command>", "?");
    }

    private static Comparator comparator = Collator.getInstance(Locale.ENGLISH);


    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length == 1) {
            screen.sendMessage("\n#---------- 所有命令帮助 ----------#");
            List<String> commands = Lists.newArrayList();
            /* 获取Terminal命令并使用forEach遍历存储至commands中 */
            List<CommandInfo> terminal = getCommandsInfo().get("Terminal");
            terminal.forEach(info -> {
                System.out.println(info.getCommand());
                commands.add(info.getCommand().split(":")[1]);
            });

            /* 获取插件的Key并使用forEach遍历存储至commands中 */
            Set<String> plugins = PluginManager.getPlugins().keySet();
            plugins.forEach(name -> {
                List<CommandInfo> pluginInfo = getCommandsInfo().get(PluginManager.getPlugin(name).getPluginName());
                pluginInfo.forEach(info -> {
                    commands.add(info.getCommand().split(":")[1]);
                });
            });

            System.out.println("List: " + commands.toString());

            /* 排序commands并使用forEach遍历并输出 */
            commands.sort(comparator);
            commands.forEach(command -> screen.sendMessage(command + " - " + Terminal.getCommandMap().get(CommandManager.exist(command)).getDesc()));
            screen.sendMessage("\n小提示：输入\"help <命令>\"可以查看该命令的详细用法哦\n");
            return;
        }
        String existCommand = CommandManager.exist(args[1]);
        if (existCommand != null) {
            screen.sendMessage("#---------- 命令 '" + existCommand.split(":")[1] + "' 的帮助 ----------#");
            screen.sendMessage("主命令：" + existCommand.split(":")[1] + " (" + existCommand + ")");
            screen.sendMessage("说明：" + Terminal.getCommandMap().get(existCommand).getDesc());
            screen.sendMessage("用法：" + Terminal.getCommandMap().get(existCommand).getUsage());
            screen.sendMessage(Terminal.getCommandMap().get(existCommand).getAliases() != null ? "别称：" + Arrays.asList(Terminal.getCommandMap().get(existCommand).getAliases()).toString() : "别称：无");
        } else {
            screen.sendMessage("查询失败，命令 " + args[1] + " 不存在！");
        }
    }
}
