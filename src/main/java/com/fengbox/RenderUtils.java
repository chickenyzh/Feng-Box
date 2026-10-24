/*
 * Rendering utilities adapted for 1.21.11 GPU API.
 * - RenderSystem blend/cull/shader methods removed -> commented out
 * - BufferRenderer removed -> RenderCompat.drawPositionColor
 * - MatrixStack.peek().getPositionMatrix() -> Matrix3x2f to Matrix4f conversion
 */
package com.fengbox;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public class RenderUtils {

    /** Convert DrawContext's Matrix3x2f to a Matrix4f for BufferBuilder.vertex() */
    private static Matrix4f getMatrix4f(DrawContext ctx) {
        Matrix3x2f mat2d = ctx.getMatrices();
        Matrix4f m = new Matrix4f();
        m.m00(mat2d.m00());
        m.m01(mat2d.m01());
        m.m10(mat2d.m10());
        m.m11(mat2d.m11());
        m.m30(mat2d.m20());
        m.m31(mat2d.m21());
        return m;
    }

    /** Draw a built buffer using RenderCompat */
    private static void drawBuilt(BufferBuilder buf) {
        BuiltBuffer built = buf.endNullable();
        if (built != null) RenderCompat.drawPositionColor(built);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * RenderUtils.clamp01(t);
    }

    public static float clamp01(float v) {
        return v < 0.0f ? 0.0f : (v > 1.0f ? 1.0f : v);
    }

    public static float easeOutCubic(float t) {
        float x = 1.0f - RenderUtils.clamp01(t);
        return 1.0f - x * x * x;
    }

    public static float approach(float value, float target, float speed, float dt) {
        float t = 1.0f - (float)Math.exp(-speed * Math.max(dt, 0.0f));
        return value + (target - value) * RenderUtils.easeOutCubic(t);
    }

    public static int withAlpha(int color, float alpha) {
        return (int)(RenderUtils.clamp01(alpha) * 255.0f) << 24 | color & 0xFFFFFF;
    }

    public static int lerpColor(int from, int to, float t) {
        if ((t = RenderUtils.clamp01(t)) <= 0.0f) {
            return from;
        }
        if (t >= 1.0f) {
            return to;
        }
        int a = (int)((float)(from >> 24 & 0xFF) * (1.0f - t) + (float)(to >> 24 & 0xFF) * t);
        int r = (int)((float)(from >> 16 & 0xFF) * (1.0f - t) + (float)(to >> 16 & 0xFF) * t);
        int g = (int)((float)(from >> 8 & 0xFF) * (1.0f - t) + (float)(to >> 8 & 0xFF) * t);
        int b = (int)((float)(from & 0xFF) * (1.0f - t) + (float)(to & 0xFF) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int brighten(int color) {
        int a = color >>> 24;
        int r = Math.min(255, (color >> 16 & 0xFF) + 18);
        int g = Math.min(255, (color >> 8 & 0xFF) + 18);
        int b = Math.min(255, (color & 0xFF) + 18);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static float hashNoise(float x, float y) {
        return RenderUtils.fract(52.982918f * RenderUtils.fract(0.06711056f * x + 0.00583715f * y));
    }

    private static float fract(float v) {
        return v - (float)Math.floor(v);
    }

    public static void drawAccentText(DrawContext ctx, TextRenderer tr, String text, int x, int y, int accentColor, int mainColor, boolean shadow) {
        if (text == null || text.isEmpty()) {
            return;
        }
        String first = text.substring(0, 1);
        String rest = text.length() > 1 ? text.substring(1) : "";
        ctx.drawText(tr, (Text)Text.literal((String)first), x, y, accentColor, shadow);
        if (!rest.isEmpty()) {
            ctx.drawText(tr, (Text)Text.literal((String)rest), x + tr.getWidth(first), y, mainColor, shadow);
        }
    }

    public static float responsiveScale(int screenW, int screenH) {
        return Math.max(0.5f, Math.min(1.0f, 0.1f + (2.0f * (float)screenW + (float)screenH) / 6000.0f));
    }

    public static int hsv2rgb(float h, float s, float v) {
        float b;
        float g;
        float r;
        if (s <= 0.001f) {
            r = v;
            g = v;
            b = v;
        } else {
            h -= (float)Math.floor(h);
            float h6 = h * 6.0f;
            int sect = (int)h6;
            float f = h6 - (float)sect;
            float p = v * (1.0f - s);
            float q = v * (1.0f - s * f);
            float t = v * (1.0f - s * (1.0f - f));
            switch (sect) {
                case 0: {
                    r = v;
                    g = t;
                    b = p;
                    break;
                }
                case 1: {
                    r = q;
                    g = v;
                    b = p;
                    break;
                }
                case 2: {
                    r = p;
                    g = v;
                    b = t;
                    break;
                }
                case 3: {
                    r = p;
                    g = q;
                    b = v;
                    break;
                }
                case 4: {
                    r = t;
                    g = p;
                    b = v;
                    break;
                }
                default: {
                    r = v;
                    g = p;
                    b = q;
                }
            }
        }
        return 0xFF000000 | (int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f);
    }

    public static int colormap(float t) {
        return RenderUtils.hsv2rgb(1.0f - RenderUtils.clamp01(t), 0.75f, 1.0f);
    }

    public static int blackbody(float t) {
        int b;
        int g;
        int r;
        if ((t = RenderUtils.clamp01(t)) < 0.5f) {
            float f = t * 2.0f;
            r = (int)RenderUtils.lerp(40.0f, 255.0f, f);
            g = (int)RenderUtils.lerp(5.0f, 180.0f, f);
            b = (int)RenderUtils.lerp(5.0f, 60.0f, f);
        } else {
            float f = (t - 0.5f) * 2.0f;
            r = 255;
            g = (int)RenderUtils.lerp(180.0f, 220.0f, f);
            b = (int)RenderUtils.lerp(60.0f, 255.0f, f);
        }
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static int alphaBlend(int base, int over) {
        int ba = base >> 24 & 0xFF;
        int br = base >> 16 & 0xFF;
        int bg = base >> 8 & 0xFF;
        int bb = base & 0xFF;
        int oa = over >> 24 & 0xFF;
        int or_ = over >> 16 & 0xFF;
        int og = over >> 8 & 0xFF;
        int ob = over & 0xFF;
        float fa = (float)oa / 255.0f;
        return (int)((float)oa + (float)ba * (1.0f - fa)) << 24 | (int)((float)or_ * fa + (float)br * (1.0f - fa)) << 16 | (int)((float)og * fa + (float)bg * (1.0f - fa)) << 8 | (int)((float)ob * fa + (float)bb * (1.0f - fa));
    }

    public static float tanhApprox(float x) {
        float ax = Math.abs(x);
        float a = ax * (27.0f + ax * ax) / (27.0f + 9.0f * ax * ax);
        return x < 0.0f ? -a : a;
    }

    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        if ((r = Math.min(r, Math.min(w, h) / 2)) < 1) {
            ctx.fill(x, y, x + w, y + h, color);
            return;
        }
        Matrix4f m = getMatrix4f(ctx);
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float rCol = (float)(color >> 16 & 0xFF) / 255.0f;
        float gCol = (float)(color >> 8 & 0xFF) / 255.0f;
        float bCol = (float)(color & 0xFF) / 255.0f;
        // TODO: RenderSystem.enableBlend/defaultBlendFunc/disableCull removed in 1.21.11
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderUtils.addRect(buf, m, x + r, y + r, x + w - r, y + h - r, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x + r, y, x + w - r, y + r, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x + r, y + h - r, x + w - r, y + h, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x, y + r, x + r, y + h - r, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x + w - r, y + r, x + w, y + h - r, rCol, gCol, bCol, a);
        drawBuilt(buf);
        int segs = Math.max(8, r * 2);
        RenderUtils.drawCornerFan(m, x + r, y + r, r, 90.0f, 180.0f, rCol, gCol, bCol, a, segs);
        RenderUtils.drawCornerFan(m, x + w - r, y + r, r, 0.0f, 90.0f, rCol, gCol, bCol, a, segs);
        RenderUtils.drawCornerFan(m, x + w - r, y + h - r, r, 270.0f, 360.0f, rCol, gCol, bCol, a, segs);
        RenderUtils.drawCornerFan(m, x + r, y + h - r, r, 180.0f, 270.0f, rCol, gCol, bCol, a, segs);
        // TODO: RenderSystem.enableCull() removed in 1.21.11
    }

    public static void drawRoundedBorder(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 2 || h <= 2) {
            return;
        }
        if ((r = Math.min(r, Math.min(w, h) / 2)) < 1) {
            ctx.fill(x, y, x + w, y + 1, color);
            ctx.fill(x, y + h - 1, x + w, y + h, color);
            ctx.fill(x, y, x + 1, y + h, color);
            ctx.fill(x + w - 1, y, x + w, y + h, color);
            return;
        }
        Matrix4f m = getMatrix4f(ctx);
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float rCol = (float)(color >> 16 & 0xFF) / 255.0f;
        float gCol = (float)(color >> 8 & 0xFF) / 255.0f;
        float bCol = (float)(color & 0xFF) / 255.0f;
        // TODO: RenderSystem.enableBlend/defaultBlendFunc/disableCull removed in 1.21.11
        int segs = Math.max(8, r * 2);
        float stepDeg = 90.0f / (float)segs;
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderUtils.addRect(buf, m, x + r, y, x + w - r, y + 1, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x + r, y + h - 1, x + w - r, y + h, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x, y + r, x + 1, y + h - r, rCol, gCol, bCol, a);
        RenderUtils.addRect(buf, m, x + w - 1, y + r, x + w, y + h - r, rCol, gCol, bCol, a);
        RenderUtils.addCornerArc(buf, m, x + r, y + r, r, 90.0f, stepDeg, segs, rCol, gCol, bCol, a);
        RenderUtils.addCornerArc(buf, m, x + w - r, y + r, r, 0.0f, stepDeg, segs, rCol, gCol, bCol, a);
        RenderUtils.addCornerArc(buf, m, x + w - r, y + h - r, r, 270.0f, stepDeg, segs, rCol, gCol, bCol, a);
        RenderUtils.addCornerArc(buf, m, x + r, y + h - r, r, 180.0f, stepDeg, segs, rCol, gCol, bCol, a);
        drawBuilt(buf);
        // TODO: RenderSystem.enableCull() removed in 1.21.11
    }

    public static void drawImageCover(DrawContext ctx, Identifier texture, int texW, int texH, int screenW, int screenH, float alpha) {
        if (texW <= 0 || texH <= 0 || screenW <= 0 || screenH <= 0) {
            return;
        }
        int u = 0;
        int v = 0;
        int rW = texW;
        int rH = texH;
        double texAspect = (double)texW / (double)texH;
        double screenAspect = (double)screenW / (double)screenH;
        if (texAspect > screenAspect) {
            rW = (int)Math.round((double)texH * screenAspect);
            u = (texW - rW) / 2;
        } else {
            rH = (int)Math.round((double)texW / screenAspect);
            v = (texH - rH) / 2;
        }
        // TODO: RenderSystem.enableBlend/defaultBlendFunc/setShaderColor removed in 1.21.11
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, texture, 0, 0, (float)u, (float)v, rW, rH, texW, texH);
        // TODO: RenderSystem.setShaderColor reset removed in 1.21.11
    }

    public static void drawRoundedRectsBatch(DrawContext ctx, List<RoundRect> rects) {
        float bCol;
        float gCol;
        float rCol;
        float a;
        int r;
        if (rects == null || rects.isEmpty()) {
            return;
        }
        Matrix4f m = getMatrix4f(ctx);
        // TODO: RenderSystem.enableBlend/defaultBlendFunc/disableCull removed in 1.21.11
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (RoundRect rr : rects) {
            if (rr.w <= 0 || rr.h <= 0) continue;
            r = Math.min(rr.r, Math.min(rr.w, rr.h) / 2);
            a = (float)(rr.color >> 24 & 0xFF) / 255.0f;
            rCol = (float)(rr.color >> 16 & 0xFF) / 255.0f;
            gCol = (float)(rr.color >> 8 & 0xFF) / 255.0f;
            bCol = (float)(rr.color & 0xFF) / 255.0f;
            if (r < 1) {
                RenderUtils.addRect(buf, m, rr.x, rr.y, rr.x + rr.w, rr.y + rr.h, rCol, gCol, bCol, a);
                continue;
            }
            RenderUtils.addRect(buf, m, rr.x + r, rr.y + r, rr.x + rr.w - r, rr.y + rr.h - r, rCol, gCol, bCol, a);
            RenderUtils.addRect(buf, m, rr.x + r, rr.y, rr.x + rr.w - r, rr.y + r, rCol, gCol, bCol, a);
            RenderUtils.addRect(buf, m, rr.x + r, rr.y + rr.h - r, rr.x + rr.w - r, rr.y + rr.h, rCol, gCol, bCol, a);
            RenderUtils.addRect(buf, m, rr.x, rr.y + r, rr.x + r, rr.y + rr.h - r, rCol, gCol, bCol, a);
            RenderUtils.addRect(buf, m, rr.x + rr.w - r, rr.y + r, rr.x + rr.w, rr.y + rr.h - r, rCol, gCol, bCol, a);
        }
        drawBuilt(buf);
        buf = tess.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        for (RoundRect rr : rects) {
            if (rr.w <= 0 || rr.h <= 0 || (r = Math.min(rr.r, Math.min(rr.w, rr.h) / 2)) < 1) continue;
            a = (float)(rr.color >> 24 & 0xFF) / 255.0f;
            rCol = (float)(rr.color >> 16 & 0xFF) / 255.0f;
            gCol = (float)(rr.color >> 8 & 0xFF) / 255.0f;
            bCol = (float)(rr.color & 0xFF) / 255.0f;
            int segs = Math.max(8, r * 2);
            RenderUtils.addCornerTriangles(buf, m, rr.x + r, rr.y + r, r, 90.0f, 180.0f, segs, rCol, gCol, bCol, a);
            RenderUtils.addCornerTriangles(buf, m, rr.x + rr.w - r, rr.y + r, r, 0.0f, 90.0f, segs, rCol, gCol, bCol, a);
            RenderUtils.addCornerTriangles(buf, m, rr.x + rr.w - r, rr.y + rr.h - r, r, 270.0f, 360.0f, segs, rCol, gCol, bCol, a);
            RenderUtils.addCornerTriangles(buf, m, rr.x + r, rr.y + rr.h - r, r, 180.0f, 270.0f, segs, rCol, gCol, bCol, a);
        }
        drawBuilt(buf);
        // TODO: RenderSystem.enableCull() removed in 1.21.11
    }

    private static void addCornerTriangles(BufferBuilder buf, Matrix4f m, float cx, float cy, float cr, float startDeg, float endDeg, int segs, float r, float g, float b, float a) {
        for (int i = 0; i < segs; ++i) {
            float a1 = (float)Math.toRadians(startDeg + (endDeg - startDeg) * (float)i / (float)segs);
            float a2 = (float)Math.toRadians(startDeg + (endDeg - startDeg) * (float)(i + 1) / (float)segs);
            float x1 = cx + (float)Math.cos(a1) * cr;
            float y1 = cy - (float)Math.sin(a1) * cr;
            float x2 = cx + (float)Math.cos(a2) * cr;
            float y2 = cy - (float)Math.sin(a2) * cr;
            buf.vertex(m, cx, cy, 0.0f).color(r, g, b, a);
            buf.vertex(m, x1, y1, 0.0f).color(r, g, b, a);
            buf.vertex(m, x2, y2, 0.0f).color(r, g, b, a);
        }
    }

    private static void addRect(BufferBuilder buf, Matrix4f m, float x1, float y1, float x2, float y2, float r, float g, float b, float a) {
        buf.vertex(m, x1, y1, 0.0f).color(r, g, b, a);
        buf.vertex(m, x1, y2, 0.0f).color(r, g, b, a);
        buf.vertex(m, x2, y2, 0.0f).color(r, g, b, a);
        buf.vertex(m, x2, y1, 0.0f).color(r, g, b, a);
    }

    private static void drawCornerFan(Matrix4f m, float cx, float cy, float cr, float startDeg, float endDeg, float r, float g, float b, float a, int segs) {
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        buf.vertex(m, cx, cy, 0.0f).color(r, g, b, a);
        for (int i = 0; i <= segs; ++i) {
            float angle = (float)Math.toRadians(startDeg + (endDeg - startDeg) * (float)i / (float)segs);
            float x = cx + (float)Math.cos(angle) * cr;
            float y = cy - (float)Math.sin(angle) * cr;
            buf.vertex(m, x, y, 0.0f).color(r, g, b, a);
        }
        drawBuilt(buf);
    }

    private static void addCornerArc(BufferBuilder buf, Matrix4f m, float cx, float cy, float cr, float startDeg, float stepDeg, int segs, float r, float g, float b, float a) {
        float startRad = (float)Math.toRadians(startDeg);
        float stepRad = (float)Math.toRadians(stepDeg);
        for (int i = 0; i < segs; ++i) {
            float a1 = startRad + (float)i * stepRad;
            float a2 = startRad + (float)(i + 1) * stepRad;
            float ox1 = cx + (float)Math.cos(a1) * cr;
            float oy1 = cy - (float)Math.sin(a1) * cr;
            float ox2 = cx + (float)Math.cos(a2) * cr;
            float oy2 = cy - (float)Math.sin(a2) * cr;
            float ix1 = cx + (float)Math.cos(a1) * (cr - 1.0f);
            float iy1 = cy - (float)Math.sin(a1) * (cr - 1.0f);
            float ix2 = cx + (float)Math.cos(a2) * (cr - 1.0f);
            float iy2 = cy - (float)Math.sin(a2) * (cr - 1.0f);
            buf.vertex(m, ix1, iy1, 0.0f).color(r, g, b, a);
            buf.vertex(m, ox1, oy1, 0.0f).color(r, g, b, a);
            buf.vertex(m, ox2, oy2, 0.0f).color(r, g, b, a);
            buf.vertex(m, ix2, iy2, 0.0f).color(r, g, b, a);
        }
    }

    public static void flatQuad(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float bl, float a) {
        if (x0 == x1) {
            b.vertex(m, x0, y0, z0).color(r, g, bl, a);
            b.vertex(m, x0, y1, z0).color(r, g, bl, a);
            b.vertex(m, x0, y1, z1).color(r, g, bl, a);
            b.vertex(m, x0, y0, z1).color(r, g, bl, a);
        } else if (y0 == y1) {
            b.vertex(m, x0, y0, z0).color(r, g, bl, a);
            b.vertex(m, x1, y0, z0).color(r, g, bl, a);
            b.vertex(m, x1, y0, z1).color(r, g, bl, a);
            b.vertex(m, x0, y0, z1).color(r, g, bl, a);
        } else {
            b.vertex(m, x0, y0, z0).color(r, g, bl, a);
            b.vertex(m, x1, y0, z0).color(r, g, bl, a);
            b.vertex(m, x1, y1, z1).color(r, g, bl, a);
            b.vertex(m, x0, y1, z1).color(r, g, bl, a);
        }
    }

    public static void boxFill(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float bl, float a) {
        // Front face (z = z1)
        b.vertex(m, x0, y0, z1).color(r, g, bl, a);
        b.vertex(m, x1, y0, z1).color(r, g, bl, a);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a);
        b.vertex(m, x0, y1, z1).color(r, g, bl, a);

        // Back face (z = z0)
        b.vertex(m, x1, y0, z0).color(r, g, bl, a);
        b.vertex(m, x0, y0, z0).color(r, g, bl, a);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a);

        // Right face (x = x1)
        b.vertex(m, x1, y0, z1).color(r, g, bl, a);
        b.vertex(m, x1, y0, z0).color(r, g, bl, a);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a);

        // Left face (x = x0)
        b.vertex(m, x0, y0, z0).color(r, g, bl, a);
        b.vertex(m, x0, y0, z1).color(r, g, bl, a);
        b.vertex(m, x0, y1, z1).color(r, g, bl, a);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a);

        // Top face (y = y1)
        b.vertex(m, x0, y1, z1).color(r, g, bl, a);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a);

        // Bottom face (y = y0)
        b.vertex(m, x0, y0, z0).color(r, g, bl, a);
        b.vertex(m, x1, y0, z0).color(r, g, bl, a);
        b.vertex(m, x1, y0, z1).color(r, g, bl, a);
        b.vertex(m, x0, y0, z1).color(r, g, bl, a);
    }

    public static void boxEdges(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float bl, float a) {
        // Edges of the box: 12 edges, each = 2 vertices, LINES mode
        // Need POSITION_COLOR_NORMAL_LINE_WIDTH format for LINES pipeline
        float lw = 2.0f;
        // Bottom edges (y=y0)
        edge(b, m, x0, y0, z0, x1, y0, z0, r, g, bl, a, lw);
        edge(b, m, x1, y0, z0, x1, y0, z1, r, g, bl, a, lw);
        edge(b, m, x1, y0, z1, x0, y0, z1, r, g, bl, a, lw);
        edge(b, m, x0, y0, z1, x0, y0, z0, r, g, bl, a, lw);
        // Top edges (y=y1)
        edge(b, m, x0, y1, z0, x1, y1, z0, r, g, bl, a, lw);
        edge(b, m, x1, y1, z0, x1, y1, z1, r, g, bl, a, lw);
        edge(b, m, x1, y1, z1, x0, y1, z1, r, g, bl, a, lw);
        edge(b, m, x0, y1, z1, x0, y1, z0, r, g, bl, a, lw);
        // Vertical edges
        edge(b, m, x0, y0, z0, x0, y1, z0, r, g, bl, a, lw);
        edge(b, m, x1, y0, z0, x1, y1, z0, r, g, bl, a, lw);
        edge(b, m, x1, y0, z1, x1, y1, z1, r, g, bl, a, lw);
        edge(b, m, x0, y0, z1, x0, y1, z1, r, g, bl, a, lw);
    }

    private static void edge(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float bl, float a, float lw) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dz = z1 - z0;
        float len = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        float nx = len > 0.001f ? dx / len : 1.0f;
        float ny = len > 0.001f ? dy / len : 0.0f;
        float nz = len > 0.001f ? dz / len : 0.0f;
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
    }

    public static void cornerMarks(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float len, float r, float g, float bl, float a) {
        float[][] cs = new float[][]{{x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}, {x0, y0, z1}, {x0, y1, z0}, {x1, y1, z0}, {x1, y1, z1}, {x0, y1, z1}};
        float sx = x1 > x0 ? 1.0f : -1.0f;
        float sy = y1 > y0 ? 1.0f : -1.0f;
        float sz = z1 > z0 ? 1.0f : -1.0f;
        for (float[] c : cs) {
            float dx = (float)(c[0] == x0 ? 1 : -1) * sx * len;
            float dy = (float)(c[1] == y0 ? 1 : -1) * sy * len;
            float dz = (float)(c[2] == z0 ? 1 : -1) * sz * len;
            b.vertex(m, c[0], c[1], c[2]).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, c[0] + dx, c[1], c[2]).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, c[0], c[1], c[2]).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, c[0], c[1] + dy, c[2]).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, c[0], c[1], c[2]).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, c[0], c[1], c[2] + dz).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
        }
    }

    public static void ringLine(BufferBuilder b, Matrix4f m, float cx, float y, float cz, float radius, float r, float g, float bl, float a, int seg) {
        for (int i = 0; i < seg; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)seg;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)seg;
            b.vertex(m, cx + (float)(Math.cos(a0) * (double)radius), y, cz + (float)(Math.sin(a0) * (double)radius)).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
            b.vertex(m, cx + (float)(Math.cos(a1) * (double)radius), y, cz + (float)(Math.sin(a1) * (double)radius)).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(2.0f);
        }
    }

    public static class RoundRect {
        public int x;
        public int y;
        public int w;
        public int h;
        public int r;
        public int color;

        public RoundRect(int x, int y, int w, int h, int r, int color) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.r = r;
            this.color = color;
        }
    }
}
