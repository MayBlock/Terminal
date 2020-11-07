package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;

public class DebugCommand extends CommandManager {

    public DebugCommand() {
        super("debug", "打开/关闭DEBUG模式", "debug [on/off]");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length == 1) {
            if (Terminal.isDebug()) {
                Terminal.getTerminal().setDebug(false);
                screen.sendMessage("已关闭 DEBUG 模式");
            } else {
                Terminal.getTerminal().setDebug(true);
                screen.sendMessage("已开启 DEBUG 模式");
            }
            return;
        }
        switch (args[1].toLowerCase()) {
            case "on":
                if (!Terminal.isDebug()) {
                    Terminal.getTerminal().setDebug(true);
                    screen.sendMessage("已开启 DEBUG 模式");
                } else {
                    screen.sendMessage("当前DEBUG已经是开启状态，如需关闭请输入 \"debug off\"");
                }
                break;
            case "off":
                if (Terminal.isDebug()) {
                    Terminal.getTerminal().setDebug(false);
                    screen.sendMessage("已关闭 DEBUG 模式");
                } else {
                    screen.sendMessage("当前DEBUG已经是关闭状态，如需开启请输入 \"debug on\"");
                }
                break;
            default:
                screen.sendMessage("用法：" + getUsage());
        }
    }
}
