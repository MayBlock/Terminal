package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.thread.packet.HeartbeatPacket;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread {

    private static Socket socket;
    private static boolean enable = false;
    public static HashMap<Integer, Sender> integerSocketHashMap = new HashMap<>();

    private static ServerThread server;

    public static ServerThread getServer() {
        return server;
    }

    public static boolean isServer() {
        return enable;
    }

    public void stopServer() {
        enable = false;
        stop();
    }

    public void startServer() {
        start();
    }


    public ServerThread(int port) {
        server = this;
        Runnable intercommonitor = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    enable = true;
                    socket = serverSocket.accept();
                    threadPool.submit(runnable);
                }
            } catch (Exception e) {
                Method.printException(this.getClass(), e);
            }
        };
        new Thread(intercommonitor).start();
    }

    private ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                /* Init Connect */
                int id = Sender.spawnNewId();
                //integerSocketHashMap.put(id, new Sender(socket, heart, id, true));
                socket.setKeepAlive(true);
                /* Init Connect */
                byte[] bytes;
                while (true) {
                    int first = inputStream.read();
                    if (first == -1) {
                        break;
                    }
                    bytes = new byte[first];
                    inputStream.read(bytes);

                    /*
                    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
                    int s = Integer.parseInt(in.readUTF().split("/")[1].split(":")[1].replace("]", ""));
                    System.out.println("protocol: " + s);
                     */

                    if (!new String(bytes).contains("[TERMINAL/protocol:1]")) {
                        try {
                            if (Terminal.getInstance().isDebug()) {
                                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD_WARN.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " 拒绝IP " + socket.getInetAddress().getHostAddress() + " 的连接，原因：协议不正确");
                            }
                            socket.close();
                            break;
                        } catch (IOException e) {
                            Method.printException(this.getClass(), e);
                        }
                    } else {
                        Thread heart = getHeartThread(id);
                        integerSocketHashMap.put(id, new Sender(socket, heart, id, true));
                        Sender sender = integerSocketHashMap.get(id);
                        for (Plugin plugin : ServerReceived.getReceivedLists()) {
                            if (ServerReceived.getReceived().get(plugin) != null) {
                                ServerReceived.getReceived().get(plugin).onMessageReceived(sender, bytes);
                                integerSocketHashMap.get(id).setFirstConnect(false);
                            }
                        }
                        if (Terminal.getInstance().isDebug()) {
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 的Socket交互信息：");
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                            Terminal.getScreen().sendMessage(new String(bytes, StandardCharsets.UTF_8));
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " -------End-------");
                        }
                    }
                }
            } catch (IOException e) {
                if (e.getMessage().equalsIgnoreCase("Connection reset") || e.getMessage().equalsIgnoreCase("Socket closed")) {
                    return;
                }
                Method.printException(this.getClass(), e);
            }
        }
    };

    private Thread getHeartThread(Integer id) {
        return new Thread(() -> {
            int i = id;
            Sender sender = integerSocketHashMap.get(i);
            while (true) {
                try {
                    Thread.sleep(10 * 1000);
                    sender.sendPacket(new HeartbeatPacket());
                    if (Terminal.getInstance().isDebug()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + integerSocketHashMap.get(i).getCanonicalName() + " 发送心跳包");
                    }
                } catch (InterruptedException | IOException e) {
                    try {
                        sender.disconnect(e.toString());
                    } catch (IOException ignored) {
                    }
                    break;
                }
            }
        });
    }
}
