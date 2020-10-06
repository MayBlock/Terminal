package cn.newcraft.terminal.network;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import cn.newcraft.terminal.event.Listener;
import cn.newcraft.terminal.event.SubscribeEvent;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListener implements Listener {

    private List<Socket> sockets = new ArrayList<>();

    @SubscribeEvent
    public void onConnect(NetworkEvent.ClientConnectEvent e) {
        if (e.getChancel().equals("TERMINAL")) {
            sockets.add(e.getSocket());
        }
    }

    @SubscribeEvent
    public void onTimeoutCount(NetworkEvent.ServerReceivedEvent e) {
        if (e.getInput() instanceof byte[]) {
            ByteArrayDataInput in = ByteStreams.newDataInput((byte[]) e.getInput());
            if (in.readUTF().equals("REGULAR")) {
                e.getSender().setTimeoutCount(0);
            }
        }
    }

    @SubscribeEvent
    public void onReceived(NetworkEvent.ServerReceivedEvent e) {
        if (sockets.contains(e.getSender().getSocket())) {
            Sender sender = e.getSender();
            if (e.getInput() instanceof byte[]) {
                ByteArrayDataInput in = ByteStreams.newDataInput((byte[]) e.getInput());
                String chancel = in.readUTF();
                if (chancel.equals("GET")) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    try {
                        String subChancel = in.readUTF();
                        switch (subChancel) {
                            case "ID":
                                out.write(sender.getId());
                                sender.sendByte(out.toByteArray(), false);
                                break;
                            case "NAME":
                                out.write(Terminal.getName().getBytes());
                                sender.sendByte(out.toByteArray(), false);
                                break;
                            case "VERSION":
                                out.write(Terminal.getOptions().getVersion().getBytes());
                                sender.sendByte(out.toByteArray(), false);
                                break;
                            case "TIMEZONE":
                                out.write(Terminal.getOptions().getTimeZone().getID().getBytes());
                                sender.sendByte(out.toByteArray(), false);
                                break;
                            case "PLUGIN_MANAGER":
                                out.writeBoolean(ServerConfig.cfg.getYml().getBoolean("server.enable_plugin"));
                                sender.sendByte(out.toByteArray(), false);
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
                                sender.sendByte(out.toByteArray(), false);
                        }
                    } catch (IOException | IllegalAccessException | InvocationTargetException ex) {
                        Terminal.printException(this.getClass(), ex);
                    }
                }
                if (chancel.equals("DISCONNECT")) {
                    try {
                        sender.disconnect(in.readUTF());
                    } catch (IOException | InvocationTargetException | IllegalAccessException ex) {
                        Terminal.printException(this.getClass(), ex);
                    }
                }
            }
        }
    }
}
