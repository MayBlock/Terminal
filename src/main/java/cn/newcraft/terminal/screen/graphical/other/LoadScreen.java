package cn.newcraft.terminal.screen.graphical.other;

import cn.newcraft.terminal.Terminal;

import javax.swing.*;
import java.awt.*;

public class LoadScreen extends JDialog {

    private int width;
    private int height;
    private static Thread run;
    private static boolean enable;

    public LoadScreen() {
        width = 200;
        height = 120;
    }

    public void close() {
        enable = false;
    }

    public void show(String str) {
        setLayout(null);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        JLabel image = new JLabel(new ImageIcon(Terminal.getOptions().getImageResource()));
        image.setBounds(0, -10, 200, 120);
        add(image);

        JLabel text = new JLabel(str);
        text.setFont(new Font("宋体", Font.BOLD, 15));
        text.setBounds(20, 45, 200, 120);
        text.setForeground(new Color(0, 245, 245));
        add(text);
        setVisible(true);
        enable = true;
        run = run(text);
        run.start();
    }

    private Thread run(JLabel text) {
        return new Thread(() -> {
            String str = text.getText();
            StringBuilder sb = new StringBuilder(str);
            int i = 0;
            while (enable) {
                try {
                    if (i >= 3) {
                        sb.setLength(str.length());
                        i = 0;
                    }
                    Thread.sleep(500);
                    text.setText(sb.append(".").toString());
                    i++;
                } catch (InterruptedException e) {
                    Terminal.printException(this.getClass(), e);
                }
            }
            dispose();
        });
    }
}
