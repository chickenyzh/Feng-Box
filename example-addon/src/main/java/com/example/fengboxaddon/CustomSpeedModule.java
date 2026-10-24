package com.example.fengboxaddon;

import com.fengbox.module.Category;
import com.fengbox.module.Module;
import com.fengbox.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

/**
 * 完全自定义模块示例。
 *
 * 继承 Module 类，自行实现 onTick/onEnable/onDisable。
 * 需要自行处理 MinecraftClient 实例和游戏逻辑。
 */
public class CustomSpeedModule extends Module {
    private final NumberSetting speedSetting;

    public CustomSpeedModule() {
        super("自定义加速", "基于代码实现的自定义加速功能", Category.MOVEMENT);
        // 添加设置项
        speedSetting = new NumberSetting("Speed", "移动速度倍率", 1.5, 1.0, 5.0, 0.1);
        addSetting(speedSetting);
    }

    @Override
    public void onEnable() {
        // 模块开启时执行
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("[Feng Box] 自定义加速已开启"), true);
        }
    }

    @Override
    public void onDisable() {
        // 模块关闭时执行
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("[Feng Box] 自定义加速已关闭"), true);
        }
    }

    @Override
    public void onTick() {
        // 每 tick 执行：如果玩家在地面上且正在移动，增加速度
        if (mc.player != null && mc.player.isOnGround()) {
            double speed = speedSetting.getValue();
            // 示例：简单的水平加速（实际实现需更精细）
            double dx = mc.player.getVelocity().x * speed;
            double dz = mc.player.getVelocity().z * speed;
            mc.player.setVelocity(dx, mc.player.getVelocity().y, dz);
        }
    }
}
