package cn.newcraft.terminal.screen.graphical.module;

import cn.newcraft.terminal.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {

    private Graphics g;
    private int arcWidth = 18, arcHeight = 18;

    public Button() {
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    public Button(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    public Button(String str) {
        super(str);
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    public Button(String str, int arcWidth, int arcHeight) {
        super(str);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.g = g;
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.getCurrentTheme().getButtonBackground());
        g2.setStroke(new BasicStroke(Integer.parseInt(Theme.getCurrentTheme().getButtonBorderCode().split(":")[1])));
        g2.addRenderingHints(rh);
        g2.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1, arcWidth, arcHeight);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Theme.getCurrentTheme().getButtonBorder());
        g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1, arcWidth, arcHeight);
    }
}
