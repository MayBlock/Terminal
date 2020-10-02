package cn.newcraft.terminal.event.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.console.Theme;
import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.util.Method;

import java.util.TimeZone;

public class ConsoleEvent {

    public static class SendCommandEvent extends Event implements Cancellable {
        private String[] command;
        private boolean cancelled = false;

        public SendCommandEvent(String[] command) {
            this.command = command;
        }

        public String[] getCommand() {
            return command;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean b) {
            cancelled = b;
        }
    }

    public static class ChangeThemeEvent extends Event {
        private Theme theme;

        public ChangeThemeEvent(Theme theme) {
            this.theme = theme;
        }

        public Theme getTheme() {
            return theme;
        }
    }

    public static class ClearMessageEvent extends Event {

        private TimeZone currentTime;

        public ClearMessageEvent(TimeZone currentTime) {
            this.currentTime = currentTime;
        }

        public String formatCurrentTime() {
            return Method.getCurrentTime(Terminal.getOptions().getTimeZone());
        }

        public TimeZone getCurrentTime() {
            return currentTime;
        }
    }
}
