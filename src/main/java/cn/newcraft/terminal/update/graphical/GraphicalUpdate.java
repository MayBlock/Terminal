package cn.newcraft.terminal.update.graphical;

import cn.newcraft.terminal.Prefix;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.console.ConsoleEvent;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.graphical.other.PromptScreen;
import cn.newcraft.terminal.update.Download;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.JsonUtils;
import cn.newcraft.terminal.util.Method;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;

public class GraphicalUpdate extends JFrame implements Update {

    private String url;
    private String version;
    private String newVersion;
    private int apiVersion;
    private String description;
    private String canonicalVersion;
    private boolean forceUpdate = false;
    private boolean update = false;

    private Screen screen = Terminal.getScreen();

    public void showNotClosePane() {
        JOptionPane.showConfirmDialog(this, "当前终端正在更新中\n请不要试图关闭更新程序！", "错误", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    private Download download;
    private JLabel text;
    private JProgressBar jProgressBar;
    private JLabel downloadSpeed;
    private JLabel downloadFile;

    @Override
    public void refreshUpdate() throws IOException, NullPointerException {
        this.canonicalVersion = Terminal.getOptions().getCanonicalVersion();
        URL url = new URL("https://api.newcraft.cn/update.php?version=" + canonicalVersion);
        this.url = JsonUtils.getJsonURL(url, "Terminal", "url").getAsString();
        this.version = JsonUtils.getJsonURL(url, "Terminal", "latest", "version").getAsString();
        this.newVersion = JsonUtils.getJsonURL(url, "Terminal", "latest", "canonical").getAsString();
        this.apiVersion = JsonUtils.getJsonURL(url, "Terminal", "latest", "api").getAsInt();
        this.description = JsonUtils.getJsonURL(url, "Terminal", "description").getAsString();
        this.forceUpdate = JsonUtils.getJsonURL(url, "Terminal", "force_update").getAsBoolean();
    }

    @Override
    public void checkUpdate(boolean ret) {
        new GraphicalUpdate();
        if (url != null && !newVersion.equals(canonicalVersion)) {
            if (forceUpdate) {
                JOptionPane.showConfirmDialog(screen.getGraphicalScreen(), "即将更新至版本 " + newVersion + "\n更新日志：" + description + "\n\n更新完毕后终端将会自动进行重启\n该更新为强制更新，点击确定后将开始更新！", Terminal.getName() + " Update", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                startUpdate();
                return;
            }
            PromptScreen promptScreen = new PromptScreen();
            JButton determine = new JButton("点击更新");
            determine.setFont(new Font("宋体", Font.PLAIN, 14));
            determine.setBounds(120, 140, 70, 30);
            determine.setCursor(new Cursor(12));
            determine.setContentAreaFilled(false);
            determine.setBorder(BorderFactory.createRaisedBevelBorder());
            determine.setBackground(Color.decode("#3366FF"));
            determine.addActionListener(arg0 -> {
                confirmUpdate();
                promptScreen.close();
            });
            promptScreen.show("检测到有新版本！",
                    "当前版本：" + Terminal.getOptions().getVersion() + " (" + canonicalVersion + ")\n" +
                            "最新版本：" + version + " (" + newVersion + ")\n\n" +
                            "更新日志：" + description, 8000, determine);
        } else if (ret) {
            screen.sendMessage("当前版本已为最新版本！");
        }
    }

    @Override
    public void confirmUpdate() {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            String[] buttons = {"确定", "取消"};
            JLabel label;
            if (apiVersion != Terminal.getOptions().getApiVersion()) {
                label = new JLabel("<html><p align=\'center\'>即将更新至版本 " + newVersion + "</p><p align=\'center\'>更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！</p><p></p><font color=\'#FF0000\'><p align=\'center\'>该版本API版本已更新至API" + apiVersion + "</p><p align=\'center\'>如进行更新请保证当前插件支持API" + apiVersion + "！</p></font></html>");
            } else {
                label = new JLabel("<html><p align=\'center\'>即将更新至版本 " + newVersion + "</p><p align=\'center\'>更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！</p</html>");
            }
            int jOptionPane = JOptionPane.showOptionDialog(this, label, Terminal.getName() + " Update", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
            if (jOptionPane == 0) {
                startUpdate();
            }
        } else {
            screen.sendMessage("更新失败，当前已经为最新版本！");
        }
    }

    @Override
    public void startUpdate() {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            screen.sendMessage("Terminal updating...");
            screen.getGraphicalScreen().setEnabled(false);
            screen.getGraphicalScreen().setComponentEnabled(false);
            Terminal.getServer().shutdown();
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/console.png"));
            setIconImage(icon.getImage());
            setLayout(null);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle(Terminal.getName() + " Update");

            setSize(550, 200);
            setLocationRelativeTo(null);
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
            update = true;
            try {
                Event.callEvent(new ConsoleEvent.UpdateEvent(newVersion, description, forceUpdate));
            } catch (InvocationTargetException | IllegalAccessException e) {
                Terminal.printException(this.getClass(), e);
            }
            text = new JLabel("即将开始更新，更新过程中请不要关闭终端");
            text.setForeground(Color.RED);
            text.setBounds(135, 100, 300, 20);
            add(text);

            downloadSpeed = new JLabel();
            downloadSpeed.setForeground(Color.BLUE);
            downloadSpeed.setBounds(135, 40, 300, 20);
            add(downloadSpeed);

            downloadFile = new JLabel();
            downloadFile.setForeground(Color.BLUE);
            downloadFile.setBounds(205, 40, 300, 20);
            add(downloadFile);

            jProgressBar = new JProgressBar();
            jProgressBar.setStringPainted(true);
            jProgressBar.setBounds(115, 60, 300, 30);
            jProgressBar.setString("即将开始更新");
            add(jProgressBar);
            new Thread(() -> {
                download = new Download(url);
                download.download(new Thread(this::countDownload));
            }).start();
            setVisible(true);
        } else {
            screen.sendMessage("更新失败，当前已经为最新版本！");
        }
    }

    @Override
    public boolean isUpdate() {
        return update;
    }

    @Override
    public void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            showNotClosePane();
            return;
        }
        super.processWindowEvent(e);
    }

    private void countDownload() {
        text.setText("正在进行更新，更新过程中请不要关闭终端");
        while (download.getCurrentLength() < download.getContentLength()) {
            try {
                Thread.sleep(1000);
                BigDecimal bigDecimal = new BigDecimal((double) (download.getCurrentLength() * 100 / download.getContentLength()));
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                jProgressBar.setValue(bigDecimal.intValue());
                jProgressBar.setString("更新中... " + bigDecimal.intValue() + "%");
                downloadSpeed.setText(download.formatLength(download.getCurrentLength() - download.getPreLength()) + "/s");
                downloadFile.setText(download.formatLength(download.getCurrentLength()) + " / " + download.formatLength(download.getContentLength()));
                download.setPreLength(download.getCurrentLength());
                if (bigDecimal.intValue() >= 100) {
                    update = false;
                    disableEvents(AWTEvent.WINDOW_EVENT_MASK);
                    jProgressBar.setString("更新完毕！");
                    Method.copyFile("./update/" + download.getFileName(), Terminal.getProgramName());
                    for (int i = 5; i > 0; i--) {
                        text.setText("更新完毕，终端将在" + i + "秒后重启！");
                        Thread.sleep(1000);
                    }
                    Terminal.reboot();
                    break;
                }
            } catch (ArithmeticException | InterruptedException | IOException e) {
                Terminal.printException(this.getClass(), e);
            }
        }
    }
}
