package com.fengbox.module;

import com.fengbox.module.setting.Setting;

/**
 * 只控制 FengBoxClient 中一个布尔开关的模块（行为由 mixin / tick 逻辑实现）。
 */
public class FlagModule extends Module {

    public FlagModule(String name, String description, Category category, String flagName) {
        super(name, description, category);
        bindFlag(flagName);
        addSetting(createEnabledSetting());
    }

    public FlagModule(String name, String description, Category category, String flagName, int keyCode) {
        super(name, description, category, keyCode);
        bindFlag(flagName);
        addSetting(createEnabledSetting());
    }

    public FlagModule(String name, String description, Category category, String flagName, Setting<?>... extraSettings) {
        super(name, description, category);
        bindFlag(flagName);
        addSetting(createEnabledSetting());
        for (Setting<?> s : extraSettings) {
            addSetting(s);
        }
    }
}