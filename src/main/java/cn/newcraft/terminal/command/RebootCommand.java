package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;

public class RebootCommand extends CommandManager {

    public RebootCommand() {
        super("reboot", "重启终端", "reboot");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        Terminal.reboot();
    }
}
