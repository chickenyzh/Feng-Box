package com.fengbox.module.setting;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    /** 以 int 下标存储的后端字段（0 = 第一个模式）。 */
    private IntSupplier indexSource;
    private IntConsumer indexSink;

    public ModeSetting(String name, String description, String defaultValue, List<String> modes) {
        super(name, description, defaultValue);
        this.modes = modes;
    }

    public List<String> getModes() { return modes; }

    /** 绑定到以 int 下标存储的后端字段。 */
    public ModeSetting bindIndex(IntSupplier source, IntConsumer sink) {
        this.indexSource = source;
        this.indexSink = sink;
        return this;
    }

    @Override
    public String getValue() {
        if (indexSource != null) {
            int i = indexSource.getAsInt();
            if (i < 0 || i >= modes.size()) i = 0;
            return modes.get(i);
        }
        return super.getValue();
    }

    @Override
    public void setValue(String value) {
        int idx = modes.indexOf(value);
        if (idx < 0) idx = 0;
        super.setValue(modes.get(idx));
        if (indexSink != null) {
            indexSink.accept(idx);
        }
    }

    public void cycle() {
        int idx = modes.indexOf(getValue());
        if (idx < 0) idx = 0;
        setValue(modes.get((idx + 1) % modes.size()));
    }
}