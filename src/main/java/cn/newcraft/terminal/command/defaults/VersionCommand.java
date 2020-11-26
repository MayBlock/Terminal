package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Options;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.screen.Screen;

public class VersionCommand extends CommandManager {

    public VersionCommand() {
        super("version", "查看当前终端版本", "version");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        Options options = new Options();
        screen.sendMessage("-|----------------------|-");
        screen.sendMessage("版本号：" + options.getVersion());
        screen.sendMessage("内部版本号：" + options.getCanonicalVersion());
        screen.sendMessage("API版本：" + options.getApiVersion());
        screen.sendMessage("时区：" + options.getTimeZone().getID());
        screen.sendMessage("");
        screen.sendMessage("©2020 May_Block 版权所有，保留所有权利");
        screen.sendMessage("-|----------------------|-");
    }
}
