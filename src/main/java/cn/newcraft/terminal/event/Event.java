package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class Event {

    private static final List<RegisteredListener> listeners = Lists.newArrayList();

    public static List<Listener> getListeners(Plugin plugin) {
        return listeners.stream()
                .filter(it -> it.getPlugin() == plugin)
                .map(RegisteredListener::getListener)
                .collect(Collectors.toList());
    }

    public static void unregisterListeners(Plugin plugin) {
        listeners.removeIf(registeredListener -> registeredListener.getPlugin().equals(plugin));
    }

    public static void regListener(Plugin plugin, Listener listener) {
        listeners.add(new RegisteredListener(listener, plugin));
    }

    public static Plugin getPluginFromListener(Listener listener) {
        for (RegisteredListener regLis : listeners) {
            if (regLis.getListener().equals(listener)) {
                return regLis.getPlugin();
            }
        }
        return null;
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (RegisteredListener regLis : listeners) {
            for (Method m : regLis.getListener().getClass().getMethods()) {
                if (m.getAnnotation(SubscribeEvent.class) != null) {
                    final Class<? extends Event> eventClass = m.getParameterTypes()[0].asSubclass(Event.class);
                    if (m.getParameterTypes().length == 1 && eventClass.isAssignableFrom(event.getClass())) {
                        m.invoke(regLis.getListener(), event);
                    }
                }
            }
        }
    }
}
