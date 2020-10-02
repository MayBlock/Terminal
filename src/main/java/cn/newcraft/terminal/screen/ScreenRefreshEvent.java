package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.event.Event;

public class ScreenRefreshEvent extends Event {

    private Screen screen;

    public ScreenRefreshEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
