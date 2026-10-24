package com.fengbox.api;

import com.fengbox.module.Category;
import com.fengbox.module.FlagModule;
import com.fengbox.module.HandleModule;
import com.fengbox.module.Module;
import com.fengbox.module.ModuleManager;
import com.fengbox.module.setting.Setting;

/**
 * Feng Box Addon API - 附属 mod 注册接口。
 *
 * 通过此 API，外部 mod 可以在不改动 Feng Box 主 mod 源码的情况下，
 * 注册自定义功能模块，包括名称、功能描述、分类、设置项等。
 *
 * 使用示例：
 * <pre>{@code
 * FengBoxAPI.registerHandleModule("自定义杀戮", "自动攻击范围内敌人",
 *     Category.COMBAT, "CustomKillAura", "customKillAuraEnabled",
 *     new NumberSetting("Range", "攻击范围", 4.5, 1.0, 8.0, 0.1));
 * }</pre>
 */
public class FengBoxAPI {

    /**
     * 注册一个基于 Handle 方法的功能模块（推荐）。
     * 模块开启后会自动调用 FengBoxClient.handleXxx() 方法。
     *
     * @param name             功能名称（中文，用于 GUI 显示）
     * @param description      功能解释（中文，用于 GUI 提示）
     * @param category         功能分类（COMBAT/MOVEMENT/PLAYER/VISUAL/MISC）
     * @param handleMethodName FengBoxClient 中的 handle 方法名（如 "CustomKillAura"）
     * @param flagName         FengBoxClient 中的 boolean 字段名（如 "customKillAuraEnabled"）
     * @param settings         该功能的设置项（可为空）
     * @return 注册成功的 Module 实例
     */
    public static Module registerHandleModule(String name, String description, Category category,
                                               String handleMethodName, String flagName,
                                               Setting<?>... settings) {
        Module module = new HandleModule(name, description, category, handleMethodName, flagName, settings);
        ModuleManager.register(module);
        return module;
    }

    /**
     * 注册一个基于 Flag 开关的功能模块。
     * 模块开启后会自动设置 FengBoxClient 中的 boolean 字段。
     *
     * @param name        功能名称（中文，用于 GUI 显示）
     * @param description 功能解释（中文，用于 GUI 提示）
     * @param category    功能分类
     * @param flagName    FengBoxClient 中的 boolean 字段名
     * @param settings    该功能的设置项（可为空）
     * @return 注册成功的 Module 实例
     */
    public static Module registerFlagModule(String name, String description, Category category,
                                             String flagName, Setting<?>... settings) {
        Module module = new FlagModule(name, description, category, flagName, settings);
        ModuleManager.register(module);
        return module;
    }

    /**
     * 注册一个完全自定义的功能模块。
     * 附属 mod 需要自行继承 Module 类，实现 onTick/onEnable/onDisable 等逻辑。
     *
     * @param module 自定义 Module 实例
     * @return 注册成功的 Module 实例
     */
    public static Module registerCustomModule(Module module) {
        ModuleManager.register(module);
        return module;
    }
}
