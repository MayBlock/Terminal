package cn.newcraft.terminal.update;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public class Download {

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
    //private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private String cookie = null;
    private String url;
    // 文件总长度
    private long contentLength;
    // 当前下载长度
    private long currentLength;
    private long preLength;
    private Map<String, List<String>> headers;
    private String localPath = "./update";

    public Download(String url) {
        this.url = url;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getPreLength() {
        return preLength;
    }

    public void setPreLength(long preLength) {
        this.preLength = preLength;
    }

    public void download(Thread count) {
        try {
            if (new File(url).exists()) {
                setCurrentLength(100);
                return;
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            if (cookie != null) connection.setRequestProperty("Cookie", cookie);
            if (connection.getResponseCode() == 302) {
                url = connection.getHeaderField("Location");
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", userAgent);
                if (cookie != null) connection.setRequestProperty("Cookie", cookie);
            }
            setContentLength(connection.getContentLength());
            headers = connection.getHeaderFields();
            // 创建本地文件
            File file = new File(localPath);
            if (!file.exists()) file.mkdirs();
            file = new File(localPath + File.separator + getFileName(url));
            FileOutputStream fos = new FileOutputStream(file);
            // 在写文件之前调用统计方法
            count.start();
            // 拿到文件流
            InputStream is = connection.getInputStream();
            int len;
            byte[] b = new byte[1024];
            while ((len = is.read(b)) != -1) {
                setCurrentLength(getCurrentLength() + len);
                fos.write(b, 0, len);
                fos.flush();
            }
            is.close();
            fos.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatLength(long length) {
        if (length < 1024) {
            return length + "b";
        } else if (length > 1024 && length < 1024 * 1024) {
            BigDecimal bigDecimal = new BigDecimal((double) length / 1024);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.floatValue() + "kb";
        } else if (length > 1024 * 1024 && length < 1024 * 1024 * 1024) {
            BigDecimal bigDecimal = new BigDecimal((double) length / 1024 / 1024);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.floatValue() + "mb";
        } else {
            BigDecimal bigDecimal = new BigDecimal((double) length / 1024 / 1024 / 1024);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.floatValue() + "gb";
        }
    }

    public String getFileName(String url) throws UnsupportedEncodingException {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.contains(".")) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (suffix.length() > 4 || suffix.contains("?")) {
                fileName = headers.get("Content-Disposition").get(0);
                if (fileName == null || !fileName.contains("filename")) {
                    fileName = fileName.substring(fileName.lastIndexOf("filename") + 9);
                }
            }
        }
        fileName = URLDecoder.decode(fileName, "UTF-8");
        return fileName;
    }
}
