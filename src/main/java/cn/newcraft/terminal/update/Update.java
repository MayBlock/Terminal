package cn.newcraft.terminal.update;

import java.io.IOException;

public interface Update {

    void refreshUpdate() throws IOException, NullPointerException;

    void checkUpdate(boolean ret);

    void confirmUpdate();

    void startUpdate();

    boolean isUpdate();

}
