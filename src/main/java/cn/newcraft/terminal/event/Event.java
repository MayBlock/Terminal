package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.MainPlugin;
import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;

public class Event {

    private static HashMap<Plugin, Listener> listenerMap = new HashMap<>();

    public static HashMap<Plugin, Listener> getListener() {
        return listenerMap;
    }

    public static void regListener(Plugin plugin, Listener listener) {
        listenerMap.put(plugin, listener);
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (Listener listener : listenerMap.values()) {
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
