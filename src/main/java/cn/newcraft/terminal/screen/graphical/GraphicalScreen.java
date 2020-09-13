package cn.newcraft.terminal.screen.graphical;

import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.console.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.SendCommand;
import cn.newcraft.terminal.console.Theme;
import cn.newcraft.terminal.util.JsonUtils;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GraphicalScreen extends JFrame implements Screen {

    private static JTextArea text;
    private static JTextField input;
    private SystemTray tray;
    private TrayIcon trayIcon = null;
    private JButton execute, clearLog, theme;
    private List<String> cache = Lists.newArrayList();
    private int max_cache;
    private int jOptionPane = JOptionPane.DEFAULT_OPTION;
    private int messageType = JOptionPane.INFORMATION_MESSAGE;
    private String announcement = JsonUtils.getStringJson("https://api.newcraft.cn/message/announcement.php", "message", "announcement", true);

    public JTextField getInput() {
        return input;
    }

    public JTextArea getTextArea() {
        return text;
    }

    public JButton getExecute() {
        return execute;
    }

    public JButton getClearLog() {
        return clearLog;
    }

    public JButton getTheme() {
        return theme;
    }

    public SystemTray getTray() {
        return tray;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    @Override
    public void onScreen() {
        try {
            Collections.reverse(cache);
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/console.png"));
            setIconImage(icon.getImage());
            setLayout(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle(Terminal.getInstance().getName() + " - " + Terminal.getInstance().getSetting().getVersion());
            setSize(900, 820);
            setLocation(200, 200);
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
            text = new JTextArea();
            text.setFont(new Font("宋体", Font.PLAIN, 15));
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setEditable(false);
            add(text);

            JLabel title = new JLabel("控制台日志");
            title.setBounds(20, 20, 150, 20);
            add(title);

            JLabel str = new JLabel("公告：" + announcement);
            str.setForeground(Color.RED);
            str.setFont(new Font("宋体", Font.BOLD, 14));
            str.setBounds(180, 5, 500, 20);
            add(str);

            JLabel version = new JLabel("Version:  " + Terminal.getInstance().getSetting().getCanonicalVersion());
            int[] versionLogOffset = {185, 60};
            add(version);

            JScrollPane jsp = new JScrollPane(text);
            jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(jsp);

            max_cache = ServerConfig.cfg.getYml().getInt("server.max_input_cache");
            input = new JTextField();
            int[] inputLogOffset = {200, 95};
            input.addActionListener(e -> {
                if (input.getText().isEmpty()) {
                    input.requestFocus();
                    return;
                }
                if (Initialization.isInitialization) {
                    Initialization.init(input.getText());
                } else {
                    new SendCommand(input.getText().split(" "));
                    if (!cache.isEmpty() && input.getText().equals(cache.get(cache.size() - 1))) {
                        input.setText("");
                        input.requestFocus();
                        return;
                    }
                    cache.add(input.getText());
                    if (cache.size() >= max_cache) {
                        cache.remove(0);
                    }
                    if (Terminal.getInstance().isDebug()) {
                        sendMessage(Prefix.DEBUG.getPrefix() + " 执行了命令：" + input.getText());
                    }
                }
                input.setText("");
                input.requestFocus();
            });
            input.addKeyListener(new KeyAdapter() {
                int timesUp = 0;
                int timesDown = 0;

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        try {
                            input.setText(cache.get(cache.size() - 1 - timesUp));
                            timesUp++;
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                        timesDown = 0;
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        try {
                            input.setText(cache.get(timesDown));
                            timesDown++;
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                        timesUp = 0;
                        return;
                    }
                    timesUp = 0;
                    timesDown = 0;
                }
            });
            add(input);

            execute = new JButton(">");
            int[] executeLogOffset = {175, 95};
            execute.addActionListener(arg0 -> {
                if (input.getText().isEmpty()) {
                    input.requestFocus();
                    return;
                }
                if (Initialization.isInitialization) {
                    Initialization.init(input.getText());
                } else {
                    new SendCommand(input.getText().split(" "));
                    if (!cache.isEmpty() && input.getText().equals(cache.get(cache.size() - 1))) {
                        input.setText("");
                        input.requestFocus();
                        return;
                    }
                    cache.add(input.getText());
                    if (cache.size() >= max_cache) {
                        cache.remove(0);
                    }
                    if (Terminal.getInstance().isDebug()) {
                        sendMessage(Prefix.DEBUG.getPrefix() + " 执行了命令：" + input.getText());
                    }
                }
                input.setText("");
                input.requestFocus();
            });
            add(execute);

            clearLog = new JButton("清除控制台日志");
            int[] clearLogOffset = {145, 780};
            clearLog.addActionListener(arg0 -> {
                int jOptionPane = JOptionPane.showConfirmDialog(null, "确定要清理当前日志吗？", "清除日志", JOptionPane.YES_NO_OPTION);
                if (jOptionPane == 0) {
                    text.setText("");
                    sendMessage("已清除日志 - " + Method.getCurrentTime(Terminal.getInstance().getSetting().getTimeZone()));
                    input.requestFocus();
                }
            });
            add(clearLog);

            theme = new JButton("切换主题");
            int[] themeOffset = {145, 740};
            theme.addActionListener(arg0 -> {
                if (ThemeConfig.cfg.getYml().getConfigurationSection("theme") == null) {
                    JOptionPane.showConfirmDialog(null, "当前没有任何主题可用", "提示", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                    return;
                }
                List<String> id = Lists.newArrayList();
                List<String> name = Lists.newArrayList();
                for (String s : ThemeConfig.cfg.getYml().getConfigurationSection("theme").getKeys(false)) {
                    id.add(s);
                    name.add(Terminal.getInstance().getSetting().getTheme(s).getName());
                }
                Object jOptionPane = JOptionPane.showInputDialog(null, "请选择主题", "主题",
                        JOptionPane.INFORMATION_MESSAGE, null,
                        name.toArray(), name.get(0));
                if (jOptionPane == null) {
                    return;
                }
                for (int i = 0; i < name.size(); i++) {
                    if (jOptionPane.equals(name.get(i))) {
                        Theme.changeTheme(id.get(i));
                        sendMessage(Prefix.TERMINAL.getPrefix() + " 已切换主题 " + Terminal.getInstance().getSetting().getTheme(id.get(i)).getName());
                    }
                }
            });
            add(theme);
            text.setCaretColor(Color.RED);

            /* Default Theme */
            Theme.changeTheme(ServerConfig.cfg.getYml().getString("server.default_theme"));
            /* Default Theme */

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension d = getSize();
                    text.setBounds(20, 40, getWidth() - 170, getHeight() - 150);
                    jsp.setBounds(20, 40, getWidth() - 170, getHeight() - 150);
                    input.setBounds(20, d.height - inputLogOffset[1], d.width - inputLogOffset[0], 30);
                    version.setBounds(d.width - versionLogOffset[0], d.height - versionLogOffset[1], 170, 20);
                    execute.setBounds(d.width - executeLogOffset[0], d.height - executeLogOffset[1], 41, 30);
                    clearLog.setBounds(d.width - clearLogOffset[0], 40, 125, 30);
                    theme.setBounds(d.width - themeOffset[0], 80, 125, 30);
                }
            });
            this.getExecute().setEnabled(false);
            this.getClearLog().setEnabled(false);
            this.getTheme().setEnabled(false);
            this.getInput().setEnabled(false);
            this.getInput().setBackground(Color.LIGHT_GRAY);
            initTray();
            setVisible(true);
        } catch (Exception e) {
            Method.printException(this.getClass(), e);
        }
    }

    public void showPromptScreen(String title, String message, int keepTime, boolean confirm) {
        new PromptScreen().show(title, message, keepTime, confirm);
    }

    public void setJOptionPane(int i) {
        this.jOptionPane = i;
    }

    public void setMessageType(int i) {
        this.messageType = i;
    }

    @Override
    public void onInitComplete() {

    }

    @Override
    public void onDisable() {
        tray.remove(trayIcon);
    }

    @Override
    public int showMessagePane(String title, String message) {
        return JOptionPane.showOptionDialog(this, message, title, jOptionPane, messageType, null, null, null);
    }

    @Override
    public void setComponentEnabled(boolean b) {
        if (b) {
            this.getExecute().setEnabled(true);
            this.getClearLog().setEnabled(true);
            this.getTheme().setEnabled(true);
            this.getInput().setEnabled(true);
            this.getInput().setBackground(Color.decode(ThemeConfig.cfg.getYml().getString("theme." + ServerConfig.cfg.getYml().getString("server.default_theme") + ".input.background")));
        } else {
            this.getExecute().setEnabled(false);
            this.getClearLog().setEnabled(false);
            this.getTheme().setEnabled(false);
            this.getInput().setEnabled(false);
            this.getInput().setBackground(Color.LIGHT_GRAY);
        }
    }

    @Override
    public void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            List<String> list = Lists.newArrayList();
            list.add("关闭终端");
            if (SystemTray.isSupported()) {
                list.add("隐藏至任务栏");
            }
            list.add("取消");
            String[] buttons = list.toArray(new String[0]);
            int jOptionPane = JOptionPane.showOptionDialog(this, "确定要关闭终端吗？\n关闭后尚未执行完毕的操作将会丢失！", "警告", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[list.size() - 1]);
            if (jOptionPane == 0) {
                Terminal.shutdown();
            }
            if (jOptionPane == list.size() - 2) {
                showPromptScreen("提示", "已隐藏至任务栏\n终端仍在运行，如需唤出界面请双击任务栏图标", 5000, true);
                minimize();
            }
            return;
        }
        super.processWindowEvent(e);
    }

    @Override
    public GraphicalScreen getGraphicalScreen() {
        return this;
    }

    @Override
    public ConsoleScreen getConsoleScreen() {
        return null;
    }

    @Override
    public void sendMessage(Object str) {
        text.append(str + "\n");
        Logger.getLogger(Terminal.class).info(str);
        System.out.println(str);
    }

    private void initTray() {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            ImageIcon trayImg = new ImageIcon(this.getClass().getResource("/console.png"));
            PopupMenu pm = new PopupMenu();
            MenuItem mi0 = new MenuItem("打开");
            mi0.addActionListener(e -> maximize());
            MenuItem mi1 = new MenuItem("关于");
            mi1.addActionListener(e -> showPromptScreen(
                    "Terminal - " + Terminal.getInstance().getSetting().getCanonicalVersion(),
                    "版权所有 ©2020 May_Block\n保留所有权利", 2500, false));
            MenuItem mi2 = new MenuItem("退出程序");
            mi2.addActionListener(e -> Terminal.shutdown());
            pm.add(mi0);
            pm.add(mi1);
            pm.add(mi2);
            trayIcon = new TrayIcon(trayImg.getImage(), "Terminal", pm);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1 && e.getClickCount() == 2) {
                        maximize();
                    }
                }
            });
            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void minimize() {
        this.setVisible(false);
    }

    private void maximize() {
        this.setVisible(true);
        this.setExtendedState(JFrame.NORMAL);
        this.toFront();
    }
}
