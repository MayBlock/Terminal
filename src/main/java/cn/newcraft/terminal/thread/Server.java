package cn.newcraft.terminal.thread;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.config.ServerConfig;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;

public class Server extends ServerReceived {

    @Override
    public void onMessageReceived(Sender sender, byte[] bytes) {
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String chancel = in.readUTF();
        if (chancel.equals("GET")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            String subChancel = in.readUTF();
            try {
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
            } catch (IOException e) {
                Terminal.printException(this.getClass(), e);
            }
        }
        if (chancel.equals("DISCONNECT")) {
            try {
                sender.disconnect(in.readUTF());
            } catch (IOException e) {
                Terminal.printException(this.getClass(), e);
            }
        }
    }
}
