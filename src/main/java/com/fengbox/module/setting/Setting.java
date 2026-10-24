package com.fengbox.module.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 单个设置项。可以绑定到 FengBoxClient 的后端字段：
 * 绑定后 {@link #getValue()} 始终读取字段真实值（GUI 显示的是当前生效值），
 * {@link #setValue(Object)} 会把改动写回字段。
 *
 * 没有绑定的设置项只保存在自身 value 里（历史行为）。
 */
public abstract class Setting<T> {
    protected final String name;
    protected final String description;
    protected T value;
    protected final T defaultValue;
    protected Consumer<T> onChange;
    /** 后端字段读取器；绑定后作为唯一事实源。 */
    protected Supplier<T> source;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public T getValue() {
        return source != null ? source.get() : value;
    }

    public T getDefaultValue() { return defaultValue; }

    /**
     * 双向绑定到后端字段。
     *
     * @param source 读取后端字段当前值
     * @param sink   把设置值写回后端字段
     */
    public Setting<T> bind(Supplier<T> source, Consumer<T> sink) {
        this.source = source;
        this.onChange = sink;
        return this;
    }

    public boolean isBound() { return source != null; }

    public void setValue(T value) {
        this.value = value;
        if (onChange != null) {
            onChange.accept(value);
        }
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    public void reset() {
        setValue(defaultValue);
    }
}