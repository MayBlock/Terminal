package cn.newcraft.terminal.screen.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.internal.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.TextColor;
import cn.newcraft.terminal.screen.ScreenEvent;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.util.JsonUtils;
import org.jline.reader.*;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ConsoleScreen extends Thread implements Screen {


    private boolean inputScreenEnabled = false;
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
            try {
                Event.callEvent(new ScreenEvent.ScreenRefreshEvent(this));
            } catch (InvocationTargetException | IllegalAccessException e) {
                Terminal.printException(this.getClass(), e);
            }
            Terminal.getLogger().info(str);
            System.out.format(TextColor.codeTo(str + "\n", true));
        }
    }

    @Override
    public void onScreen() {
        System.out.format(TextColor.codeTo(TextColor.RED + "Warning! 在控制台模式下运行Terminal部分窗口模式功能将无法使用！\n", true));
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
            try {
                Event.callEvent(new ScreenEvent.ShowPaneEvent(title, message));
            } catch (InvocationTargetException | IllegalAccessException e) {
                Terminal.printException(this.getClass(), e);
            }
            System.out.println(title);
            System.out.println(message);
            System.out.print("> ");
        }
        return -1;
    }

    @Override
    public void run() {
        /*while (true) {
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext() && inputScreenEnabled) {
                if (Initialization.isInitialization) {
                    init.initFirst(sc.next());
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

         */
        Initialization init = new Initialization();
        try {
            org.jline.terminal.Terminal terminal = TerminalBuilder.builder()
                    .dumb(true)
                    .system(true)
                    .build();
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
            inputScreenEnabled = true;
            while (true) {
                String line;
                line = lineReader.readLine("> ").replaceAll("§", "");
                if (!line.isEmpty() && inputScreenEnabled) {
                    if (Initialization.isInitialization) {
                        init.initFirst(line);
                    } else {
                        Terminal.dispatchCommand(line);
                        if (Terminal.isDebug()) {
                            sendMessage(Prefix.DEBUG.getPrefix() + " 执行了命令：" + line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Terminal.printException(this.getClass(), e);
        }
    }
}
