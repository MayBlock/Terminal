package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;

public class DebugCommand extends CommandManager {

    public DebugCommand() {
        super("debug", "打开/关闭DEBUG模式", "debug");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (Terminal.isDebug()) {
            Terminal.setDebug(false);
            screen.sendMessage("已关闭 DEBUG 模式");
        } else {
            Terminal.setDebug(true);
            screen.sendMessage("已开启 DEBUG 模式");
        }
    }
}
