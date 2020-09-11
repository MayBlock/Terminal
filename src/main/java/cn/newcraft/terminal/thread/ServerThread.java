package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.thread.packet.HeartbeatPacket;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

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
    public static List<Socket> socketList = Lists.newArrayList();

    private static ServerThread server;

    public static int getConnects() {
        return integerSocketHashMap.size();
    }

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
                byte[] bytes;
                int id = Sender.getNewId();
                socketList.add(socket);
                /* send HeartPacket */
                Thread heart = new Thread(() -> {
                    Integer i = id;
                    while (true) {
                        try {
                            Thread.sleep(30 * 1000);
                            integerSocketHashMap.get(i).sendPacket(new HeartbeatPacket());
                            if (Terminal.getInstance().isDebug()) {
                                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + integerSocketHashMap.get(i).getCanonicalName() + " 发送心跳包");
                            }
                        } catch (InterruptedException | IOException e) {
                            try {
                                integerSocketHashMap.get(i).disconnect(e.toString());
                            } catch (IOException ignored) {
                            }
                            break;
                        }
                    }
                });
                /* send HeartPacket */
                integerSocketHashMap.put(id, new Sender(socket, heart, id));
                Sender sender = integerSocketHashMap.get(id);
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + sender.getCanonicalName() + " 与终端连接！");
                socket.setKeepAlive(true);
                heart.start();
                while (true) {
                    int first = inputStream.read();
                    if (first == -1) {
                        break;
                    }
                    bytes = new byte[first];
                    inputStream.read(bytes);
                    for (Plugin plugin : ServerReceived.getReceivedLists()) {
                        if (ServerReceived.getReceived().get(plugin) != null) {
                            ServerReceived.getReceived().get(plugin).onMessageReceived(integerSocketHashMap.get(id), bytes);
                        }
                    }
                    if (Terminal.getInstance().isDebug()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 的Socket交互信息：");
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                        Terminal.getScreen().sendMessage(new String(bytes, StandardCharsets.UTF_8));
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " -------End-------");
                    }
                }
            } catch (IOException e) {
                if (e.getMessage().equals("Connection reset") || e.getMessage().equals("Socket closed")) {
                    //closeSocket(integerSocketHashMap.get(socket));
                    return;
                }
                Method.printException(this.getClass(), e);
            }


            /* Old Code
            InputStream is = null;
            OutputStream os = null;
            try {
                is = socket.getInputStream();
                byte[] bytes = new byte[256];
                int i;
                while ((i = is.read(bytes)) != -1) {
                    socket.shutdownInput();
                    is.read(bytes, 0, i);
                    os = socket.getOutputStream();
                    for (Plugin plugin : ServerReceived.getReceivedLists()) {
                        if (ServerReceived.getReceived().get(plugin) != null) {
                            ServerReceived.getReceived().get(plugin).onMessageReceived(new Sender(socket, socket.getInetAddress().getHostAddress(), socket.getPort()), bytes);
                        }
                    }
                    if (Terminal.getInstance().isDebug()){
                        Terminal.getScreen().sendMessage(Prefix.DEBUG.getPrefix() + " 来自IP：" + socket.getInetAddress().getCanonicalHostName() + " 的Socket交互信息：");
                        Terminal.getScreen().sendMessage(Prefix.DEBUG.getPrefix() + " ------Info------");
                        Terminal.getScreen().sendMessage(new String(bytes));
                        Terminal.getScreen().sendMessage(Prefix.DEBUG.getPrefix() + " -------End-------");
                    }
                }
            } catch (Exception e) {
                if (e.getMessage().equals("Connection reset")){
                    return;
                }
                Method.printException(this.getClass(), e);
            } finally {//最终将要执行的一些代码
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    Method.printException(this.getClass(), e);
                }
            }
             */
        }
    };
}
