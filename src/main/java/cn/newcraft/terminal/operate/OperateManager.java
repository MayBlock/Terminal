package cn.newcraft.terminal.operate;

import cn.newcraft.terminal.exception.IllegalNameException;
import cn.newcraft.terminal.screen.Screen;
import cn.newcraft.terminal.thread.Sender;
import cn.newcraft.terminal.util.Method;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public abstract class OperateManager extends OperateInfo {

    private static HashMap<String, OperateManager> regOperate = new HashMap<>();
    private static ArrayList<String> OperateNames = Lists.newArrayList();

    public OperateManager(String name, String desc) {
        super(name, desc);
    }

    public static HashMap<String, OperateManager> getRegOperate() {
        return regOperate;
    }

    public static ArrayList<String> getOperateNames() {
        return OperateNames;
    }

    public static void regOperate(OperateManager operateManager) {
        if (!(Pattern.matches("[a-zA-Z0-9_]*", operateManager.getName())) ||
                (operateManager.getName().equalsIgnoreCase("add") ||
                        operateManager.getName().equalsIgnoreCase("send"))) {
            try {
                throw new IllegalNameException("The name " + operateManager.getName() + " is illegal!");
            } catch (IllegalNameException e) {
                Method.printException(OperateManager.class, e);
            }
            return;
        }
        OperateNames.add("[" + operateManager.getName() + "]");
        regOperate.put("[" + operateManager.getName() + "]", operateManager);
    }

    public abstract void onOperate(Screen screen, Sender sender);
}

class OperateInfo {

    private String name;
    private String desc;

    public OperateInfo(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
