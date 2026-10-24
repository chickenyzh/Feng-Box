package com.fengbox.clickgui;

import com.fengbox.module.Module;
import com.fengbox.module.setting.BooleanSetting;
import com.fengbox.module.setting.ModeSetting;
import com.fengbox.module.setting.NumberSetting;
import com.fengbox.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton {
    private int x, y;
    private int width;
    private int height = Window.ROW_HEIGHT;
    private Window parent;
    private final Module module;
    private boolean expanded = false;
    private final List<SettingComponent> settingComponents = new ArrayList<>();

    public ModuleButton(Module module) {
        this.module = module;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting bs) {
                settingComponents.add(new BooleanSettingComponent(bs));
            } else if (setting instanceof NumberSetting ns) {
                settingComponents.add(new NumberSettingComponent(ns));
            } else if (setting instanceof ModeSetting ms) {
                settingComponents.add(new ModeSettingComponent(ms));
            }
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        for (SettingComponent sc : settingComponents) {
            sc.setParent(this);
        }
    }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() {
        int h = height;
        if (expanded) {
            for (SettingComponent sc : settingComponents) {
                h += sc.getHeight();
            }
        }
        return h;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public Module getModule() { return module; }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hovering = isHovering(mouseX, mouseY);
        boolean enabled = module.isEnabled();

        int bgColor;
        int textColor;
        if (enabled) {
            bgColor = hovering ? 0xFFFF4444 : 0xFFCC0000;
            textColor = 0xFFFFFFFF;
        } else {
            bgColor = hovering ? 0xFF333333 : 0xFF1A1A1A;
            textColor = 0xFFAAAAAA;
        }

        ctx.fill(x, y, x + width, y + height, bgColor);

        String name = module.getDisplayName();
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(name);
        int maxTextWidth = width - 8;
        if (textWidth > maxTextWidth) {
            name = MinecraftClient.getInstance().textRenderer.trimToWidth(name, maxTextWidth) + "..";
        }
        ctx.drawText(MinecraftClient.getInstance().textRenderer, name, x + 4, y + 3, textColor, false);

        if (expanded) {
            int settingY = y + height;
            for (SettingComponent sc : settingComponents) {
                sc.setX(x + 4);
                sc.setY(settingY);
                sc.setWidth(width - 8);
                sc.render(ctx, mouseX, mouseY, delta);
                settingY += sc.getHeight();
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 先检查设置组件（如果展开），设置项优先处理自己的点击，避免被模块按钮拦截
        if (expanded) {
            for (SettingComponent sc : settingComponents) {
                if (sc.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }

        if (!isHovering((int)mouseX, (int)mouseY)) {
            return false;
        }

        if (button == 0) {
            module.toggle();
            return true;
        } else if (button == 1) {
            if (!module.getSettings().isEmpty()) {
                expanded = !expanded;
                if (parent != null) parent.invalidate();
            }
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (expanded) {
            for (SettingComponent sc : settingComponents) {
                if (sc instanceof NumberSettingComponent nsc) {
                    nsc.updateDrag((int)mouseX);
                    if (nsc.isDragging()) return true;
                }
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded) {
            for (SettingComponent sc : settingComponents) {
                if (sc.mouseReleased(mouseX, mouseY, button)) return true;
            }
        }
        return false;
    }

    public boolean isHovering(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public static abstract class SettingComponent {
        protected int x, y, width, height = 12;
        protected ModuleButton parent;

        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setParent(ModuleButton parent) { this.parent = parent; }
        public abstract void render(DrawContext ctx, int mouseX, int mouseY, float delta);
        public abstract boolean mouseClicked(double mouseX, double mouseY, int button);
        public abstract boolean mouseReleased(double mouseX, double mouseY, int button);
    }

    public static class BooleanSettingComponent extends SettingComponent {
        private final BooleanSetting setting;
        public BooleanSettingComponent(BooleanSetting setting) { this.setting = setting; }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            boolean hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int bg = hover ? 0xFF333333 : 0xFF1A1A1A;
            ctx.fill(x, y, x + width, y + height, bg);
            // 用模块中文名替换"Enabled"，显示为"功能名: 开/关"
            String moduleName = (parent != null && parent.getModule() != null)
                ? parent.getModule().getDisplayName()
                : setting.getName();
            String label = moduleName + ": " + (setting.getValue() ? "开启" : "关闭");
            int color = setting.getValue() ? 0xFFFF5555 : 0xFF888888;
            ctx.drawText(MinecraftClient.getInstance().textRenderer, label, x + 2, y + 2, color, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                setting.setValue(!setting.getValue());
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    }

    public static class NumberSettingComponent extends SettingComponent {
        private final NumberSetting setting;
        private boolean dragging = false;
        private int dragStartX;
        private double dragStartValue;

        public NumberSettingComponent(NumberSetting setting) {
            this.setting = setting;
            this.height = 14;
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            boolean hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int bg = hover ? 0xFF333333 : 0xFF1A1A1A;
            ctx.fill(x, y, x + width, y + height, bg);

            String label = setting.getName() + ": " + String.format("%.2f", setting.getValue());
            ctx.drawText(MinecraftClient.getInstance().textRenderer, label, x + 2, y + 1, 0xFFCCCCCC, false);

            int barY = y + 10;
            int barHeight = 3;
            double ratio = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
            int fillWidth = (int)(ratio * (width - 4));
            ctx.fill(x + 2, barY, x + width - 2, barY + barHeight, 0xFF333333);
            ctx.fill(x + 2, barY, x + 2 + fillWidth, barY + barHeight, 0xFFFF4444);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                dragging = true;
                dragStartX = (int)mouseX;
                dragStartValue = setting.getValue();
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            dragging = false;
            return false;
        }

        public void updateDrag(int mouseX) {
            if (!dragging) return;
            double range = setting.getMax() - setting.getMin();
            double delta = (mouseX - dragStartX) / (double)(width - 4) * range;
            double newVal = Math.max(setting.getMin(), Math.min(setting.getMax(), dragStartValue + delta));
            setting.setValue(newVal);
        }

        public boolean isDragging() {
            return dragging;
        }
    }

    public static class ModeSettingComponent extends SettingComponent {
        private final ModeSetting setting;
        public ModeSettingComponent(ModeSetting setting) { this.setting = setting; }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            boolean hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int bg = hover ? 0xFF333333 : 0xFF1A1A1A;
            ctx.fill(x, y, x + width, y + height, bg);
            String label = setting.getName() + ": " + setting.getValue();
            ctx.drawText(MinecraftClient.getInstance().textRenderer, label, x + 2, y + 2, 0xFFFF5555, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                List<String> modes = setting.getModes();
                int idx = modes.indexOf(setting.getValue());
                idx = (idx + 1) % modes.size();
                setting.setValue(modes.get(idx));
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    }
}
