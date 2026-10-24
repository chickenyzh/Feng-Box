package com.fengbox.clickgui;

import com.fengbox.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Window {
    private static final int MIN_WIDTH = 120;
    private static final int TITLE_BAR_HEIGHT = 14;
    public static final int ROW_HEIGHT = 13;
    private static final int MAX_VISIBLE_HEIGHT = 200; // 内容区域最大可见高度，超出可滚动
    private static final int ICON_SIZE = 10;

    private String title;
    private int x, y;
    private int width = MIN_WIDTH;
    private int height;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    private boolean minimized;
    private boolean valid = true;
    private int innerHeight;
    private int scrollOffset = 0; // 当前滚动偏移量（像素，正值表示向下滚动）
    private Identifier iconTexture;

    private final List<ModuleButton> children = new ArrayList<>();

    public Window(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;
    }

    public void setIcon(Identifier icon) {
        this.iconTexture = icon;
    }

    public void addChild(ModuleButton child) {
        children.add(child);
        child.setParent(this);
        invalidate();
    }

    public void invalidate() {
        this.valid = false;
    }

    public void validate() {
        if (valid) return;
        valid = true;
        int yOffset = TITLE_BAR_HEIGHT - scrollOffset; // 减去滚动偏移
        for (ModuleButton child : children) {
            child.setX(x);
            child.setY(y + yOffset);
            child.setWidth(width);
            yOffset += child.getHeight();
        }
        this.innerHeight = yOffset - TITLE_BAR_HEIGHT + scrollOffset;
        int contentH = minimized ? 0 : Math.min(innerHeight, MAX_VISIBLE_HEIGHT);
        this.height = TITLE_BAR_HEIGHT + contentH;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        validate();

        int bgColor = 0xFF111111;
        int titleBg = 0xFF880000;
        int borderColor = 0xFFCC0000;

        ctx.fill(x, y, x + width, y + TITLE_BAR_HEIGHT, titleBg);
        if (!minimized) {
            ctx.fill(x, y + TITLE_BAR_HEIGHT, x + width, y + height, bgColor);
        }
        ctx.fill(x, y, x + width, y + 1, borderColor);
        ctx.fill(x, y + height - 1, x + width, y + height, borderColor);
        ctx.fill(x, y, x + 1, y + height, borderColor);
        ctx.fill(x + width - 1, y, x + width, y + height, borderColor);

        String displayTitle = minimized ? title + " +" : title;
        String translatedTitle = com.fengbox.I18n.getCategoryName(displayTitle);
        int textX = x + 4;
        if (iconTexture != null) {
            textX += ICON_SIZE + 2;
            // 绘制分类颜色指示方块代替贴图（1.21.11 drawGuiTexture API变化，用色块+字母标识）
            int catColor = switch (title) {
                case "Combat" -> 0xFFFF5555;
                case "Movement" -> 0xFF55FF55;
                case "Player" -> 0xFF5555FF;
                case "Visual" -> 0xFFFFFF55;
                case "Misc" -> 0xFFFF55FF;
                default -> 0xFFFFFFFF;
            };
            ctx.fill(x + 3, y + 2, x + 3 + ICON_SIZE, y + 2 + ICON_SIZE, catColor);
            String initial = switch (title) {
                case "Combat" -> "C";
                case "Movement" -> "M";
                case "Player" -> "P";
                case "Visual" -> "V";
                case "Misc" -> "X";
                default -> "?";
            };
            ctx.drawText(MinecraftClient.getInstance().textRenderer, initial, x + 5, y + 3, 0xFF000000, false);
        }
        ctx.drawText(MinecraftClient.getInstance().textRenderer, translatedTitle, textX, y + 3, 0xFFFFFFFF, false);

        int btnColor = isHoveringMinimize(mouseX, mouseY) ? 0xFFFF3333 : 0xFFCC0000;
        int btnX = x + width - 12;
        int btnY = y + 2;
        ctx.fill(btnX, btnY, btnX + 10, btnY + 10, btnColor);
        ctx.drawText(MinecraftClient.getInstance().textRenderer, minimized ? "+" : "-", btnX + 3, btnY + 1, 0xFFFFFFFF, false);

        if (!minimized) {
            int contentTop = y + TITLE_BAR_HEIGHT;
            int contentBottom = y + height - 1;
            // 启用 scissor 裁剪，确保内容不越界
            ctx.enableScissor(x, contentTop, x + width, contentBottom);
            for (ModuleButton child : children) {
                child.render(ctx, mouseX, mouseY, delta);
            }
            ctx.disableScissor();
            // 滚动条在 scissor 外绘制
            if (innerHeight > MAX_VISIBLE_HEIGHT) {
                int sbX = x + width - 3;
                int sbTrackH = height - TITLE_BAR_HEIGHT - 2;
                int sbThumbH = Math.max(8, (int)(sbTrackH * ((float)MAX_VISIBLE_HEIGHT / innerHeight)));
                int sbThumbY = contentTop + 1 + (int)(sbTrackH * ((float)scrollOffset / (innerHeight - MAX_VISIBLE_HEIGHT)));
                ctx.fill(sbX, contentTop + 1, sbX + 2, contentTop + 1 + sbTrackH, 0xFF333333);
                ctx.fill(sbX, sbThumbY, sbX + 2, sbThumbY + sbThumbH, 0xFFCC0000);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveringMinimize((int)mouseX, (int)mouseY) && button == 0) {
            minimized = !minimized;
            invalidate();
            return true;
        }

        if (isHoveringTitle((int)mouseX, (int)mouseY) && button == 0) {
            dragging = true;
            dragOffsetX = (int)(mouseX - x);
            dragOffsetY = (int)(mouseY - y);
            return true;
        }

        if (!minimized) {
            for (ModuleButton child : children) {
                int cy = child.getY();
                int ch = child.getHeight();
                int contentTop = y + TITLE_BAR_HEIGHT;
                int contentBottom = y + height - 1;
                if (cy + ch > contentTop && cy < contentBottom) {
                    if (child.mouseClicked(mouseX, mouseY, button)) return true;
                }
            }
        }
        return isHovering((int)mouseX, (int)mouseY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (!minimized) {
            for (ModuleButton child : children) {
                int cy = child.getY();
                int ch = child.getHeight();
                int contentTop = y + TITLE_BAR_HEIGHT;
                int contentBottom = y + height - 1;
                if (cy + ch > contentTop && cy < contentBottom) {
                    if (child.mouseReleased(mouseX, mouseY, button)) return true;
                }
            }
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            x = (int)(mouseX - dragOffsetX);
            y = (int)(mouseY - dragOffsetY);
            int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
            x = Math.max(0, Math.min(x, sw - width));
            y = Math.max(0, Math.min(y, sh - TITLE_BAR_HEIGHT));
            invalidate();
            return true;
        }
        if (!minimized) {
            for (ModuleButton child : children) {
                int cy = child.getY();
                int ch = child.getHeight();
                int contentTop = y + TITLE_BAR_HEIGHT;
                int contentBottom = y + height - 1;
                if (cy + ch > contentTop && cy < contentBottom) {
                    if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
                }
            }
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (minimized) return false;
        if (!isHovering((int)mouseX, (int)mouseY)) return false;
        if (innerHeight <= MAX_VISIBLE_HEIGHT) return false;

        int scrollSpeed = ROW_HEIGHT;
        scrollOffset = (int)Math.max(0, Math.min(scrollOffset - amount * scrollSpeed, innerHeight - MAX_VISIBLE_HEIGHT));
        invalidate();
        return true;
    }

    private boolean isHovering(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    private boolean isHoveringTitle(int mx, int my) {
        return mx >= x && mx <= x + width - 14 && my >= y && my <= y + TITLE_BAR_HEIGHT;
    }

    private boolean isHoveringMinimize(int mx, int my) {
        return mx >= x + width - 13 && mx <= x + width - 2 && my >= y + 1 && my <= y + 12;
    }

    public String getTitle() { return title; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { validate(); return height; }
    public boolean isMinimized() { return minimized; }
    public List<ModuleButton> getChildren() { return children; }
}
