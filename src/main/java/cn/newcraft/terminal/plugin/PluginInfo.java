package cn.newcraft.terminal.plugin;

import java.net.URLClassLoader;

public class PluginInfo {

    private String name;
    private String main;
    private String version;
    private String author;
    private String prefix;
    private String path;
    private String absolutePath;
    private URLClassLoader urlClassLoader;

    public PluginInfo(String name, String main, String version, String author, String prefix, String path, String absolutePath, URLClassLoader urlClassLoader) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.author = author;
        this.prefix = prefix;
        this.path = path;
        this.absolutePath = absolutePath;
        this.urlClassLoader = urlClassLoader;
    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPath() {
        return path;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }
}
