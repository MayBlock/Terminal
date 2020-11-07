package cn.newcraft.terminal.console;

import cn.newcraft.terminal.Terminal;
import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.util.Method;

import java.util.TimeZone;

public class ConsoleEvent extends Event {

    public static class SendCommandEvent extends ConsoleEvent implements Cancellable {
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

    public static class ChangeThemeEvent extends ConsoleEvent {
        private Theme theme;

        public ChangeThemeEvent(Theme theme) {
            this.theme = theme;
        }

        public Theme getTheme() {
            return theme;
        }
    }

    public static class ClearMessageEvent extends ConsoleEvent {

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

    public static class UpdateEvent extends ConsoleEvent {

        private String newVersion;
        private String description;
        private boolean force;

        public UpdateEvent(String newVersion, String description, boolean force) {
            this.newVersion = newVersion;
            this.description = description;
            this.force = force;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public String getDescription() {
            return description;
        }

        public boolean isForce() {
            return force;
        }
    }
}
