package cn.newcraft.terminal.util;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import com.sun.management.OperatingSystemMXBean;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Method {

    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static boolean isLocalPortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isConnect() {
        return JsonUtils.getBooleanJson("https://api.newcraft.cn/verify/internet.php", "internet", "active", true);
    }

    public static void runCmd(String command) {
        new Thread(() -> {
            BufferedReader br = null;
            String line;
            try {
                Process p = Runtime.getRuntime().exec(command);
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = br.readLine()) != null) {
                    Terminal.getScreen().sendMessage(line);
                }
            } catch (Exception e) {
                Method.printException(Method.class, e);
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        Method.printException(Method.class, e);
                    }
                }
            }
        }).start();
    }

    public static boolean isReallyHeadless() {
        if (GraphicsEnvironment.isHeadless()) {
            return true;
        }
        try {
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            return screenDevices == null || screenDevices.length == 0;
        } catch (HeadlessException e) {
            Method.printException(Method.class, e);
            return true;
        }
    }

    public static void printException(Class clazz, Exception ex) {
        ex.printStackTrace();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        ex.printStackTrace(ps);
        try {
            String output = os.toString("UTF-8");
            Terminal.getScreen().sendMessage("\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生错误，以下为错误报告\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 错误名称：" + ex.getMessage() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生的类：" + clazz.getName() + "\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 发生时间：" + getCurrentTime(Terminal.getOptions().getTimeZone()) + "\n\n" + Prefix.TERMINAL_ERROR.getPrefix() + " 异常输出：\n" + output);
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    public static List getListPage(int page, int pageSize, List list) {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("分页数据不能为空!");
        }

        int totalCount = list.size();
        page = page - 1;
        int fromIndex = page * pageSize;
        //分页不能大于总数
        if (fromIndex >= totalCount) {
            throw new RuntimeException("页数或分页大小不正确!");
        }
        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }

    public static void sendByte(Socket socket, String chancel, byte[] b) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(b.length);
        out.write(chancel.getBytes().length);
        out.write(chancel.getBytes());
        out.write(b);
        out.flush();
    }

    public static String getCurrentTime(TimeZone timeZone) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTime(date);
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
    }

    public static String getCurrentTime(TimeZone timeZone, String pattern) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTime(date);
        return new SimpleDateFormat(pattern).format(date);
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress Address = InetAddress.getByName(host);
        try {
            new Socket(Address, port);  //建立一个Socket连接
            flag = true;
        } catch (IOException ignored) {
        }
        return flag;
    }
}
