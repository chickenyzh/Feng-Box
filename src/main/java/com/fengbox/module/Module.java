package com.fengbox.module;

import com.fengbox.module.setting.BooleanSetting;
import com.fengbox.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected final String name;
    protected final String description;
    protected final Category category;
    protected boolean enabled = false;
    protected int keyCode = GLFW.GLFW_KEY_UNKNOWN;
    protected KeyBinding keyBinding;
    protected final List<Setting<?>> settings = new ArrayList<>();

    /** 对应的 FengBoxClient 静态开关字段名；null 表示没有后端字段。 */
    protected String flagName;
    /** 反射缓存，避免每 tick 重新查找。 */
    protected Field flagField;

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Module(String name, String description, Category category, int keyCode) {
        this(name, description, category);
        this.keyCode = keyCode;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender2d(net.minecraft.client.gui.DrawContext context, float tickDelta) {}
    public void onRender3d(Object context) {}

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        applyFlag();
    }

    /**
     * 创建与模块自身开关状态双向绑定的 "Enabled" 设置项。
     * GUI 显示的状态就是模块真实状态，点击即直接切换模块。
     */
    protected BooleanSetting createEnabledSetting() {
        BooleanSetting setting = new BooleanSetting("Enabled", "切换该模块", false);
        setting.bind(this::isEnabled, this::setEnabled);
        // 标记为模块开关，GUI 才会用「模块名: 开/关」显示；其余布尔项显示自己的名字
        setting.markAsModuleToggle();
        return setting;
    }

    /** 绑定模块开关所对应的 FengBoxClient 静态字段。 */
    protected void bindFlag(String flagName) {
        this.flagName = flagName;
        if (flagName == null) return;
        try {
            this.flagField = com.fengbox.FengBoxClient.class.getField(flagName);
        } catch (Exception e) {
            this.flagField = null;
            System.err.println("[Feng Box] 模块 " + name + " 的开关字段不存在: " + flagName);
        }
    }

    public String getFlagName() { return flagName; }

    /** 把模块当前状态写入后端字段。 */
    public void applyFlag() {
        if (flagField == null) return;
        try {
            flagField.setBoolean(null, enabled);
        } catch (Exception ignored) {
        }
    }

    /** 把后端字段（按键切换、指令、配置文件等其它入口改动）同步回模块状态。 */
    public void syncFromFlag() {
        if (flagField == null) return;
        try {
            setEnabled(flagField.getBoolean(null));
        } catch (Exception ignored) {
        }
    }

    public String getName() { return name; }
    public String getDisplayName() { return com.fengbox.I18n.getModuleName(name); }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
    public List<Setting<?>> getSettings() { return settings; }

    public Module addSetting(Setting<?> setting) {
        settings.add(setting);
        return this;
    }

    public boolean hasSettings() {
        return !settings.isEmpty();
    }

    public void handleKey() {
        if (keyBinding != null && keyBinding.wasPressed()) {
            toggle();
            if (mc.player != null) {
                String msgKey = "msg.feng_box." + name.toLowerCase().replace(" ", "_").replace("-", "_") + "." + (enabled ? "on" : "off");
                mc.player.sendMessage(net.minecraft.text.Text.translatable(msgKey), true);
            }
        }
    }

    public void initKeyBinding(String categoryName) {
        if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            keyBinding = new KeyBinding(
                "key.feng_box." + name.toLowerCase().replace(" ", "_"),
                InputUtil.Type.KEYSYM,
                keyCode,
                net.minecraft.client.option.KeyBinding.Category.GAMEPLAY
            );
            net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }
}
