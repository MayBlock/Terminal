package cn.newcraft.terminal.util;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import com.sun.management.OperatingSystemMXBean;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    public static void runCmd(String command) throws IOException {
        BufferedReader br = null;
        String line;
        Process p = Runtime.getRuntime().exec(command);
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = br.readLine()) != null) {
            Terminal.getScreen().sendMessage(line);
        }
        br.close();
    }

    public static void copyFile(String oldPath, String newPath) throws IOException, InterruptedException {
        FileOutputStream writer = null;
        FileInputStream reader = null;
        BufferedInputStream bufR = null;
        BufferedOutputStream bufW = null;
        reader = new FileInputStream(oldPath);
        writer = new FileOutputStream(newPath);
        bufR = new BufferedInputStream(reader);
        bufW = new BufferedOutputStream(writer);
        int temp;
        while ((temp = bufR.read()) != -1) {
            bufW.write(temp);
        }
        Thread.sleep(100);
        bufW.close();
        bufR.close();
        writer.close();
        reader.close();
    }


    public static boolean isReallyHeadless() {
        if (GraphicsEnvironment.isHeadless()) {
            return true;
        }
        try {
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            return screenDevices == null || screenDevices.length == 0;
        } catch (HeadlessException e) {
            Terminal.printException(Method.class, e);
            return true;
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

    public static String getFileName(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        long contentLength = connection.getContentLength();
        Map<String, List<String>> headers = connection.getHeaderFields();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.contains(".")) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (suffix.length() > 4 || suffix.contains("?")) {
                fileName = headers.get("Content-Disposition").get(0);
                if (fileName == null || !fileName.contains("filename")) {
                    fileName = UUID.randomUUID().toString();
                } else {
                    fileName = fileName.substring(fileName.lastIndexOf("filename") + 9);
                }
            }
        } else {
            fileName = headers.get("Content-Disposition").get(0);
            if (fileName == null || !fileName.contains("filename")) {
                fileName = UUID.randomUUID().toString();
            } else {
                fileName = fileName.substring(fileName.lastIndexOf("filename") + 9);
            }
        }
        fileName = URLDecoder.decode(fileName, "UTF-8");
        return fileName;
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
            new Socket(Address, port);
            flag = true;
        } catch (IOException ignored) {
        }
        return flag;
    }
}
