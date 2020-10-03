package cn.newcraft.terminal.command;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.update.Update;

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
                screen.sendMessage("update help - 获取更新帮助");
                screen.sendMessage("update check - 检查更新");
                screen.sendMessage("update latest - 更新至最新版本");
                break;
            case "check":
                screen.sendMessage("检查更新...");
                new Thread(() -> {
                    update.refreshUpdate();
                    update.checkUpdate(true);
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
