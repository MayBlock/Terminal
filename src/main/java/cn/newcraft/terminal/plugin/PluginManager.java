package cn.newcraft.terminal.plugin;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.command.CommandManager;
import cn.newcraft.terminal.configuration.file.YamlConfiguration;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.screen.TextColor;
import com.google.common.collect.Lists;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class PluginManager {

    private static File file;
    private static HashMap<String, PluginInfo> plugins = new HashMap<>();
    private Class<?> LoadMain;
    private URLClassLoader urlClassLoader;
    private Screen screen = Terminal.getScreen();

    public static HashMap<String, PluginInfo> getPlugins() {
        return plugins;
    }

    private static int loadFailed = 0;

    public enum Status {
        LOAD, ENABLE, DISABLE
    }

    protected PluginManager() {
    }

    public PluginManager(Status status) {
        switch (status) {
            case LOAD:
                File[] array = file.listFiles();
                List<File> loadList = Lists.newArrayList();
                for (File value : array) {
                    if (value.isFile() && value.getName().endsWith(".jar")) {
                        loadList.add(value);
                    }
                }
                if (loadList.size() <= 0) {
                    screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER.getPrefix() + " 终端当前无安装任何插件，跳过插件加载过程");
                    return;
                }
                screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER.getPrefix() + " 当前终端已安装了 " + loadList.size() + " 个插件，初始化中...");
                for (File value : loadList) {
                    this.loadPlugin(value);
                }
                break;
            case ENABLE:
                for (String plugin : plugins.keySet()) {
                    this.enablePlugin(new Plugin(plugin));
                }
                screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER.getPrefix() + " 所有插件已加载且启用完毕，" + plugins.size() + " 个加载完毕，" + loadFailed + " 个加载失败");
                break;
            case DISABLE:
                while (!plugins.isEmpty()) {
                    this.disablePlugin(new Plugin((String) plugins.keySet().toArray()[0]));
                }
        }
    }

    protected void loadPlugin(File file) {
        try {
            String name = getPluginYmlString(file, "name");
            String main = getPluginYmlString(file, "main");
            String version = getPluginYmlString(file, "version");
            String author = getPluginYmlString(file, "author") == null ? "匿名作者" : getPluginYmlString(file, "author");
            String prefix = getPluginYmlString(file, "prefix") == null ? file.getName().replace(".jar", "") : getPluginYmlString(file, "prefix");
            String apiVersion = getPluginYmlString(file, "api-version");
            screen.sendMessage("[" + name + "] Loading...");
            if (plugins.get(name) != null) {
                screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 加载失败，插件 " + name + " 名字与其他插件相同！");
                loadFailed++;
                return;
            }
            if (apiVersion == null) {
                screen.sendMessage(Prefix.PLUGIN_MANAGER_WARN.getPrefix() + " 插件 " + name + " 在plugin.yml中未定义API版本！");
            } else try {
                String[] api = apiVersion.split("-");
                boolean b = false;
                if (api.length == 1) {
                    if (Integer.parseInt(api[0]) == Terminal.getOptions().getApiVersion()) {
                        b = true;
                    }
                } else for (int i = Integer.parseInt(api[0]); i < Integer.parseInt(api[1]); i++) {
                    if (Terminal.getOptions().getApiVersion() == i) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    screen.sendMessage(Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 插件 " + name + " 的API版本已不受支持，请联系开发者更新！");
                    loadFailed++;
                    return;
                }
            } catch (NumberFormatException e) {
                screen.sendMessage(Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 插件 " + name + " 的API版本号不符合规范！");
                loadFailed++;
                return;
            }
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + file.getAbsolutePath())});
            LoadMain = urlClassLoader.loadClass(main);
            Object instance = LoadMain.newInstance();
            Method m1 = LoadMain.getDeclaredMethod("onLoad");
            m1.invoke(instance);
            plugins.put(name, new PluginInfo(name, main, version, author, prefix, file.getPath(), file.getAbsolutePath(), urlClassLoader));
            if (Terminal.isDebug()) {
                screen.sendMessage(Prefix.DEBUG.getPrefix() + " 插件 " + name + " Version: " + version + " MainClass: " + main + " 已加载至终端！");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException | InstantiationException e) {
            loadFailed++;
            screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 插件 " + file.getName().replace(".jar", "") + " 加载失败，该报错非为Terminal问题，请联系该插件开发者");
            screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 请检查插件内是否包含 plugin.yml 文件且保证语法正确");

            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↓=↓=↓=↓=↓== 该错误并非为Terminal造成，请不要报告该错误 ==↓=↓=↓=↓=↓===");
            Terminal.printException(this.getClass(), e);
            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↑=↑=↑=↑=↑== 该错误并非为Terminal造成，请不要报告该错误 ==↑=↑=↑=↑=↑===");
        }
    }

    protected void enablePlugin(Plugin plugin) {
        String name = plugin.getPluginName();
        try {
            String main = plugins.get(name).getMain();
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + plugins.get(name).getAbsolutePath())});
            LoadMain = urlClassLoader.loadClass(main);
            Object instance = LoadMain.newInstance();
            Method m1 = LoadMain.getDeclaredMethod("onEnable");
            m1.invoke(instance);
            if (Terminal.isDebug()) {
                screen.sendMessage(
                        Prefix.DEBUG.getPrefix() + " 插件 " + name +
                                " Version: " + plugins.get(name).getVersion() + " MainClass: " + main + " 已启用！");
            }
            screen.sendMessage("[" + name + "] Loaded!");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException | ClassNotFoundException | InstantiationException e) {
            loadFailed++;
            screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 插件 " + name + " 启用失败，该报错非为Terminal问题，请联系该插件开发者");
            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↓=↓=↓=↓=↓== 该错误并非为Terminal造成，请不要报告该错误 ==↓=↓=↓=↓=↓===");
            Terminal.printException(this.getClass(), e);
            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↑=↑=↑=↑=↑== 该错误并非为Terminal造成，请不要报告该错误 ==↑=↑=↑=↑=↑===");
            new PluginManager(Status.DISABLE);
        }
    }

    protected void disablePlugin(Plugin plugin) {
        String name = plugin.getPluginName();
        try {
            String main = plugins.get(name).getMain();
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + plugins.get(name).getAbsolutePath())});
            LoadMain = urlClassLoader.loadClass(main);
            Object instance = LoadMain.newInstance();
            Method m1 = LoadMain.getDeclaredMethod("onDisable");
            m1.invoke(instance);
            if (Terminal.isDebug()) {
                screen.sendMessage(
                        Prefix.DEBUG.getPrefix() + " 插件 " + name +
                                " Version: " + plugins.get(name).getVersion() + " MainClass: " + main + " 已从终端卸载！");
            }
            screen.sendMessage("[" + name + "] Disabled!");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException | InstantiationException | ClassNotFoundException e) {
            screen.sendMessage("\n" + Prefix.PLUGIN_MANAGER_ERROR.getPrefix() + " 插件 " + name + " 卸载失败，该报错非为Terminal问题，请联系该插件开发者");
            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↓=↓=↓=↓=↓== 该错误并非为Terminal造成，请不要报告该错误 ==↓=↓=↓=↓=↓===");
            Terminal.printException(this.getClass(), e);
            screen.sendMessage(TextColor.ORANGE + TextColor.BOLD + "\n===↑=↑=↑=↑=↑== 该错误并非为Terminal造成，请不要报告该错误 ==↑=↑=↑=↑=↑===");
        }
        CommandManager.getCommandsInfo().remove(name);
        if (Event.getListeners().get(plugin) != null) {
            Event.getListeners().remove(plugin);
        }
        plugins.remove(name);
    }

    public static Plugin getPlugin(String name) {
        return new Plugin(name);
    }

    public static Plugin getPlugin(MainPlugin mainPlugin) {
        return new Plugin(mainPlugin.getPluginName());
    }

    private static String getPluginYmlString(File file, String path) throws IOException {
        YamlConfiguration yml = null;
        ZipFile zip = new ZipFile(file.getPath());
        InputStream in = new BufferedInputStream(new FileInputStream(file.getPath()));
        ZipInputStream zipInputStream = new ZipInputStream(in, StandardCharsets.UTF_8);
        ZipEntry ze;
        while ((ze = zipInputStream.getNextEntry()) != null) {
            if (ze.toString().equals("plugin.yml")) {
                yml = YamlConfiguration.loadConfiguration(new InputStreamReader(zip.getInputStream(ze)));
            }
        }
        zipInputStream.closeEntry();
        return yml.getString(path);
    }

    public static void spawnFile() {
        file = new File("./plugins");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
