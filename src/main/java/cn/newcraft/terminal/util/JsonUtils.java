package cn.newcraft.terminal.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    public static String getStringJson(String URL, String path_1, String path_2, boolean error) {
        String str;
        try {
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String response = reader.readLine();
            JsonElement jsonElement = new JsonParser().parse(response);
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            if (path_2 != null) {
                str = jsonElement.getAsJsonObject().get(path_1).getAsJsonObject().get(path_2).getAsString();
            } else {
                str = jsonElement.getAsJsonObject().get(path_1).getAsString();
            }
            reader.close();

        } catch (IOException e) {
            if (error) {
                e.printStackTrace();
            }
            return null;
        }
        return str;
    }

    public static boolean getBooleanJson(String URL, String path_1, String path_2, boolean error) {
        boolean bl;
        try {
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String response = reader.readLine();
            JsonElement jsonElement = new JsonParser().parse(response);
            if (!jsonElement.isJsonObject()) {
                return false;
            }
            if (path_2 != null) {
                bl = jsonElement.getAsJsonObject().get(path_1).getAsJsonObject().get(path_2).getAsBoolean();
            } else {
                bl = jsonElement.getAsJsonObject().get(path_1).getAsBoolean();
            }
            reader.close();

        } catch (IOException e) {
            if (error) {
                e.printStackTrace();
            }
            return false;
        }
        return bl;
    }

    public static int getIntJson(String URL, String path_1, String path_2, boolean error) {
        int it;
        try {
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String response = reader.readLine();
            JsonElement jsonElement = new JsonParser().parse(response);
            if (!jsonElement.isJsonObject()) {
                return 0;
            }
            if (path_2 != null) {
                it = jsonElement.getAsJsonObject().get(path_1).getAsJsonObject().get(path_2).getAsInt();
            } else {
                it = jsonElement.getAsJsonObject().get(path_1).getAsInt();
            }
            reader.close();

        } catch (IOException e) {
            if (error) {
                e.printStackTrace();
            }
            return 0;
        }
        return it;
    }
}
