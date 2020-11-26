package cn.newcraft.terminal.network;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.Prefix;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.event.network.SocketEvent;
import cn.newcraft.terminal.network.packet.DisconnectPacket;
import cn.newcraft.terminal.network.packet.Packet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Map;

public class Sender {

    private int id;
    private int timeout = 0;
    private Socket socket;

    public Sender(Socket socket, int id) {
        this.id = id;
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getCanonicalName() {
        return "[" + this.getHostAddress() + "/" + id + "]";
    }

    public void sendPacket(Packet packet) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketEvent.SendPacketToClientEvent event = new SocketEvent.SendPacketToClientEvent(this, packet);
        Event.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        packet.onPacket(this);
    }

    @Deprecated
    public void disconnect() throws IOException, InvocationTargetException, IllegalAccessException {
        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + getCanonicalName() + " 断开连接！ ( disconnect )");
        sendPacket(new DisconnectPacket());
    }

    public void disconnect(String reason) throws IOException, InvocationTargetException, IllegalAccessException {
        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + getCanonicalName() + " 断开连接！ ( " + reason + " )");
        sendPacket(new DisconnectPacket(reason));
    }

    public void sendMessage(String str) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
            pw.write(str);
            pw.flush();
        } catch (IOException e) {
            Terminal.printException(this.getClass(), e);
        }
    }

    public void sendByte(byte[] bytes, OutputStream stream) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketEvent.SendByteToClientEvent event = new SocketEvent.SendByteToClientEvent(this, bytes);
        Event.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        stream.write(bytes);
        stream.flush();
    }

    public void setTimeout(int second) {
        this.timeout = second;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public static Sender getSender(int id) {
        return Terminal.getServer().getSenderMap().get(id);
    }

    public static int spawnNewId() {
        int id = 0;
        Map<Integer, Sender> senderMap = Terminal.getServer().getSenderMap();
        while (true) {
            if (senderMap.isEmpty()) {
                return id;
            }
            if (senderMap.get(id) != null) {
                id++;
            } else {
                return id;
            }
        }
    }

    public static void disconnectAll() {
        disconnectAll("Server Closed");
    }

    public static void disconnectAll(String reason) {
        Map<Integer, Sender> senderMap = Terminal.getServer().getSenderMap();
        if (senderMap.isEmpty()) {
            return;
        }
        for (int i = 0; i < senderMap.size(); i++) {
            try {
                senderMap.get(i).disconnect(reason);
            } catch (IOException ignored) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                Terminal.printException(Terminal.class, e);
            }
        }
    }
}
