package cn.newcraft.terminal.screen.graphical;

import cn.newcraft.terminal.event.Listener;
import cn.newcraft.terminal.event.SubscribeEvent;
import cn.newcraft.terminal.screen.ScreenEvent;

import javax.swing.*;
import java.awt.*;

public class GraphicalListener implements Listener {

    @SubscribeEvent
    public void onRefresh(ScreenEvent.ScreenRefreshEvent e) {
        if (e.getScreen().getGraphicalScreen() != null) {
            JScrollPane pane = e.getScreen().getGraphicalScreen().getScrollPane();
            pane.getViewport().setViewPosition(new Point(0, pane.getVerticalScrollBar().getMaximum()));
        }
    }
}
