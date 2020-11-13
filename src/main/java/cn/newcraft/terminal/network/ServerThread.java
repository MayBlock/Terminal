package cn.newcraft.terminal.network;

import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.network.packet.HeartbeatPacket;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Prefix;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread implements Server {

    private Socket socket;
    private ServerSocket serverSocket;
    private Map<Integer, Sender> senderMap = new HashMap<>();
    private ExecutorService threadPool = Executors.newFixedThreadPool(Terminal.getOptions().getMaxConcurrent());
    private static boolean enabled;

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
                serverSocket = new ServerSocket(Terminal.getPort());
                while (true) {
                    socket = serverSocket.accept();
                    Terminal.getServer().getThread().start();
                }
            } catch (SocketException ignored) {
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
    public boolean onServer() {
        if (Terminal.isInternetEnabled()) {
            if (!enabled) {
                this.listenThread().start();
                this.heartThread().start();
                enabled = true;
                return true;
            }
            Terminal.getScreen().sendMessage(Prefix.TERMINAL_WARN.getPrefix() + " 由于您当前的设备尚未联网，服务器监听将不会启动，直到您的设备进行联网并重启终端！");
        }
        return false;
    }

    @Override
    public boolean shutdown() {
        if (enabled) {
            enabled = false;
            Sender.disconnectAll();
            try {
                senderMap.clear();
                this.listenThread().stop();
                this.heartThread().stop();
                if (socket != null) {
                    socket.close();
                }
                serverSocket.close();
            } catch (IOException e) {
                Terminal.printException(this.getClass(), e);
            }
            return true;
        }
        return false;
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
