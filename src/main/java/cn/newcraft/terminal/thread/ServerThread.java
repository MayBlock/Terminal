package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.event.network.ClientConnectEvent;
import cn.newcraft.terminal.event.network.ClientConnectedEvent;
import cn.newcraft.terminal.event.network.ClientReceivedEvent;
import cn.newcraft.terminal.event.server.ServerStartEvent;
import cn.newcraft.terminal.event.server.ServerStopEvent;
import cn.newcraft.terminal.thread.packet.HeartbeatPacket;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.plugin.Plugin;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread {

    private static Socket socket;
    private static boolean enable = false;
    private static HashMap<Integer, Sender> senderHashMap = new HashMap<>();
    private static HashMap<Integer, Boolean> init = new HashMap<>();

    private static ServerThread server;

    public static HashMap<Integer, Sender> getSenders() {
        return senderHashMap;
    }

    public static void removeHashInit(int id) {
        init.remove(id);
    }

    public static ServerThread getServer() {
        return server;
    }

    public static boolean isServer() {
        return enable;
    }

    public void stopServer() {
        enable = false;
        try {
            Event.callEvent(new ServerStopEvent());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        stop();
    }

    public void startServer() {
        start();
        try {
            Event.callEvent(new ServerStartEvent());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public ServerThread() {
        server = this;
        Runnable intercommonitor = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(Terminal.getPort());
                while (true) {
                    enable = true;
                    socket = serverSocket.accept();
                    threadPool.submit(runnable);
                }
            } catch (Exception e) {
                Terminal.printException(this.getClass(), e);
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
                socket.setKeepAlive(true);
                init.put(id, true);
                /* Init Connect */
                byte[] chancel;
                byte[] bytes;
                while (true) {
                    int first = inputStream.read();
                    if (first == -1) {
                        break;
                    }
                    int second = inputStream.read();
                    chancel = new byte[second];
                    inputStream.read(chancel);

                    bytes = new byte[first];
                    inputStream.read(bytes);
                    ClientConnectEvent connectEvent = new ClientConnectEvent(new String(chancel), socket);
                    Event.callEvent(connectEvent);
                    if (connectEvent.isCancelled()) {
                        socket.close();
                        break;
                    }
                    if (new String(chancel).equals("TERMINAL")) {
                        if (init.get(id)) {
                            init.put(id, false);
                            Thread heart = getHeartThread(id);
                            senderHashMap.put(id, new Sender(socket, heart, id, true));
                            heart.start();
                            Event.callEvent(new ClientConnectedEvent(senderHashMap.get(id)));
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + senderHashMap.get(id).getCanonicalName() + " 与终端连接！");
                        }
                        Sender sender = senderHashMap.get(id);
                        Set<Plugin> key = ServerReceived.getReceived().keySet();
                        Event.callEvent(new ClientReceivedEvent(sender, bytes));
                        for (Plugin plugin : key) {
                            if (ServerReceived.getReceived().get(plugin) != null) {
                                ServerReceived.getReceived().get(plugin).onMessageReceived(sender, bytes);
                                senderHashMap.get(id).setFirstConnect(false);
                            }
                        }
                        if (Terminal.isDebug()) {
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 的Socket交互信息：");
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                            Terminal.getScreen().sendMessage(new String(bytes, StandardCharsets.UTF_8));
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " -------End-------");
                        }
                    } else {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                if (e.getMessage().equalsIgnoreCase("Connection reset") || e.getMessage().equalsIgnoreCase("Socket closed") || e.getMessage().equalsIgnoreCase("Socket is closed")) {
                    return;
                }
                Terminal.printException(this.getClass(), e);
            } catch (InvocationTargetException e) {
                if (e.getCause() == null || !e.getCause().getMessage().equals("java.io.EOFException")) {
                    Terminal.printException(this.getClass(), e);
                }
            } catch (IllegalAccessException e) {
                Terminal.printException(this.getClass(), e);
            }
        }
    };

    private Thread getHeartThread(Integer id) {
        return new Thread(() -> {
            int i = id;
            Sender sender = senderHashMap.get(i);
            while (true) {
                try {
                    Thread.sleep(10 * 1000);
                    sender.sendPacket(new HeartbeatPacket());
                    if (Terminal.isDebug()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + senderHashMap.get(i).getCanonicalName() + " 发送心跳包");
                    }
                } catch (InterruptedException | IOException e) {
                    try {
                        sender.disconnect(e.toString());
                    } catch (IOException | InvocationTargetException | IllegalAccessException ex) {
                        Terminal.printException(this.getClass(), ex);
                    }
                    break;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Terminal.printException(this.getClass(), e);
                }
            }
        });
    }
}
