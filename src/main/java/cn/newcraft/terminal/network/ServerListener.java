package cn.newcraft.terminal.network;

import cn.newcraft.terminal.Prefix;
import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.event.Listener;
import cn.newcraft.terminal.event.SubscribeEvent;
import cn.newcraft.terminal.event.network.NetworkEvent;
import cn.newcraft.terminal.util.Method;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerListener implements Listener {

    private List<Socket> sockets = new ArrayList<>();

    @SubscribeEvent
    public void onConnect(NetworkEvent.ClientConnectEvent e) {
        if (e.getChannel().equals("TERMINAL")) {
            sockets.add(e.getSocket());
        }
        if (e.getChannel().equals("PING")) {
            try {
                NetworkEvent.PingServerEvent pingEvent = new NetworkEvent.PingServerEvent(e.getId(), e.getSocket());
                Event.callEvent(pingEvent);
                if (pingEvent.isCancelled()) {
                    e.setCancelled(true);
                }
            } catch (InvocationTargetException | IllegalAccessException ex) {
                Terminal.printException(this.getClass(), ex);
            }
        }
    }

    @SubscribeEvent
    public void onTimeoutCount(NetworkEvent.ServerReceivedEvent e) {
        if (e.getInput() instanceof byte[]) {
            ByteArrayDataInput in = ByteStreams.newDataInput((byte[]) e.getInput());
            String channel = in.readUTF();
            if (channel.equals("REGULAR")) {
                e.getSender().setTimeout(0);
            }
        }
    }

    @SubscribeEvent
    public void onPing(NetworkEvent.PingServerEvent e) {
        System.out.println("connect!");
        try {
            e.getSocket().shutdownInput();
            Method.sendStream(e.getSocket(), "PASS".getBytes(StandardCharsets.UTF_8));
            e.getSocket().getOutputStream().write(100);
            Terminal.getScreen().sendMessage(Prefix.SERVER_THREAD.getPrefix() + " " + e.getSocket().getInetAddress() + " - 初始处理程序已连接！");
            e.setCancelled(true);
        } catch (SocketException ex) {
            if (ex.getMessage().equalsIgnoreCase("Socket input is already shutdown")) {
                return;
            }
            Terminal.printException(this.getClass(), ex);
        } catch (IOException ex) {
            Terminal.printException(this.getClass(), ex);
        }
    }

    @SubscribeEvent
    public void onReceived(NetworkEvent.ServerReceivedEvent e) {
        if (sockets.contains(e.getSender().getSocket())) {
            Sender sender = e.getSender();
            if (e.getInput() instanceof byte[]) {
                try {
                    OutputStream output = e.getSender().getSocket().getOutputStream();
                    ByteArrayDataInput in = ByteStreams.newDataInput((byte[]) e.getInput());
                    String chancel = in.readUTF();
                    if (chancel.equals("GET")) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        String subChancel = in.readUTF();
                        switch (subChancel) {
                            case "ID":
                                out.write(sender.getId());
                                sender.sendByte(out.toByteArray(), output);
                                break;
                            case "NAME":
                                out.write(Terminal.getName().getBytes());
                                sender.sendByte(out.toByteArray(), output);
                                break;
                            case "VERSION":
                                out.write(Terminal.getOptions().getVersion().getBytes());
                                sender.sendByte(out.toByteArray(), output);
                                break;
                            case "TIMEZONE":
                                out.write(Terminal.getOptions().getTimeZone().getID().getBytes());
                                sender.sendByte(out.toByteArray(), output);
                                break;
                            case "PLUGIN_MANAGER":
                                out.writeBoolean(ServerConfig.cfg.getYml().getBoolean("server.enable_plugin"));
                                sender.sendByte(out.toByteArray(), output);
                                break;
                            case "INFO":
                                out.writeUTF("RETURN");
                                out.writeUTF("Terminal Name: " + Terminal.getName());
                                out.writeUTF("Terminal Version: " + Terminal.getOptions().getVersion());
                                out.writeUTF("Terminal TimeZone: " + Terminal.getOptions().getTimeZone().getID());
                                out.writeUTF("PluginManager: " + ServerConfig.cfg.getYml().getBoolean("server.enable_plugin"));
                                out.writeUTF("Your Connect ID: " + sender.getId());
                                out.writeUTF("Your Address: " + sender.getHostAddress() + "/" + sender.getPort());
                                out.writeUTF("FINISH");
                                sender.sendByte(out.toByteArray(), output);
                        }
                    }
                    if (chancel.equals("DISCONNECT")) {
                        sender.disconnect(in.readUTF());
                    }
                } catch (InvocationTargetException | IllegalAccessException | IOException ex) {
                    Terminal.printException(this.getClass(), ex);
                }
            }
        }
    }
}

