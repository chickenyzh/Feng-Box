package com.fengbox.clickgui;

import com.fengbox.module.Category;
import com.fengbox.module.Module;
import com.fengbox.module.ModuleManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int WINDOW_WIDTH = 120;
    private static final int WINDOW_GAP = 6;

    private final List<Window> windows = new ArrayList<>();

    public ClickGuiScreen() {
        super(Text.literal("Feng Box"));
    }

    @Override
    protected void init() {
        super.init();
        if (windows.isEmpty()) {
            buildWindows();
        }
    }

    private void buildWindows() {
        windows.clear();
        int xOffset = 10;
        int yOffset = 10;

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            Window window = new Window(cat.getName(), xOffset, yOffset);

            List<Module> modules = ModuleManager.getModulesByCategory(cat);
            for (Module module : modules) {
                ModuleButton button = new ModuleButton(module);
                window.addChild(button);
            }

            windows.add(window);

            xOffset += WINDOW_WIDTH + WINDOW_GAP;
            if (xOffset + WINDOW_WIDTH > this.width - 10) {
                xOffset = 10;
                yOffset += 200;
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xE6000000);

        for (Window window : windows) {
            window.render(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean quick) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        for (int i = windows.size() - 1; i >= 0; i--) {
            Window window = windows.get(i);
            if (window.mouseClicked(mouseX, mouseY, button)) {
                if (windows.get(windows.size() - 1) != window) {
                    windows.remove(i);
                    windows.add(window);
                }
                return true;
            }
        }
        return super.mouseClicked(click, quick);
    }

    @Override
    public boolean mouseReleased(Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        for (int i = windows.size() - 1; i >= 0; i--) {
            if (windows.get(i).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        for (int i = windows.size() - 1; i >= 0; i--) {
            if (windows.get(i).mouseDragged(mouseX, mouseY, button, dx, dy)) {
                return true;
            }
        }
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontalAmount, double verticalAmount) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            if (windows.get(i).mouseScrolled(mx, my, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mx, my, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == 256) {
            close();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        return super.charTyped(charInput);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean shouldCloseOnEsc() { return true; }
}
