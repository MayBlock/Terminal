package cn.newcraft.terminal.event;

public interface Cancellable {

    boolean isCancelled();
    void setCancelled(boolean b);
}
