package com.example.fengboxaddon;

import com.fengbox.api.FengBoxAPI;
import com.fengbox.module.Category;
import com.fengbox.module.Module;
import com.fengbox.module.setting.BooleanSetting;
import com.fengbox.module.setting.ModeSetting;
import com.fengbox.module.setting.NumberSetting;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

/**
 * Feng Box 附属 mod 示例。
 *
 * 展示如何通过 FengBoxAPI 注册自定义功能模块。
 * 将本 mod 的 jar 放入 mods 文件夹，与 Feng Box 主 mod 一起加载即可。
 */
public class AddonExample implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // ===== 方式一：注册基于 Handle 的模块（推荐）=====
        // 模块开启后会自动调用 FengBoxClient.handleCustomAimAssist()
        FengBoxAPI.registerHandleModule(
            "自定义瞄准",                    // 功能名称（显示在 ClickGUI）
            "自动瞄准附近敌人并平滑转向",      // 功能解释
            Category.COMBAT,                 // 功能分类
            "CustomAimAssist",               // FengBoxClient 中的 handle 方法名
            "customAimAssistEnabled",        // FengBoxClient 中的 boolean 字段名
            new NumberSetting("Range", "瞄准范围", 5.0, 1.0, 8.0, 0.1),
            new NumberSetting("Speed", "转向速度", 60.0, 10.0, 180.0, 5.0),
            new ModeSetting("Mode", "瞄准模式", "Normal", List.of("Normal", "Silent", "Legit"))
        );

        // ===== 方式二：注册基于 Flag 的模块 =====
        // 模块开启后会自动设置 FengBoxClient.customBlinkEnabled = true
        FengBoxAPI.registerFlagModule(
            "自定义闪烁",
            "短时间内传送一小段距离",
            Category.MOVEMENT,
            "customBlinkEnabled",
            new NumberSetting("Distance", "闪烁距离", 3.0, 1.0, 10.0, 0.5),
            new BooleanSetting("Only Ground", "仅在地面上触发", true)
        );

        // ===== 方式三：注册完全自定义模块 =====
        // 需要自行继承 Module 类，实现 onTick/onEnable/onDisable 逻辑
        Module customModule = new CustomSpeedModule();
        FengBoxAPI.registerCustomModule(customModule);
    }
}
