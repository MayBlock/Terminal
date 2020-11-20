package cn.newcraft.terminal.screen.graphical;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.console.ConsoleEvent;
import cn.newcraft.terminal.screen.TextColor;
import cn.newcraft.terminal.screen.ScreenEvent;
import cn.newcraft.terminal.screen.console.ConsoleScreen;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.graphical.module.Button;
import cn.newcraft.terminal.screen.graphical.module.ScrollPane;
import cn.newcraft.terminal.screen.graphical.other.LoadScreen;
import cn.newcraft.terminal.screen.graphical.other.PromptScreen;
import cn.newcraft.terminal.update.graphical.GraphicalUpdate;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.config.ThemeConfig;
import cn.newcraft.terminal.internal.Initialization;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.console.Theme;
import cn.newcraft.terminal.util.JsonUtils;
import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class GraphicalScreen extends JFrame implements Screen {

    private static JTextPane text;
    private static JTextField input;
    private JLabel announcement = null;
    private JLabel copyright;
    private JLabel version;
    private JScrollPane scrollPane;
    private SystemTray tray;
    private TrayIcon trayIcon = null;
    private JButton execute, clearLog, theme;
    private List<String> cache = Lists.newArrayList();
    private int max_cache;
    private int jOptionPane = JOptionPane.DEFAULT_OPTION;
    private int messageType = JOptionPane.INFORMATION_MESSAGE;

    public JTextField getInput() {
        return input;
    }

    public JTextPane getTextArea() {
        return text;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
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

    public JLabel getCopyright() {
        return copyright;
    }

    public JLabel getVersion() {
        return version;
    }

    public JLabel getAnnouncement() {
        return announcement;
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
            ImageIcon icon = new ImageIcon(Terminal.getOptions().getImageResource());
            setIconImage(icon.getImage());
            setLayout(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle(Terminal.getName() + " - " + Terminal.getOptions().getVersion());
            setSize(900, 820);
            setLocationRelativeTo(null);
            Dimension dimension = new Dimension();
            dimension.setSize(754, 480);
            setMinimumSize(dimension);
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
            text = new JTextPane();
            text.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            text.setFont(new Font("宋体", Font.PLAIN, 15));
            //text.setLineWrap(true);
            //text.setWrapStyleWord(true);
            text.setEditable(false);
            add(text);

            Initialization init = new Initialization();

            JLabel title = new JLabel("控制台日志");
            title.setBounds(20, 20, 150, 20);
            add(title);

            try {
                announcement = new JLabel("公告：" + JsonUtils.getJsonURL("https://api.newcraft.cn/message/announcement.php", "message", "announcement").getAsString(), JLabel.CENTER);
            } catch (IOException e) {
                sendMessage("获取公告失败！ （错误：" + e.toString() + "）");
            }
            announcement.setForeground(Color.RED);
            announcement.setFont(new Font("宋体", Font.BOLD, 14));
            add(announcement);

            version = new JLabel("Version:  " + Terminal.getOptions().getCanonicalVersion());
            int[] versionLogOffset = {195, 60};
            add(version);

            copyright = new JLabel("©2020 May_Block 版权所有，保留所有权利");
            int[] copyrightOffset = {800, 60};
            add(copyright);

            scrollPane = new JScrollPane(text);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            scrollPane.getVerticalScrollBar().setUI(new ScrollPane());
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(scrollPane);

            max_cache = ServerConfig.cfg.getYml().getInt("server.max_input_cache");
            input = new JTextField();
            int[] inputLogOffset = {200, 95};
            input.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    String read = String.valueOf(e.getKeyChar());
                    if (read.equals("§")) {
                        e.consume();
                    }
                }
            });
            input.addActionListener(e -> {
                if (input.getText().isEmpty()) {
                    input.requestFocus();
                    return;
                }
                if (Initialization.isInitialization) {
                    init.initFirst(input.getText());
                } else {
                    String text = input.getText().replace("§", "");
                    Terminal.dispatchCommand(text);
                    if (!cache.isEmpty() && text.equals(cache.get(cache.size() - 1))) {
                        input.setText("");
                        input.requestFocus();
                        return;
                    }
                    cache.add(text);
                    if (cache.size() >= max_cache) {
                        cache.remove(0);
                    }
                }
                input.setText("");
                input.requestFocus();
            });
            input.addKeyListener(new KeyAdapter() {
                int count = cache.size() + 1;
                @Override
                public void keyPressed(KeyEvent e) {
                    if (count > cache.size()) {
                        count--;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP && !cache.isEmpty()) {
                        if (count <= 0) {
                            return;
                        }
                        count--;
                        input.setText(cache.get(count));
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN && !cache.isEmpty()) {
                        if (count >= cache.size() - 1) {
                            return;
                        }
                        count++;
                        input.setText(cache.get(count));
                        return;
                    }
                    count = cache.size() + 1;
                }
            });
            add(input);

            execute = new Button(">");
            int[] executeLogOffset = {175, 95};
            execute.setCursor(new Cursor(Cursor.HAND_CURSOR));
            execute.addActionListener(arg0 -> {
                if (input.getText().isEmpty()) {
                    input.requestFocus();
                    return;
                }
                if (Initialization.isInitialization) {
                    init.initFirst(input.getText());
                } else {
                    String text = input.getText().replace("§", "");
                    Terminal.dispatchCommand(text);
                    if (!cache.isEmpty() && text.equals(cache.get(cache.size() - 1))) {
                        input.setText("");
                        input.requestFocus();
                        return;
                    }
                    cache.add(text);
                    if (cache.size() >= max_cache) {
                        cache.remove(0);
                    }
                }
                input.setText("");
                input.requestFocus();
            });
            add(execute);

            clearLog = new Button("清除控制台日志");
            int[] clearLogOffset = {145, 780};
            clearLog.setCursor(new Cursor(Cursor.HAND_CURSOR));
            clearLog.addActionListener(arg0 -> {
                int jOptionPane = JOptionPane.showConfirmDialog(this, "确定要清理当前日志吗？", "清除日志", JOptionPane.YES_NO_OPTION);
                if (jOptionPane == 0) {
                    text.setText("");
                    sendMessage("已清除日志 - " + Method.getCurrentTime(Terminal.getOptions().getTimeZone()));
                    input.requestFocus();
                    try {
                        Event.callEvent(new ConsoleEvent.ClearMessageEvent(Terminal.getOptions().getTimeZone()));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        Terminal.printException(this.getClass(), e);
                    }
                }
            });
            add(clearLog);
            theme = new Button("切换主题");
            int[] themeOffset = {145, 740};
            theme.setCursor(new Cursor(Cursor.HAND_CURSOR));
            theme.addActionListener(arg0 -> {
                if (ThemeConfig.cfg.getYml().getConfigurationSection("theme") == null) {
                    JOptionPane.showConfirmDialog(this, "当前没有任何主题可用", "提示", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                    return;
                }
                List<String> id = Lists.newArrayList();
                List<String> name = Lists.newArrayList();
                for (String s : ThemeConfig.cfg.getYml().getConfigurationSection("theme").getKeys(false)) {
                    id.add(s);
                    name.add(Terminal.getTheme(s).getName());
                }
                Object jOptionPane = JOptionPane.showInputDialog(this, "请选择主题", "主题",
                        JOptionPane.INFORMATION_MESSAGE, null,
                        name.toArray(), name.get(0));
                if (jOptionPane == null) {
                    return;
                }
                for (int i = 0; i < name.size(); i++) {
                    if (jOptionPane.equals(name.get(i))) {
                        Theme.changeTheme(id.get(i));
                        sendMessage(Prefix.TERMINAL.getPrefix() + " 已切换主题 " + Terminal.getTheme(id.get(i)).getName());
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
                    try {
                        Event.callEvent(new ScreenEvent.GraphicalEvent.ScreenResizeEvent(getGraphicalScreen(), getWidth(), getHeight()));
                    } catch (InvocationTargetException | IllegalAccessException ex) {
                        Terminal.printException(GraphicalScreen.this.getClass(), ex);
                    }
                    text.setBounds(20, 40, getWidth() - 170, getHeight() - 150);
                    scrollPane.setBounds(20, 40, getWidth() - 170, getHeight() - 150);
                    input.setBounds(20, d.height - inputLogOffset[1], d.width - inputLogOffset[0], 30);
                    version.setBounds(d.width - versionLogOffset[0], d.height - versionLogOffset[1], d.width, 20);
                    copyright.setBounds(5, d.height - copyrightOffset[1], getWidth() - 200, 20);
                    announcement.setBounds(80, 5, getWidth() - 200, 20);
                    execute.setBounds(d.width - executeLogOffset[0], d.height - executeLogOffset[1], 41, 30);
                    clearLog.setBounds(d.width - clearLogOffset[0], 40, 125, 30);
                    theme.setBounds(d.width - themeOffset[0], 80, 125, 30);
                    super.componentResized(e);
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
            Terminal.printException(this.getClass(), e);
        }
        new LoadScreen().close();
    }

    public void showPromptScreen(String title, String message, int keepTime, boolean confirm, String confirmMessage) {
        new PromptScreen().show(title, message, keepTime, confirm, confirmMessage);
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
    public void onUpdate(String newVersion) {
        Terminal.getUpdate().confirmUpdate();
    }

    @Override
    public int showMessagePane(String title, String message) {
        try {
            Event.callEvent(new ScreenEvent.ShowPaneEvent(title, message));
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
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
                showPromptScreen("提示", "已隐藏至任务栏\n终端仍在运行，如需唤出界面请双击任务栏图标", 5000, true, "知道了");
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
    public synchronized void sendMessage(Object str) {
        String string = String.valueOf(str);
        try {
            Event.callEvent(new ScreenEvent.ScreenRefreshEvent(this));
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
        System.out.format(TextColor.codeTo(string + "\n", true));
        try {
            StyledDocument d = text.getStyledDocument();
            SimpleAttributeSet attr = new SimpleAttributeSet();
            if (string.contains("§")) {
                for (String s : string.split("§")) {
                    if (!s.isEmpty()) {
                        String s1 = "§" + s;
                        TextColor screenColor = new TextColor(s1);
                        if (screenColor.getColor() != null) {
                            StyleConstants.setForeground(attr, screenColor.getColor());
                        }
                        if (s1.contains("§l")) {
                            StyleConstants.setBold(attr, true);
                        }
                        if (s1.contains("§n")) {
                            StyleConstants.setUnderline(attr, true);
                        }
                        if (s1.contains("§m")) {
                            StyleConstants.setStrikeThrough(attr, true);
                        }
                        if (s1.contains("§o")) {
                            StyleConstants.setItalic(attr, true);
                        }
                        d.insertString(d.getLength(), TextColor.codeTo(screenColor.getString(), false), attr);
                    }
                }
                d.insertString(d.getLength(), "\n", attr);
                return;
            }
            d.insertString(d.getLength(), string + "\n", attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        Terminal.getLogger().info(string);
    }

    private void initTray() {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            ImageIcon trayImg = new ImageIcon(this.getClass().getResource("/console.png"));
            PopupMenu pm = new PopupMenu();
            MenuItem mi0 = new MenuItem("打开");
            mi0.setFont(new Font("宋体", Font.BOLD, 13));
            mi0.addActionListener(e -> {
                if (!Terminal.getUpdate().isUpdate()) {
                    maximize();
                }
            });
            MenuItem mi1 = new MenuItem("关于");
            mi1.setFont(new Font("宋体", Font.PLAIN, 13));
            mi1.addActionListener(e -> showPromptScreen(
                    "Terminal - " + Terminal.getOptions().getCanonicalVersion(),
                    "版权所有 ©2020 May_Block\n保留所有权利", 2500, false, null));
            MenuItem mi2 = new MenuItem("退出程序");
            mi2.setFont(new Font("宋体", Font.PLAIN, 13));
            mi2.addActionListener(e -> {
                if (!Terminal.getUpdate().isUpdate()) {
                    Terminal.shutdown();
                } else {
                    new GraphicalUpdate().showNotClosePane();
                }
            });
            pm.add(mi0);
            pm.add(mi1);
            pm.addSeparator();
            pm.add(mi2);
            trayIcon = new TrayIcon(trayImg.getImage(), "Terminal - " + Terminal.getOptions().getVersion(), pm);
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
