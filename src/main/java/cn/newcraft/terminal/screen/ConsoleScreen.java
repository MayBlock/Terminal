package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.util.JsonUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.Scanner;

public class ConsoleScreen extends Thread implements Screen {

    private String announcement = JsonUtils.getStringJson("https://api.newcraft.cn/message/announcement.php", "message", "announcement", true);

    @Override
    public ConsoleScreen getConsoleScreen() {
        return this;
    }

    @Override
    public void setComponentEnabled(boolean b) {

    }

    @Override
    public GraphicalScreen getGraphicalScreen() {
        return null;
    }

    @Override
    public void sendMessage(Object str) {
        Logger.getLogger(Terminal.class).info(str);
        System.out.println(str);
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
    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext()) {
                if (Initialization.isInitialization) {
                    Initialization.init(sc.next());
                } else {
                    String in = sc.nextLine();
                    new SendCommand(in.split(" "));
                    if (Terminal.getInstance().isDebug()) {
                        sendMessage(Prefix.DEBUG.getPrefix() + " 执行了命令：" + in);
                    }
                    System.out.print("> ");
                }
            }
        }
    }
}
