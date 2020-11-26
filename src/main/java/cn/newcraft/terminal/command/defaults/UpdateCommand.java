package cn.newcraft.terminal.command.defaults;

import cn.newcraft.terminal.Prefix;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.update.Update;

import java.io.IOException;

public class UpdateCommand extends CommandManager {

    private boolean confirm = false;

    public UpdateCommand() {
        super("update", "检查更新", "update <help/check/latest>");
    }

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (!(args.length >= 2)) {
            screen.sendMessage("用法：" + getUsage());
            return;
        }
        Update update = Terminal.getUpdate();
        switch (args[1]) {
            case "help":
                screen.sendMessage(getCommand() + " help - 获取更新帮助");
                screen.sendMessage(getCommand() + " check - 检查更新");
                screen.sendMessage(getCommand() + " latest - 更新至最新版本");
                break;
            case "check":
                screen.sendMessage("检查更新...");
                new Thread(() -> {
                    try {
                        update.refreshUpdate();
                        update.checkUpdate(true);
                    } catch (IOException | NullPointerException e) {
                        Terminal.getScreen().sendMessage(Prefix.TERMINAL_ERROR.getPrefix() + " 检查更新失败，请检查网络连接是否正常！");
                    }
                }).start();
                break;
            case "latest":
                if (!confirm) {
                    update.confirmUpdate();
                    confirm = true;
                } else {
                    update.startUpdate();
                    confirm = false;
                }
                break;
            default:
                screen.sendMessage("用法：" + getUsage());
        }
    }
}
