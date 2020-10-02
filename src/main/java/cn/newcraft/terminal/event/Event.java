package cn.newcraft.terminal.event;

import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class Event {

    private static List<Listener> listeners = Lists.newArrayList();

    public static List<Listener> getListener() {
        return listeners;
    }

    public static void regListener(Listener listener) {
        listeners.add(listener);
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (Listener listener : listeners) {
            for (Method m : listener.getClass().getMethods()) {
                if (m.isAnnotationPresent(SubscribeEvent.class)) {
                    Parameter[] parameters = m.getParameters();
                    for (Parameter parameter : parameters) {
                        if (parameter.getType() == event.getClass()) {
                            m.invoke(listener, event);
                        }
                    }
                }
            }
        }
    }
}
