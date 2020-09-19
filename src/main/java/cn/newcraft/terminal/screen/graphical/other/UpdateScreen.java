package cn.newcraft.terminal.screen.graphical.other;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;
import cn.newcraft.terminal.thread.ServerThread;
import cn.newcraft.terminal.update.Download;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarFile;

public class UpdateScreen extends JFrame {

    private GraphicalScreen graphicalScreen = Terminal.getScreen().getGraphicalScreen();
    private String newVersion;

    private static boolean update = false;

    public static boolean isUpdate() {
        return update;
    }

    private static UpdateScreen instance;

    public static UpdateScreen getInstance() {
        return instance;
    }

    public void showNotClosePane() {
        JOptionPane.showConfirmDialog(this, "当前终端正在更新中\n请不要试图关闭更新程序！", "错误", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    private Download download;
    private JLabel text;
    private JProgressBar jProgressBar;
    private JLabel downloadSpeed;
    private JLabel downloadFile;

    public UpdateScreen(String newVersion) {
        this.newVersion = newVersion;
        String[] buttons = {"确定", "取消"};
        int jOptionPane = JOptionPane.showOptionDialog(this, "即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！", Terminal.getName() + " Update", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
        if (jOptionPane == 0) {
            instance = this;
            Terminal.getScreen().sendMessage("Terminal updating...");
            graphicalScreen.setEnabled(false);
            graphicalScreen.setComponentEnabled(false);
            if (ServerThread.isServer()) {
                for (int i = 0; i < ServerThread.getIntegerSocketHashMap().size(); i++) {
                    try {
                        ServerThread.getIntegerSocketHashMap().get(i).disconnect("Server Closed");
                    } catch (IOException ignored) {
                    }
                }
                ServerThread.getServer().stopServer();
            }
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
                download = new Download("https://api.newcraft.cn/download/terminal/" + newVersion + "/terminal-" + newVersion + ".jar");
                download.download(new Thread(this::countDownload));
            }).start();
            setVisible(true);
        }
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
                    Method.copyFile("./update/terminal-" + newVersion + ".jar", Terminal.getProgramName());
                    for (int i = 10; i > 0; i--) {
                        text.setText("更新完毕，终端将在" + i + "秒后重启！");
                        Thread.sleep(1000);
                    }
                    Terminal.reboot();
                    break;
                }
            } catch (ArithmeticException | InterruptedException | IOException e) {
                Method.printException(this.getClass(), e);
            }
        }
    }
}
