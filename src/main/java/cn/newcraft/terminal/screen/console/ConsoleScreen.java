package cn.newcraft.terminal.screen.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.util.JsonUtils;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class ConsoleScreen extends Thread implements Screen {


    private boolean inputScreenEnabled = true;
    private boolean showMessageEnabled = true;
    private boolean showMessagePaneEnabled = true;
    private String announcement = JsonUtils.getStringJson("https://api.newcraft.cn/message/announcement.php", "message", "announcement", true);

    public void setInputScreenEnabled(boolean b) {
        this.inputScreenEnabled = b;
    }

    public void setShowMessageEnabled(boolean b) {
        this.showMessageEnabled = b;
    }

    public void setShowMessagePaneEnabled(boolean b) {
        this.showMessagePaneEnabled = b;
    }

    @Override
    public ConsoleScreen getConsoleScreen() {
        return this;
    }

    @Override
    public void setComponentEnabled(boolean b) {
        this.inputScreenEnabled = b;
        this.showMessagePaneEnabled = b;
    }

    @Override
    public GraphicalScreen getGraphicalScreen() {
        return null;
    }

    @Override
    public void sendMessage(Object str) {
        if (showMessageEnabled) {
            Logger.getLogger(Terminal.class).info(str);
            System.out.println(str);
        }
    }

    @Override
    public void onScreen() {
        System.out.println("\033[31;1m" + "Warning! 在控制台模式下运行Terminal部分窗口模式功能将无法使用！" + "\033[0m");
        this.start();
    }

    @Override
    public void onInitComplete() {
        sendMessage("-------------------------\n公告：" + announcement + "\n-------------------------");
        System.out.print("> ");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUpdate(String newVersion) {
        sendMessage("Terminal updating...");
    }

    @Override
    @Deprecated
    public int showMessagePane(String title, String message) {
        if (showMessagePaneEnabled) {
            System.out.println(title);
            System.out.println(message);
            System.out.print("> ");
        }
        return -1;
    }

    @Override
    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext() && inputScreenEnabled) {
                if (Initialization.isInitialization) {
                    Initialization.init(sc.next());
                } else {
                    String in = sc.nextLine();
                    new SendCommand(in.split(" "));
                    if (Terminal.isDebug()) {
                        sendMessage(Prefix.DEBUG.getPrefix() + " 执行了命令：" + in);
                    }
                    System.out.print("> ");
                }
            }
        }
    }
}
