package cn.newcraft.terminal.event;

import cn.newcraft.terminal.Terminal;
import com.google.common.collect.Lists;

import java.io.EOFException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class Event {

    private static List<Event> events = Lists.newArrayList();

    public static List<Event> getEvents() {
        return events;
    }

    public static void regEvents(Event event) {
        events.add(event);
    }

    public static void callEvent(Event e) throws InvocationTargetException, IllegalAccessException {
        for (Event event : events) {
            for (Method m : event.getClass().getMethods()) {
                if (m.isAnnotationPresent(SubscribeEvent.class)) {
                    Parameter[] parameters = m.getParameters();
                    for (Parameter parameter : parameters) {
                        if (parameter.getType() == e.getClass()) {
                            m.invoke(event, e);
                        }
                    }
                }
            }
        }
    }
}
