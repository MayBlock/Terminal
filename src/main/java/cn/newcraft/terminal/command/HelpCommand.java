package cn.newcraft.terminal.command;

import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.plugin.PluginManager;
import com.google.common.collect.Lists;

import java.text.Collator;
import java.util.*;

public class HelpCommand extends CommandManager {

    public HelpCommand() {
        super("help", "获取所有命令帮助", "help <Command>");
    }

    private static Comparator comparator = Collator.getInstance(Locale.ENGLISH);


    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length == 1) {
            screen.sendMessage("\n#---------- 所有命令帮助 ----------#");
            List<String> commands = Lists.newArrayList();
            /* 获取Terminal命令并使用forEach遍历存储至commands中 */
            List<CommandInfo> terminal = getCommandsInfo().get("Terminal");
            terminal.forEach(info -> commands.add(info.getCommand()));

            /* 获取插件命令并使用forEach遍历存储至commands中 */
            List<String> pluginLists = PluginManager.getPluginLists();
            pluginLists.forEach(name -> {
                List<CommandInfo> pluginInfo = getCommandsInfo().get(PluginManager.getPlugin(name).getPluginName());
                pluginInfo.forEach(info -> commands.add(info.getCommand()));
            });

            /* 排序commands并使用forEach遍历并输出 */
            commands.sort(comparator);
            commands.forEach(command -> screen.sendMessage(command + " - " + CommandManager.getCommands().get(command).getDesc()));
            screen.sendMessage("\n小提示：输入\"help <命令>\"可以查看该命令的详细用法哦\n");
            return;
        }
        if (CommandManager.isExist(args[1])) {
            screen.sendMessage("#---------- 命令 '" + args[1] + "' 的帮助 ----------#");
            screen.sendMessage("主命令：" + args[1]);
            screen.sendMessage("说明：" + CommandManager.getCommands().get(args[1]).getDesc());
            screen.sendMessage("用法：" + CommandManager.getCommands().get(args[1]).getUsage());
        } else {
            screen.sendMessage("查询失败，命令 " + args[1] + " 不存在！");
        }
    }
}
