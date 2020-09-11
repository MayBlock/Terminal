package cn.newcraft.terminal.command;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.screen.Screen;

public class ReloadCommand extends CommandManager {

    public ReloadCommand() {
        super("reload", "重载所有配置文件（部分配置需要重启终端才能生效）", "reload");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        ServerConfig.cfg.reload();
        ThemeConfig.cfg.reload();
        screen.sendMessage("成功重载配置文件！");
    }
}
