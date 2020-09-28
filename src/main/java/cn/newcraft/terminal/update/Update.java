package cn.newcraft.terminal.update;

public interface Update {

    void refreshUpdate();

    void checkUpdate(boolean ret);

    void confirmUpdate();

    void startUpdate();

    boolean isUpdate();

}
