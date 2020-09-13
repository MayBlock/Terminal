package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;

public interface Screen {

    GraphicalScreen getGraphicalScreen();

    ConsoleScreen getConsoleScreen();

    void setComponentEnabled(boolean b);

    void sendMessage(Object str);

    void onScreen();

    void onInitComplete();

    void onDisable();

    int showMessagePane(String title, String message);
}
