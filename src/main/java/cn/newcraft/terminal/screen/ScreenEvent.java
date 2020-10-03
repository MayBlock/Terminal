package cn.newcraft.terminal.screen;

import cn.newcraft.terminal.event.Event;
import cn.newcraft.terminal.screen.graphical.GraphicalScreen;

public class ScreenEvent {

    public static class ScreenRefreshEvent extends Event {

        private Screen screen;

        public ScreenRefreshEvent(Screen screen) {
            this.screen = screen;
        }

        public Screen getScreen() {
            return screen;
        }
    }

    public static class GraphicalEvent {
        public static class ScreenResizeEvent extends Event {

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
    }
}
