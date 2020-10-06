package cn.newcraft.terminal.command;

import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.network.ServerThread;

public class ConnectCommand extends CommandManager {

    public ConnectCommand() {
        super("connect", "查看当前所有已连接的终端数量", "connect");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        screen.sendMessage("当前正在连接终端的数量：" + ServerThread.getSenderMap().size());
    }
}
