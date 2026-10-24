package com.fengbox.module;

import com.fengbox.FengBoxClient;
import com.fengbox.module.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Method;

/**
 * 通用模块：每 tick 通过反射调用 FengBoxClient 上对应的 handleXxx(MinecraftClient) 方法，
 * 并把开关状态同步到 FengBoxClient 的静态字段。
 */
public class HandleModule extends Module {
    private final String handleMethodName;
    /** 反射缓存。 */
    private Method handleMethod;
    /** 方法不存在（源码改名/移植断链）时不再重复查找。 */
    private boolean handleMissing;
    private int errorCount;

    public HandleModule(String name, String description, Category category, String handleMethodName, String flagName) {
        this(name, description, category, handleMethodName, flagName, new Setting<?>[0]);
    }

    public HandleModule(String name, String description, Category category, String handleMethodName, String flagName, Setting<?>... extraSettings) {
        super(name, description, category);
        this.handleMethodName = handleMethodName;
        bindFlag(flagName);
        addSetting(createEnabledSetting());
        for (Setting<?> s : extraSettings) {
            addSetting(s);
        }
    }

    public HandleModule(String name, String description, Category category, String handleMethodName, String flagName, int keyCode) {
        this(name, description, category, handleMethodName, flagName, keyCode, new Setting<?>[0]);
    }

    public HandleModule(String name, String description, Category category, String handleMethodName, String flagName, int keyCode, Setting<?>... extraSettings) {
        super(name, description, category, keyCode);
        this.handleMethodName = handleMethodName;
        bindFlag(flagName);
        addSetting(createEnabledSetting());
        for (Setting<?> s : extraSettings) {
            addSetting(s);
        }
    }

    @Override
    public void onTick() {
        if (FengBoxClient.INSTANCE == null || handleMissing) return;
        if (handleMethod == null) {
            try {
                handleMethod = FengBoxClient.class.getMethod("handle" + handleMethodName, MinecraftClient.class);
            } catch (NoSuchMethodException e) {
                handleMissing = true;
                System.err.println("[Feng Box] 模块 " + name + " 的处理方法不存在: handle" + handleMethodName + "(MinecraftClient)");
                return;
            }
        }
        try {
            handleMethod.invoke(FengBoxClient.INSTANCE, mc);
        } catch (Exception e) {
            // 单元逻辑出错不能影响其它模块，但必须留下可诊断的痕迹
            if (errorCount++ < 5) {
                System.err.println("[Feng Box] 模块 " + name + " 执行出错: " + e);
                e.printStackTrace();
            }
        }
    }
}