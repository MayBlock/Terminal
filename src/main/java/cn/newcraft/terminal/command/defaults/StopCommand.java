package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.screen.Screen;

public class StopCommand extends CommandManager {

    public StopCommand() {
        super("stop", "关闭终端并退出", "stop");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        Terminal.shutdown();
    }
}
