package cn.newcraft.terminal.data;

import java.io.OutputStream;

public class AccessData {

    private String ip;
    private String mainSystem;
    private RunType type;
    private int port;
    private long lastTime;

    public AccessData(String ip, String mainSystem, RunType type, int port, long lastTime) {
        this.ip = ip;
        this.mainSystem = mainSystem;
        this.type = type;
        this.port = port;
        this.lastTime = lastTime;
    }

    public String getIp() {
        return ip;
    }

    public String getMainSystem() {
        return mainSystem;
    }

    public RunType getType() {
        return type;
    }

    public int getPort() {
        return port;
    }

    public long getLastTime() {
        return lastTime;
    }
}
