package cn.newcraft.terminal.update.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.console.other.ConsoleProgressBar;
import cn.newcraft.terminal.update.Download;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.Method;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class ConsoleUpdate implements Update {

    private String version;
    private String newVersion;
    private String description;
    private String canonicalVersion;
    private boolean forceUpdate;
    private boolean update = false;
    private Download download;
    private ConsoleProgressBar progressBar;

    private Screen screen = Terminal.getScreen();
    private static ConsoleUpdate instance;

    public static ConsoleUpdate getInstance() {
        return instance;
    }

    public ConsoleUpdate() {
        try {
            this.canonicalVersion = Terminal.getOptions().getCanonicalVersion();
            URL url = new URL("https://api.newcraft.cn/update.php?version=" + canonicalVersion);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String response = reader.readLine();
            this.version = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("version");
            this.newVersion = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("canonical");
            this.description = JSONObject.parseObject(response).getJSONObject("Terminal").getString("description");
            this.forceUpdate = JSONObject.parseObject(response).getJSONObject("Terminal").getBoolean("force_update");
        } catch (IOException e) {
            Method.printException(this.getClass(), e);
        }
    }

    @Override
    public void checkUpdate(boolean ret) {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            if (forceUpdate) {
                try {
                    screen.sendMessage(Terminal.getName() + " Update");
                    screen.sendMessage("即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启\n该更新为强制更新，点击确定后将开始更新！");
                    screen.sendMessage("终端将在10秒后自动进行更新！");
                    Thread.sleep(1000 * 10);
                    startUpdate();
                } catch (InterruptedException e) {
                    Method.printException(this.getClass(), e);
                }
                return;
            }
            screen.sendMessage("-----检测到有新版本-----");
            screen.sendMessage("当前版本：" + Terminal.getOptions().getVersion() + " (" + canonicalVersion + ")");
            screen.sendMessage("最新版本：" + version + " (" + newVersion + ")\n");
            screen.sendMessage("更新日志：" + description);
            screen.sendMessage("更新请输入命令 \"update latest\" 即可进行更新操作！");
        } else if (ret) {
            screen.sendMessage("当前版本已为最新版本！");
        }
    }

    @Override
    public void confirmUpdate() {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            screen.sendMessage(Terminal.getName() + " Update");
            screen.sendMessage("即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！");
            screen.sendMessage("如需确定更新，请再次输入一次 \"update latest\" 以确认操作！");
        } else {
            screen.sendMessage("更新失败，当前已经为最新版本！");
        }
    }

    @Override
    public void startUpdate() {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            instance = this;
            screen.sendMessage("Terminal updating...");
            update = true;
            screen.getConsoleScreen().setComponentEnabled(false);
            screen.getConsoleScreen().setShowMessageEnabled(false);
            progressBar = new ConsoleProgressBar(0, 100, 50, '=');
            new Thread(() -> {
                download = new Download("https://api.newcraft.cn/download/terminal/" + newVersion + "/terminal-" + newVersion + ".jar");
                download.download(new Thread(this::countDownload));
            }).start();
        } else {
            screen.sendMessage("更新失败，当前已经为最新版本！");
        }
    }

    @Override
    public boolean isUpdate() {
        return update;
    }

    private void countDownload() {
        screen.sendMessage("正在进行更新，更新过程中请不要关闭终端");
        while (download.getCurrentLength() < download.getContentLength()) {
            try {
                Thread.sleep(1000);
                BigDecimal bigDecimal = new BigDecimal((double) (download.getCurrentLength() * 100 / download.getContentLength()));
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                progressBar.show(bigDecimal.intValue());
                progressBar.setPrefix("更新中...  ");
                progressBar.setString(bigDecimal.intValue() + "%    " +
                        download.formatLength(download.getCurrentLength() - download.getPreLength()) + "/s" + "    " +
                        download.formatLength(download.getCurrentLength()) + " / " + download.formatLength(download.getContentLength()));
                download.setPreLength(download.getCurrentLength());
                if (bigDecimal.intValue() >= 100) {
                    update = false;
                    screen.getConsoleScreen().setShowMessageEnabled(true);
                    progressBar.setString("更新完毕！");
                    Method.copyFile("./update/terminal-" + newVersion + ".jar", Terminal.getProgramName());
                    for (int i = 10; i > 0; i--) {
                        screen.sendMessage("更新完毕，终端将在" + i + "秒后重启！");
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
