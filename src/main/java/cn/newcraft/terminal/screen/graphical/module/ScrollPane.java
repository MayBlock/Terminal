package cn.newcraft.terminal.screen.graphical.module;

import cn.newcraft.terminal.console.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollPane extends BasicScrollBarUI {

    @Override
    public Dimension getPreferredSize(JComponent c) {
        c.setPreferredSize(new Dimension(20, 0));
        return super.getPreferredSize(c);
    }

    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = null;
        //判断滚动条是垂直的 还是水平的
        if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            gp = new GradientPaint(0, 0, Theme.getCurrentTheme().getScrollBackground(),
                    trackBounds.width, 0, Theme.getCurrentTheme().getScrollBackground());
        }

        if (this.scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
            gp = new GradientPaint(0, 0, Theme.getCurrentTheme().getScrollBackground(),
                    trackBounds.height, 0, Theme.getCurrentTheme().getScrollBackground());
        }
        g2.setPaint(gp);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        if (trackHighlight == BasicScrollBarUI.DECREASE_HIGHLIGHT)
            this.paintDecreaseHighlight(g);
        if (trackHighlight == BasicScrollBarUI.INCREASE_HIGHLIGHT)
            this.paintIncreaseHighlight(g);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(Theme.getCurrentTheme().getScrollTrack());
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.addRenderingHints(rh);
        // 半透明
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        // 填充圆角矩形
        g2.fillRoundRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1, 10, 10);

    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new Button(0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new Button(0, 0);
    }
}
