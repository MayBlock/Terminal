package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.thread.packet.DisconnectPacket;
import cn.newcraft.terminal.thread.packet.HeartbeatPacket;
import cn.newcraft.terminal.thread.packet.Packet;
import cn.newcraft.terminal.util.Method;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender {

    private int id;
    private Thread heartThread;
    private Socket socket;
    private boolean firstConnect;

    public Sender(Socket socket, Thread heartThread, int id, boolean firstConnect) {
        this.id = id;
        this.heartThread = heartThread;
        this.socket = socket;
        this.firstConnect = firstConnect;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getHeartThread() {
        return heartThread;
    }

    public String getCanonicalName() {
        return "[" + getHostAddress() + "/" + id + "]";
    }

    public void sendPacket(Packet packet) throws IOException {
        packet.onPacket(this);
    }

    public void disconnect(String reason) throws IOException {
        sendPacket(new DisconnectPacket(reason));
        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + getCanonicalName() + " 断开连接！ ( " + reason + " )");
    }

    public boolean isFirstConnect() {
        return firstConnect;
    }

    protected void setFirstConnect(boolean b) {
        firstConnect = b;
    }

    public void sendMessage(String str) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.write(str);
            pw.flush();
        } catch (IOException e) {
            Method.printException(this.getClass(), e);
        }
    }

    public void sendByte(byte[] bytes, boolean length) throws IOException {
        OutputStream out = socket.getOutputStream();
        if (length) out.write(bytes.length);
        out.write(bytes);
        out.flush();
    }

    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public static int spawnNewId() {
        int id = 0;
        while (true) {
            if (ServerThread.integerSocketHashMap.isEmpty()) {
                return id;
            }
            if (ServerThread.integerSocketHashMap.get(id) != null) {
                id++;
            } else {
                return id;
            }
        }
    }
}
