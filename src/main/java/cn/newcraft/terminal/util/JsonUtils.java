package cn.newcraft.terminal.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    /**
     * 访问网址（String）
     *
     * @return 返回的信息
     */
    public static JsonElement getJsonURL(String url, String... paths) throws IOException {
        return getJsonURL(new URL(url), StandardCharsets.UTF_8, paths);
    }

    /**
     * 访问网址（URL）
     *
     * @return 返回的信息
     */
    public static JsonElement getJsonURL(URL url, String... paths) throws IOException {
        return getJsonURL(url, StandardCharsets.UTF_8, paths);
    }

    /**
     * 访问网址带Charset（String）
     *
     * @return 返回的信息
     */
    public static JsonElement getJsonURL(String url, Charset charsetName, String... paths) throws IOException {
        return getJsonURL(new URL(url), charsetName, paths);
    }

    /**
     * 访问网址带Charset（URL）
     *
     * @return 返回的信息
     */
    public static JsonElement getJsonURL(URL url, Charset charsetName, String... paths) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);
        InputStream is = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charsetName));
        return getJson(reader, paths);
    }

    /**
     * 在Reader中解析Json
     *
     * @return 返回JsonElement对象
     */
    public static JsonElement getJson(Reader reader, String... paths) {
        return getJson(new JsonParser().parse(reader), paths);
    }

    /**
     * 在字符串中解析Json
     *
     * @return 返回JsonElement对象
     */
    public static JsonElement getJson(String json, String... paths) {
        return getJson(new JsonParser().parse(json), paths);
    }

    /**
     * 在JsonReader中解析Json
     *
     * @return 返回JsonElement对象
     */
    public static JsonElement getJson(JsonReader jsonReader, String... paths) {
        return getJson(new JsonParser().parse(jsonReader), paths);
    }

    /**
     * 在JsonElement中解析Json（原方法）
     *
     * @return 返回JsonElement对象
     */
    private static JsonElement getJson(JsonElement element, String... paths) {
        JsonElement ret = null;
        if (element.isJsonObject()) {
            for (String path : paths) {
                if (ret == null) {
                    ret = element.getAsJsonObject().get(path);
                } else {
                    ret = ret.getAsJsonObject().get(path);
                }
            }
        }
        return ret;
    }
}
