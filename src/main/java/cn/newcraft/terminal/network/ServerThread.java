package cn.newcraft.terminal.network;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.exception.UnknownException;
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

public class ServerThread extends Thread implements Server {

    private Socket socket;
    private boolean enabled;
    private Map<Integer, Sender> senderMap = new HashMap<>();
    private ExecutorService threadPool = Executors.newFixedThreadPool(Terminal.getOptions().getMaxConcurrent());

    @Override
    public void run() {
        int id = Sender.spawnNewId();
        System.out.println("ID: " + id + " connected to terminal!");
        /** Init Connect **/
        byte[] channel;
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            Terminal.printException(this.getClass(), e);
        }
        while (true) {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(new BufferedInputStream(inputStream));
                int chancelLength = ois.read();
                channel = new byte[chancelLength];
                ois.read(channel);
            } catch (Exception e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Terminal.printException(this.getClass(), ex);
                }
                break;
            }
            try {
                /* ClientConnectEvent */
                if (senderMap.get(id) == null) {
                    NetworkEvent.ClientConnectEvent connectEvent = new NetworkEvent.ClientConnectEvent(new String(channel), socket);
                    Event.callEvent(connectEvent);
                    if (connectEvent.isCancelled()) {
                        socket.close();
                        return;
                    }
                    /* ClientConnectedEvent */
                    senderMap.put(id, new Sender(socket, id));
                    Sender sender = senderMap.get(id);
                    Event.callEvent(new NetworkEvent.ClientConnectedEvent(sender));
                    Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + sender.getCanonicalName() + " 与终端连接！");
                }
                threadPool.submit(new ServerInputRunnable(senderMap.get(id), ois.readObject()));
            } catch (IOException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                if (e.getMessage().equalsIgnoreCase("Socket closed")) {
                    return;
                }
                if (e.getMessage().equalsIgnoreCase("Stream closed.")) {
                    return;
                }
                if (e.getMessage().equalsIgnoreCase("Connection reset")) {
                    try {
                        senderMap.get(id).disconnect(e.toString());
                    } catch (IOException | InvocationTargetException | IllegalAccessException ex) {
                        return;
                    }
                }
                Terminal.printException(this.getClass(), e);
            }
        }
    }

    private Thread listenThread() {
        return new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(Terminal.getPort());
                while (true) {
                    socket = serverSocket.accept();
                    Terminal.getServer().getThread().start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Thread heartThread() {
        return new Thread(() -> {
            while (true) {
                if (!senderMap.isEmpty()) {
                    for (Sender sender : senderMap.values()) {
                        try {
                            if (sender.getTimeoutCount() >= 5) {
                                sender.disconnect("Time out");
                                continue;
                            }
                            sender.sendPacket(new HeartbeatPacket());
                            sender.setTimeoutCount(sender.getTimeoutCount() + 1);
                            if (Terminal.isDebug()) {
                                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " " + sender.getCanonicalName() + " 发送心跳包");
                            }
                        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                            try {
                                sender.disconnect(e.getMessage());
                            } catch (IOException | InvocationTargetException | IllegalAccessException ignored) {
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(ServerConfig.cfg.getYml().getInt("server.heart_packet_delay"));
                } catch (InterruptedException e) {
                    Terminal.printException(this.getClass(), e);
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<Integer, Sender> getSenderMap() {
        return senderMap;
    }

    @Override
    public void onServer() {
        System.out.println("start server thread!");
        if (!enabled) {
            this.listenThread().start();
            this.heartThread().start();
            enabled = true;
        }
    }

    @Override
    public void shutdown() {
        if (enabled) {
            this.listenThread().stop();
            this.heartThread().stop();
            enabled = false;
        }
    }

    @Override
    public Thread getThread() {
        return this;
    }

    @Override
    public int getPort() {
        return this.getPort();
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
        if (input == null) {
            return;
        }
        try {
            Event.callEvent(new NetworkEvent.ServerReceivedEvent(sender, input));
        } catch (InvocationTargetException | IllegalAccessException e) {
            if (e.getCause() != null && e.getCause().toString().contains("java.io.EOFException")) {
                return;
            }
            e.printStackTrace();
        }
        sender.setTimeoutCount(0);
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
            } else {
                Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + Prefix.DEBUG.getPrefix() + " ------Info------");
                Terminal.getScreen().sendMessage(input.getClass().getName());
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
