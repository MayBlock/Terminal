package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.thread.packet.DisconnectPacket;
import cn.newcraft.terminal.thread.packet.Packet;

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

    @Deprecated
    public void disconnect() throws IOException {
        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + getCanonicalName() + " 断开连接！ ( disconnect )");
        sendPacket(new DisconnectPacket());
    }

    public void disconnect(String reason) throws IOException {
        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + getCanonicalName() + " 断开连接！ ( " + reason + " )");
        sendPacket(new DisconnectPacket(reason));
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
            Terminal.printException(this.getClass(), e);
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
            if (ServerThread.getSenders().isEmpty()) {
                return id;
            }
            if (ServerThread.getSenders().get(id) != null) {
                id++;
            } else {
                return id;
            }
        }
    }
}
