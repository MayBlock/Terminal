package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.event.Cancellable;
import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;

public class ScreenEvent extends Event {

    public static class ScreenRefreshEvent extends ScreenEvent {

        private Screen screen;

        public ScreenRefreshEvent(Screen screen) {
            this.screen = screen;
        }

        public Screen getScreen() {
            return screen;
        }
    }

    public static class ShowPaneEvent extends ScreenEvent {

        private String title;
        private String message;

        public ShowPaneEvent(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class GraphicalEvent extends ScreenEvent {
        public static class ScreenResizeEvent extends GraphicalEvent {

            private GraphicalScreen graphicalScreen;
            private int width;
            private int height;

            public ScreenResizeEvent(GraphicalScreen graphicalScreen, int width, int height) {
                this.graphicalScreen = graphicalScreen;
                this.width = width;
                this.height = height;
            }

            public GraphicalScreen getGraphicalScreen() {
                return graphicalScreen;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }
        }

        public static class ShowPromptEvent extends GraphicalEvent implements Cancellable {

            private String title;
            private String message;
            private int keepTime;
            private boolean confirm;
            private String confirmMessage;
            private boolean cancellable = false;

            public ShowPromptEvent(String title, String message, int keepTime, boolean confirm, String confirmMessage) {
                this.title = title;
                this.message = message;
                this.keepTime = keepTime;
                this.confirm = confirm;
                this.confirmMessage = confirmMessage;
            }

            public String getTitle() {
                return title;
            }

            public String getMessage() {
                return message;
            }

            public int getKeepTime() {
                return keepTime;
            }

            public boolean isConfirm() {
                return confirm;
            }

            public String getConfirmMessage() {
                return confirmMessage;
            }

            @Override
            public boolean isCancelled() {
                return cancellable;
            }

            @Override
            public void setCancelled(boolean b) {
                cancellable = b;
            }
        }
    }
}
