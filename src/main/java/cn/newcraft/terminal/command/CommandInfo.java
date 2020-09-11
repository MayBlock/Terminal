package cn.newcraft.terminal.command;

public class CommandInfo {

    private String command;
    private String desc;
    private String usage;

    public CommandInfo(String command, String desc, String usage) {
        this.command = command;
        this.desc = desc;
        this.usage = usage;
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
}
