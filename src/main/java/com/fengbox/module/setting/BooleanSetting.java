package com.fengbox.module.setting;

public class BooleanSetting extends Setting<Boolean> {
    /** 是否代表模块自身的开关。为 true 时 GUI 用「模块名: 开/关」显示。 */
    private boolean moduleToggle = false;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public void toggle() {
        setValue(!value);
    }

    /** 标记本项为模块自身的开关设置项。 */
    public void markAsModuleToggle() {
        this.moduleToggle = true;
    }

    /** 本项是否为模块自身的开关设置项。 */
    public boolean isModuleToggle() {
        return moduleToggle;
    }
}
