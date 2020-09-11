package cn.newcraft.terminal.config;

import cn.newcraft.terminal.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * class author: Hello_Han
 */

public class ConfigManager {

    private YamlConfiguration yml;
    private File config;
    private String name;

    public ConfigManager(String name, String folder) throws Exception {
        this.config = new File(folder + "/" + name + ".yml");
        config.getParentFile().mkdirs();
        config.createNewFile();
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        this.name = name;
    }

    public void reload() {
        this.yml = YamlConfiguration.loadConfiguration(this.config);
    }

    public void set(String string, Object object) {
        this.yml.set(string, object);
        this.save();
    }

    public void save() {
        try {
            this.yml.save(this.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml() {
        return this.yml;
    }

    public String getName() {
        return this.name;
    }

    public boolean getBoolean(String b) {
        return this.yml.getBoolean(b);
    }

    public int getInt(String i) {
        return this.yml.getInt(i);
    }
}
