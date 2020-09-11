package cn.newcraft.terminal.screen;

public interface Screen {

    GraphicalScreen getGraphicalScreen();

    ConsoleScreen getConsoleScreen();

    void setComponentEnabled(boolean b);

    void sendMessage(Object str);

    void onScreen();

    void onInitComplete();
}
