package cn.newcraft.terminal.operate;

import cn.newcraft.terminal.exception.IllegalNameException;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.network.Sender;
import cn.newcraft.terminal.util.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class OperateManager extends OperateInfo {

    private static final HashMap<String, OperateManager> regOperate = new HashMap<>();

    public OperateManager(String name, String desc, boolean isTarget) {
        super(name, desc, isTarget);
    }

    public static Map<String, OperateManager> getOperateMap() {
        return regOperate;
    }

    public static void regOperate(OperateManager operateManager) throws IllegalNameException {
        if (!(Pattern.matches("[a-zA-Z0-9_]*", operateManager.getName())) ||
                (Method.equals(operateManager.getName().toLowerCase(), "add", "send", "active", "start", "shutdown", "reboot"))) {
            throw new IllegalNameException("The name " + operateManager.getName() + " is illegal!");
        }
        if (regOperate.get(operateManager.getName()) != null) {
            throw new IllegalNameException("The name " + operateManager.getName() + " is existed!");
        }
        regOperate.put("[" + operateManager.getName() + "]", operateManager);
    }

    public abstract void onOperate(Screen screen, Sender sender);
}

class OperateInfo {

    private String name;
    private String desc;
    private boolean isTarget;

    public OperateInfo(String name, String desc, boolean isTarget) {
        this.name = name;
        this.desc = desc;
        this.isTarget = isTarget;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isTarget() {
        return isTarget;
    }
}
