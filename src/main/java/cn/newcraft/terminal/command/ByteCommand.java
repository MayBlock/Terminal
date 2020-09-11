package cn.newcraft.terminal.command;

import cn.newcraft.terminal.console.Prefix;
import cn.newcraft.terminal.util.Method;
import cn.newcraft.terminal.screen.Screen;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ByteCommand extends CommandManager {

    public ByteCommand() {
        super("byte", "向外部发送一条byte信息", "byte <Address> <port> <add/send> [args...]");
    }

    private ByteArrayDataOutput bytes = ByteStreams.newDataOutput();

    @Override
    public void onCommand(Screen screen, String[] args) {
        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("send")) {
                new Thread(() -> {
                    Socket socket = null;
                    screen.sendMessage(Prefix.CLIENT_THREAD.getPrefix() + " 尝试对远程服务器发送连接请求，这可能需要一段时间... [" + args[1] + "/" + args[2] + "]");
                    try {
                        bytes.writeUTF("GET");
                        bytes.writeUTF("INFO");
                        socket = new Socket(args[1], Integer.parseInt(args[2]));
                        socket.setKeepAlive(true);
                        Method.sendByte(socket, "TERMINAL", bytes.toByteArray());
                        screen.sendMessage(Prefix.CLIENT_THREAD.getPrefix() + " 已成功发送了连接请求！ Byte数组长度：" + bytes.toByteArray().length);
                        bytes = ByteStreams.newDataOutput();

                        byte[] bytes = new byte[1024];
                        InputStream inputStream = socket.getInputStream();
                        while (inputStream.read(bytes) != -1) {
                            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
                            String chancel = in.readUTF();
                            if (chancel.equals("DISCONNECT")) {
                                String reason = in.readUTF();
                                screen.sendMessage(Prefix.CLIENT_THREAD.getPrefix() + " 远程主机强制关闭了一个现有的连接 " + reason);
                                socket.close();
                                break;
                            }
                            if (chancel.equals("TEST_CONNECT")) {
                                ByteArrayDataOutput connect = ByteStreams.newDataOutput();
                                connect.writeUTF("REGULAR");
                                Method.sendByte(socket, "TERMINAL", connect.toByteArray());
                            }
                            if (chancel.equals("RETURN")) {
                                screen.sendMessage(Prefix.CLIENT_THREAD.getPrefix() + " 远程服务器返回信息");
                                screen.sendMessage(in.readUTF());
                                screen.sendMessage(in.readUTF());
                                screen.sendMessage(in.readUTF());
                                screen.sendMessage(in.readUTF());
                                screen.sendMessage(in.readUTF());
                                screen.sendMessage(in.readUTF());
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        screen.sendMessage(Prefix.CLIENT_THREAD_ERROR.getPrefix() + " 你输入的端口号不合法或无效，请重试！");
                    } catch (ConnectException e) {
                        screen.sendMessage(Prefix.CLIENT_THREAD_ERROR.getPrefix() + " 无法连接至远程服务器 [" + args[1] + "/" + args[2] + "]");
                        screen.sendMessage(Prefix.CLIENT_THREAD_ERROR.getPrefix() + " 请检查IP和端口是否输入正确后重试！");
                    } catch (IOException e) {
                        screen.sendMessage(Prefix.CLIENT_THREAD.getPrefix() + " 与远程服务器断开连接 ( " + e + " )");
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            Method.printException(this.getClass(), ex);
                        }
                    }
                }).start();
            }
            return;
        }
        if (args.length >= 5) {
            if (args[3].equalsIgnoreCase("add")) {
                StringBuilder text = new StringBuilder();
                for (int i = 4; i < args.length; i++) {
                    text.append(args[i]).append(" ");
                }
                String string = text.toString().substring(0, text.toString().length() - 1);
                bytes.writeUTF(string);
                screen.sendMessage("成功添加 " + string + " 使用add可继续添加byte信息，如需发送请使用send");
            }
            return;
        }
        screen.sendMessage("用法：" + getUsage());

        //PrintWriter pw = new PrintWriter(out);
        //pw.write("{\"connect\":{\"main_system\":\"" + System.getProperty("os.name") + "\",\"type\":\"system\",\"port\":"+socket.getLocalPort()+"}}");
        //pw.flush();
    }
}
