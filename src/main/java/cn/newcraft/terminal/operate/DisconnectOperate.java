package cn.newcraft.terminal.operate;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.network.Sender;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DisconnectOperate extends OperateManager {

    public DisconnectOperate() {
        super("disconnect", "与客户端断开连接");
    }

    @Override
    public void onOperate(Screen screen, Sender sender) {
        try {
            sender.disconnect("disconnect");
        } catch (IOException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            Terminal.printException(this.getClass(), e);
        }
    }
}
