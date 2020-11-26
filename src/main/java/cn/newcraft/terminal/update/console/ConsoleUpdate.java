package cn.newcraft.terminal.update.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.console.ConsoleEvent;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.TextColor;
import cn.newcraft.terminal.screen.console.other.ConsoleProgressBar;
import cn.newcraft.terminal.update.Download;
import cn.newcraft.terminal.update.Update;
import cn.newcraft.terminal.util.JsonUtils;
import cn.newcraft.terminal.util.Method;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;

public class ConsoleUpdate implements Update {

    private String url;
    private String version;
    private String newVersion;
    private int apiVersion;
    private String description;
    private String canonicalVersion;
    private boolean forceUpdate;
    private boolean update = false;
    private Download download;
    private ConsoleProgressBar progressBar;

    private Screen screen = Terminal.getScreen();

    @Override
    public void refreshUpdate() {
        try {
            this.canonicalVersion = Terminal.getOptions().getCanonicalVersion();
            URL url = new URL("https://api.newcraft.cn/update.php?version=" + canonicalVersion);
            this.url = JsonUtils.getJsonURL(url, "Terminal", "url").getAsString();
            this.version = JsonUtils.getJsonURL(url, "Terminal", "latest", "version").getAsString();
            this.newVersion = JsonUtils.getJsonURL(url, "Terminal", "latest", "canonical").getAsString();
            this.apiVersion = JsonUtils.getJsonURL(url, "Terminal", "latest", "api").getAsInt();
            this.description = JsonUtils.getJsonURL(url, "Terminal", "description").getAsString();
            this.forceUpdate = JsonUtils.getJsonURL(url, "Terminal", "force_update").getAsBoolean();
        } catch (IOException e) {
            Terminal.printException(this.getClass(), e);
        }
    }

    @Override
    public void checkUpdate(boolean ret) {
        if (url != null && !newVersion.equals(canonicalVersion)) {
            if (forceUpdate) {
                try {
                    screen.sendMessage(Terminal.getName() + " Update");
                    screen.sendMessage("即将更新至版本 " + newVersion + "\n更新日志：" + description + "\n\n更新完毕后终端将会自动进行重启\n该更新为强制更新，点击确定后将开始更新！");
                    screen.sendMessage("终端将在10秒后自动进行更新！");
                    Thread.sleep(1000 * 10);
                    startUpdate();
                } catch (InterruptedException e) {
                    Terminal.printException(this.getClass(), e);
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
            if (apiVersion != Terminal.getOptions().getApiVersion()) {
                screen.sendMessage("即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！");
            } else {
                screen.sendMessage("即将更新至版本 " + newVersion + "\n更新完毕后终端将会自动进行重启，请确保当前没有任何进行中的操作！\n\n" + TextColor.RED + "该版本API版本已更新至API" + apiVersion + "\n如进行更新请保证当前插件支持API" + apiVersion + "！");
            }
            screen.sendMessage("如需确定更新，请再次输入一次 \"update latest\" 以确认操作！");
        } else {
            screen.sendMessage("更新失败，当前已经为最新版本！");
        }
    }

    @Override
    public void startUpdate() {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            screen.sendMessage("Terminal updating...");
            update = true;
            try {
                Event.callEvent(new ConsoleEvent.UpdateEvent(newVersion, description, forceUpdate));
            } catch (InvocationTargetException | IllegalAccessException e) {
                Terminal.printException(this.getClass(), e);
            }
            screen.getConsoleScreen().setComponentEnabled(false);
            screen.getConsoleScreen().setShowMessageEnabled(false);
            progressBar = new ConsoleProgressBar(0, 100, 50, '=');
            new Thread(() -> {
                download = new Download(url);
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
                    Method.copyFile("./update/" + download.getFileName(), Terminal.getProgramName());
                    for (int i = 10; i > 0; i--) {
                        screen.sendMessage("更新完毕，终端将在" + i + "秒后重启！");
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
