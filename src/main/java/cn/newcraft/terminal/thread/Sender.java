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
    private Thread thread;
    private Socket socket;

    public Sender(Socket socket, Thread thread, int id) {
        this.id = id;
        this.thread = thread;
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public String getCanonicalName() {
        return "[" + getHostAddress() + "/" + id + "]";
    }

    public void sendPacket(Packet packet) throws IOException {
        packet.onPacket(this);
    }

    public void disconnect(String reason) throws IOException {
        sendPacket(new DisconnectPacket(reason));
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

    public static int getNewId() {
        int id = 0;
        while (true) {
            if (ServerThread.socketList.isEmpty()) {
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
