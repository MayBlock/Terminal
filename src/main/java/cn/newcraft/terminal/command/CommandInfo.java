package cn.newcraft.terminal.command;

import javax.annotation.Nullable;

public class CommandInfo {

    private String command;
    private String desc;
    private String usage;
    private String[] aliases;

    public CommandInfo(String command, String desc, String usage) {
        this.command = command;
        this.desc = desc;
        this.usage = usage;
    }

    public CommandInfo(String command, String desc, String usage, String... aliases) {
        this.command = command;
        this.desc = desc;
        this.usage = usage;
        this.aliases = aliases;
    }

    public String getCommand() {
        return command;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }
}
