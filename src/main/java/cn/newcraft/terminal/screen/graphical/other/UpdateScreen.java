package cn.newcraft.terminal.screen.graphical.other;

import cn.newcraft.terminal.screen.graphical.GraphicalScreen;

import javax.swing.*;

public class UpdateScreen extends GraphicalScreen {

    public UpdateScreen(String newVersion) {
        String[] buttons = {"确定", "取消"};
        int jOptionPane = JOptionPane.showOptionDialog(this, "即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！", "Terminal Update", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
        if (jOptionPane == 0) {

        }
    }
}
