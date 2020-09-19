package cn.newcraft.terminal.update;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.screen.graphical.other.PromptScreen;
import cn.newcraft.terminal.util.Method;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Update {

    public Update(String canonicalVersion) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.newcraft.cn/update.php?version=" + canonicalVersion);
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                InputStream is = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String response = reader.readLine();
                String newVersion = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("canonical");
                if (newVersion != null && !newVersion.equals(canonicalVersion)) {
                    String version = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("version");
                    String description = JSONObject.parseObject(response).getJSONObject("Terminal").getString("description");
                    PromptScreen promptScreen = new PromptScreen();
                    JButton determine = new JButton("点击更新");
                    determine.setFont(new Font("宋体", Font.PLAIN, 14));
                    determine.setBounds(120, 140, 70, 30);
                    determine.setCursor(new Cursor(12));
                    determine.setContentAreaFilled(false);
                    determine.setBorder(BorderFactory.createRaisedBevelBorder());
                    determine.setBackground(Color.decode("#3366FF"));
                    determine.addActionListener(arg0 -> {
                        Terminal.getScreen().onUpdate(newVersion);
                        promptScreen.close();
                    });
                    promptScreen.show("检测到有新版本！",
                            "当前版本：" + Terminal.getOptions().getVersion() + " (" + canonicalVersion + ")\n" +
                                    "最新版本：" + version + " (" + newVersion + ")\n\n" +
                                    "更新日志：" + description, 8000, determine);
                }
            } catch (IOException e) {
                Method.printException(this.getClass(), e);
            }
        }).start();
    }
}
