package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.screen.Screen;

public class ConnectCommand extends CommandManager {

    public ConnectCommand() {
        super("connect", "查看当前所有已连接的终端数量", "connect");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        screen.sendMessage("当前正在连接终端的数量：" + Terminal.getServer().getSenderMap().size());
    }
}
