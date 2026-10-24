package com.fengbox.clickgui;

import net.minecraft.client.gui.DrawContext;

public final class RoundedRectUtils {
    private RoundedRectUtils() {}
    public static void fillRoundedRect(DrawContext ctx, float x1, float y1, float x2, float y2, float r, int c) {
        ctx.fill((int)x1, (int)y1, (int)x2, (int)y2, c);
    }
    public static void outlineRoundedRect(DrawContext ctx, float x1, float y1, float x2, float y2, float r, float t, int c) {
        int ti = (int)t;
        ctx.fill((int)x1, (int)y1, (int)x2, (int)y1+ti, c);
        ctx.fill((int)x1, (int)y2-ti, (int)x2, (int)y2, c);
        ctx.fill((int)x1, (int)y1, (int)x1+ti, (int)y2, c);
        ctx.fill((int)x2-ti, (int)y1, (int)x2, (int)y2, c);
    }
}
