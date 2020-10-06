package cn.newcraft.terminal.network;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.network.packet.HeartbeatPacket;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread {

    private static Socket socket;
    private static boolean serverEnable = false;
    protected static Map<Integer, Sender> senderMap = new HashMap<>();

    public static boolean isServer() {
        return serverEnable;
    }

    public static Map<Integer, Sender> getSenderMap() {
        return senderMap;
    }

    public static void startServerThread() {
        serverEnable = true;
        new ServerThread().listenThread().start();
    }

    public static void stopServerThread() {
        serverEnable = false;
        new ServerThread().listenThread().stop();
    }

    @Override
    public void run() {
        int id = Sender.spawnNewId();
        ExecutorService threadPool = Executors.newFixedThreadPool(Terminal.getOptions().getMaxThread());
        try {
            /** Init Connect **/
            byte[] chancel;
            while (true) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                int chancelLength = ois.read();
                chancel = new byte[chancelLength];
                ois.read(chancel);

                /* ClientConnectEvent */
                if (senderMap.get(id) == null) {
                    NetworkEvent.ClientConnectEvent connectEvent = new NetworkEvent.ClientConnectEvent(new String(chancel), socket);
                    Event.callEvent(connectEvent);
                    if (connectEvent.isCancelled()) {
                        socket.close();
                        return;
                    }
                    /* ClientConnectedEvent */
                    senderMap.put(id, new Sender(socket, getHeartThread(id), id));
                    Sender sender = senderMap.get(id);
                    sender.getHeartThread().start();
                    Event.callEvent(new NetworkEvent.ClientConnectedEvent(sender));
                    Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + sender.getCanonicalName() + " 与终端连接！");
                }
                threadPool.submit(new ServerInputRunnable(senderMap.get(id), ois.readObject()));
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            if (e.getMessage().equals("Socket closed")) {
                return;
            }
            if (e.getMessage().equals("Connection reset")) {
                try {
                    senderMap.get(id).disconnect(e.toString());
                } catch (IOException | InvocationTargetException | IllegalAccessException ex) {
                    return;
                }
            }
            Terminal.printException(this.getClass(), e);
        }
    }

    private Thread listenThread() {
        return new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(Terminal.getPort());
                while (true) {
                    socket = serverSocket.accept();
                    new ServerThread().start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Thread getHeartThread(Integer id) {
        return new Thread(() -> {
            int i = id;
            Sender sender = senderMap.get(i);
            while (true) {
                try {
                    if (sender.getTimeoutCount() >= 2) {
                        sender.disconnect("Time out");
                        break;
                    }
                    Thread.sleep(5 * 1000);
                    sender.sendPacket(new HeartbeatPacket());
                    sender.setTimeoutCount(sender.getTimeoutCount() + 1);
                    if (Terminal.isDebug()) {
                        Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 发送心跳包");
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

class ServerInputRunnable implements Runnable {

    private Sender sender;
    private Object input;

    public ServerInputRunnable(Sender sender, Object input) {
        this.sender = sender;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            Event.callEvent(new NetworkEvent.ServerReceivedEvent(sender, input));
        } catch (IllegalAccessException | InvocationTargetException e) {
            Terminal.printException(this.getClass(), e);
        }
        if (Terminal.isDebug()) {
            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 与终端交互");
            if (input instanceof String || input instanceof Integer || input instanceof Long) {
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                Terminal.getScreen().sendMessage(input);
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " -------End-------");
            } else if (input instanceof byte[]) {
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                Terminal.getScreen().sendMessage(new String((byte[]) input, StandardCharsets.UTF_8));
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " -------End-------");
            }
        }
    }
}

    /*private static Socket socket;
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
            Event.callEvent(new NetworkEvent.ServerStopEvent());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        stop();
    }

    public void startServer() {
        start();
        try {
            Event.callEvent(new NetworkEvent.ServerStartEvent());
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
                int id = Sender.spawnNewId();
                socket.setKeepAlive(true);
                init.put(id, true);

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
                    NetworkEvent.ClientConnectEvent connectEvent = new NetworkEvent.ClientConnectEvent(new String(chancel), socket);
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
                            Event.callEvent(new NetworkEvent.ClientConnectedEvent(senderHashMap.get(id)));
                            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + senderHashMap.get(id).getCanonicalName() + " 与终端连接！");
                        }
                        Sender sender = senderHashMap.get(id);
                        Event.callEvent(new NetworkEvent.ServerReceivedEvent(sender, bytes));
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

     */
