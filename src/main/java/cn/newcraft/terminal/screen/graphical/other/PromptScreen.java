package cn.newcraft.terminal.screen.graphical.other;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.event.screen.ScreenEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

public class PromptScreen extends JDialog {

    private static Dimension dim;
    private int x, y;
    private int width, height;
    private static Insets screenInsets;

    public PromptScreen() {
        this.width = 300;
        this.height = 180;
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
                this.getGraphicsConfiguration());
        x = (int) (dim.getWidth() - width - 3);
        y = (int) (dim.getHeight() - screenInsets.bottom - 3);
        this.setSize(width, height);
        this.setLocation(x, y);
        this.setBackground(Color.BLACK);
    }

    public void show(String title, String message, int keepTime, boolean confirm, String confirmMessage) {
        try {
            ScreenEvent.GraphicalEvent.ShowPromptEvent event = new ScreenEvent.GraphicalEvent.ShowPromptEvent(title, message, keepTime, confirm, confirmMessage);
            Event.callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Terminal.printException(this.getClass(), e);
        }
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        JLabel jTitle = new JLabel(title);
        JLabel close = new JLabel(" X ");
        JButton determine = new JButton(confirmMessage);
        JTextArea color = new JTextArea();
        JTextArea jTextArea = new JTextArea(message);

        jTitle.setBounds(10, 7, 270, 20);
        jTitle.setVerticalTextPosition(JLabel.CENTER);
        jTitle.setHorizontalTextPosition(JLabel.CENTER);
        jTitle.setFont(new Font("宋体", Font.BOLD, 15));
        jTitle.setForeground(Color.BLACK);
        add(jTitle);

        close.setVerticalTextPosition(JLabel.CENTER);
        close.setHorizontalTextPosition(JLabel.CENTER);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.setBounds(280, 5, 20, 20);
        close.setBackground(Color.RED);
        close.setToolTipText("关闭窗口");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                close.setOpaque(false);
                close();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                close.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                close.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setBorder(null);
                close.setOpaque(false);
            }
        });
        add(close);

        if (confirm) {
            determine.setFont(new Font("宋体", Font.PLAIN, 14));
            determine.setBounds(120, 140, 70, 30);
            determine.setCursor(new Cursor(12));
            determine.setContentAreaFilled(false);
            determine.setBorder(BorderFactory.createRaisedBevelBorder());
            determine.setBackground(Color.decode("#3366FF"));
            determine.addActionListener(arg0 -> close());
            add(determine);
        }

        color.setEditable(false);
        color.setBackground(new Color(51, 102, 153));
        color.setBounds(0, 0, 300, 35);
        add(color);

        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);
        jTextArea.setForeground(Color.BLACK);
        jTextArea.setBackground(Color.WHITE);
        jTextArea.setBounds(10, 40, 280, 100);
        add(jTextArea);

        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);
        setVisible(true);
        run(keepTime);
    }

    public void show(String title, String message, int keepTime, JButton determine) {
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        JLabel jTitle = new JLabel(title);
        JLabel close = new JLabel(" X ");
        JTextArea color = new JTextArea();
        JTextArea jTextArea = new JTextArea(message);

        jTitle.setBounds(10, 7, 270, 20);
        jTitle.setVerticalTextPosition(JLabel.CENTER);
        jTitle.setHorizontalTextPosition(JLabel.CENTER);
        jTitle.setFont(new Font("宋体", Font.BOLD, 15));
        jTitle.setForeground(Color.BLACK);
        add(jTitle);

        close.setVerticalTextPosition(JLabel.CENTER);
        close.setHorizontalTextPosition(JLabel.CENTER);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.setBounds(280, 5, 20, 20);
        close.setBackground(Color.RED);
        close.setToolTipText("关闭窗口");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                close.setOpaque(false);
                close();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                close.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                close.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setBorder(null);
                close.setOpaque(false);
            }
        });
        add(close);
        add(determine);

        color.setEditable(false);
        color.setBackground(new Color(51, 102, 153));
        color.setBounds(0, 0, 300, 35);
        add(color);

        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);
        jTextArea.setForeground(Color.BLACK);
        jTextArea.setBackground(Color.WHITE);
        jTextArea.setBounds(10, 40, 280, 100);
        add(jTextArea);

        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);
        setVisible(true);
        run(keepTime);
    }

    public void run(int keepTime) {
        new Thread(() -> {
            for (int i = 0; i <= height; i += 10) {
                try {
                    this.setLocation(x, y - i);
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                }
            }
            try {
                Thread.sleep(keepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            close();
        }).start();
    }

    public void close() {
        x = this.getX();
        y = this.getY();
        int ybottom = (int) dim.getHeight() - screenInsets.bottom;
        for (int i = 0; i <= ybottom - y; i += 10) {
            try {
                setLocation(x, y + i);
                Thread.sleep(40);
            } catch (InterruptedException ignored) {
            }
        }
        dispose();
    }

}
