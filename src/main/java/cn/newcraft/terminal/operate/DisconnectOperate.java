package cn.newcraft.terminal.operate;

import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.thread.Sender;
import cn.newcraft.terminal.thread.packet.DisconnectPacket;
import cn.newcraft.terminal.util.Method;

import java.io.IOException;

public class DisconnectOperate extends OperateManager {

    public DisconnectOperate() {
        super("disconnect", "与客户端断开连接");
    }

    @Override
    public void onOperate(Screen screen, Sender sender) {
        try {
            sender.sendPacket(new DisconnectPacket());
        } catch (IOException e) {
            Method.printException(this.getClass(), e);
        }
    }
}
