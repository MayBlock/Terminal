package cn.newcraft.terminal.event.console;

import cn.newcraft.terminal.event.Event;

public class ConsoleUpdateEvent extends Event {

    private String newVersion;
    private String description;
    private boolean force;

    public ConsoleUpdateEvent(String newVersion, String description, boolean force) {
        this.newVersion = newVersion;
        this.description = description;
        this.force = force;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getDescription() {
        return description;
    }

    public boolean isForce() {
        return force;
    }
}
