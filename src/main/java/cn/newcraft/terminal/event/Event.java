package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;

public class Event {

    private static HashMap<Plugin, List<Listener>> listenerMap = new HashMap<>();

    public static HashMap<Plugin, List<Listener>> getListeners() {
        return listenerMap;
    }

    public static void regListener(Plugin plugin, Listener listener) {
        List<Listener> list;
        if (listenerMap.get(plugin) != null) {
            list = listenerMap.get(plugin);
        } else {
            list = Lists.newArrayList();
        }
        list.add(listener);
        listenerMap.put(plugin, list);
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (List<Listener> listeners : listenerMap.values()) {
            for (Listener listener : listeners) {
                for (Method m : listener.getClass().getMethods()) {
                    if (m.isAnnotationPresent(SubscribeEvent.class)) {
                        for (Parameter parameter : m.getParameters()) {
                            if (parameter.getType() == event.getClass()) {
                                m.invoke(listener, event);
                            }
                        }
                    }
                }
            }
        }
    }
}
