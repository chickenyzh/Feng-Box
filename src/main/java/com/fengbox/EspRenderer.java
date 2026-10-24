package com.fengbox;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class EspRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // ========== Box ESP ==========
    public static void drawBoxEsp(MatrixStack matrices, float tickDelta) {
        if (!FengBoxClient.boxEspEnabled) return;
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        float maxDistSq = FengBoxClient.boxEspMaxDistance * FengBoxClient.boxEspMaxDistance;

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!isBoxEspTarget(e)) continue;
            if (mc.player.squaredDistanceTo(e) > maxDistSq) continue;

            Box box = e.getBoundingBox();
            int color = getBoxEspColor(e);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            drawBox(tessellator, matrix, camPos, box, r, g, b,
                    FengBoxClient.boxEspFill, FengBoxClient.boxEspFillAlpha,
                    FengBoxClient.boxEspOutline);
        }
    }

    // ========== Hitbox ESP ==========
    public static void drawHitboxEsp(MatrixStack matrices, float tickDelta) {
        if (!FengBoxClient.hitboxEspEnabled) return;
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!(e instanceof LivingEntity)) continue;

            Box box = e.getBoundingBox();
            float r = 0.0f, g = 1.0f, b = 0.0f;
            if (e instanceof Monster) { r = 1.0f; g = 0.0f; b = 0.0f; }
            else if (e instanceof PlayerEntity) { r = 1.0f; g = 1.0f; b = 0.0f; }

            drawWireframeBox(tessellator, matrix, camPos, box, r, g, b, 0.9f, 2.0f);
        }
    }

    // ========== Tracers ==========
    public static void drawTracers(MatrixStack matrices, float tickDelta) {
        if (!FengBoxClient.tracersEnabled) return;
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        Vec3d playerEye = mc.player.getCameraPosVec(tickDelta);

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (e instanceof PlayerEntity && !FengBoxClient.tracersPlayers) continue;
            if (e instanceof Monster && !FengBoxClient.tracersMobs) continue;
            if (e instanceof AnimalEntity && !FengBoxClient.tracersAnimals) continue;
            if (!(e instanceof PlayerEntity || e instanceof Monster || e instanceof AnimalEntity)) continue;

            Vec3d targetPos = new Vec3d(e.getX(), e.getY() + e.getHeight() / 2.0, e.getZ());
            float r = 1.0f, g = 1.0f, b = 1.0f;
            if (e instanceof Monster) { r = 1.0f; g = 0.0f; b = 0.0f; }
            else if (e instanceof PlayerEntity) { r = 0.0f; g = 1.0f; b = 1.0f; }
            else if (e instanceof AnimalEntity) { r = 0.0f; g = 1.0f; b = 0.0f; }

            drawLine(tessellator, matrix, camPos, playerEye, targetPos, r, g, b, 0.8f, FengBoxClient.tracersWidth);
        }
    }

    // ========== Item ESP ==========
    public static void drawItemEsp(MatrixStack matrices, float tickDelta) {
        if (!FengBoxClient.itemEspEnabled) return;
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof ItemEntity)) continue;
            Box box = e.getBoundingBox();
            drawWireframeBox(tessellator, matrix, camPos, box, 0.5f, 1.0f, 0.5f, 0.9f, 1.5f);
        }
    }

    // ========== Entity Outline ==========
    public static void drawEntityOutlines(MatrixStack matrices, float tickDelta) {
        if (!FengBoxClient.entityOutlineEnabled) return;
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        int color = FengBoxClient.entityOutlineColor;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float width = FengBoxClient.entityOutlineWidth;
        float maxDist = FengBoxClient.entityOutlineMaxDistance;
        float maxDistSq = maxDist * maxDist;

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!EntityOutlineShader.shouldOutline(e)) continue;
            if (mc.player.squaredDistanceTo(e) > maxDistSq) continue;

            Box box = e.getBoundingBox();
            drawWireframeBox(tessellator, matrix, camPos, box, r, g, b, 0.9f, width);
        }
    }

    // ========== Player Tracker HUD text ==========
    public static String getPlayerTrackerText() {
        if (!FengBoxClient.playerTrackerEnabled) return null;
        if (mc.world == null || mc.player == null) return null;
        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!(e instanceof PlayerEntity)) continue;
            double dist = mc.player.squaredDistanceTo(e);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = e;
            }
        }
        if (nearest == null) return null;
        double dist = Math.sqrt(nearestDist);
        String name = nearest.getName().getString();
        return String.format("Tracker: %s (%.1fm)", name, dist);
    }

    // ========== Utility: drawBox (fill + outline) ==========
    public static void drawBox(Tessellator tessellator, Matrix4f matrix, Vec3d camPos, Box box,
                                float r, float g, float b, boolean fill, float fillAlpha, boolean outline) {
        float x0 = (float)(box.minX - camPos.x);
        float y0 = (float)(box.minY - camPos.y);
        float z0 = (float)(box.minZ - camPos.z);
        float x1 = (float)(box.maxX - camPos.x);
        float y1 = (float)(box.maxY - camPos.y);
        float z1 = (float)(box.maxZ - camPos.z);

        if (fill) {
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            // Front
            buffer.vertex(matrix, x0, y0, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y0, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y1, z1).color(r, g, b, fillAlpha);
            // Back
            buffer.vertex(matrix, x1, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y1, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z0).color(r, g, b, fillAlpha);
            // Right
            buffer.vertex(matrix, x1, y0, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z1).color(r, g, b, fillAlpha);
            // Left
            buffer.vertex(matrix, x0, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y0, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y1, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y1, z0).color(r, g, b, fillAlpha);
            // Top
            buffer.vertex(matrix, x0, y1, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y1, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y1, z0).color(r, g, b, fillAlpha);
            // Bottom
            buffer.vertex(matrix, x0, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y0, z0).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x1, y0, z1).color(r, g, b, fillAlpha);
            buffer.vertex(matrix, x0, y0, z1).color(r, g, b, fillAlpha);
            BuiltBuffer built = buffer.endNullable();
            if (built != null) RenderCompat.drawPositionColorEsp(built);
        }

        if (outline) {
            drawWireframeBox(tessellator, matrix, camPos, box, r, g, b, 0.9f, 2.0f);
        }
    }

    // ========== Utility: drawWireframeBox ==========
    public static void drawWireframeBox(Tessellator tessellator, Matrix4f matrix, Vec3d camPos, Box box,
                                         float r, float g, float b, float a, float lineWidth) {
        float x0 = (float)(box.minX - camPos.x);
        float y0 = (float)(box.minY - camPos.y);
        float z0 = (float)(box.minZ - camPos.z);
        float x1 = (float)(box.maxX - camPos.x);
        float y1 = (float)(box.maxY - camPos.y);
        float z1 = (float)(box.maxZ - camPos.z);

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
        float lw = lineWidth;
        // bottom edges
        addEdge(buffer, matrix, x0, y0, z0, x1, y0, z0, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y0, z0, x1, y0, z1, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y0, z1, x0, y0, z1, r, g, b, a, lw);
        addEdge(buffer, matrix, x0, y0, z1, x0, y0, z0, r, g, b, a, lw);
        // top edges
        addEdge(buffer, matrix, x0, y1, z0, x1, y1, z0, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y1, z0, x1, y1, z1, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y1, z1, x0, y1, z1, r, g, b, a, lw);
        addEdge(buffer, matrix, x0, y1, z1, x0, y1, z0, r, g, b, a, lw);
        // vertical edges
        addEdge(buffer, matrix, x0, y0, z0, x0, y1, z0, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y0, z0, x1, y1, z0, r, g, b, a, lw);
        addEdge(buffer, matrix, x1, y0, z1, x1, y1, z1, r, g, b, a, lw);
        addEdge(buffer, matrix, x0, y0, z1, x0, y1, z1, r, g, b, a, lw);
        BuiltBuffer built = buffer.endNullable();
        if (built != null) RenderCompat.drawPositionColorEsp(built);
    }

    // ========== Utility: drawLine (Tracer) ==========
    public static void drawLine(Tessellator tessellator, Matrix4f matrix, Vec3d camPos,
                                 Vec3d start, Vec3d end, float r, float g, float bl, float a, float width) {
        float x0 = (float)(start.x - camPos.x);
        float y0 = (float)(start.y - camPos.y);
        float z0 = (float)(start.z - camPos.z);
        float x1 = (float)(end.x - camPos.x);
        float y1 = (float)(end.y - camPos.y);
        float z1 = (float)(end.z - camPos.z);

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
        addEdge(buffer, matrix, x0, y0, z0, x1, y1, z1, r, g, bl, a, width);
        BuiltBuffer built = buffer.endNullable();
        if (built != null) RenderCompat.drawPositionColorEsp(built);
    }

    // ========== Internal helper: addEdge ==========
    private static void addEdge(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                                 float r, float g, float bl, float a, float lw) {
        float dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float nx = len > 0.001f ? dx / len : 1.0f;
        float ny = len > 0.001f ? dy / len : 0.0f;
        float nz = len > 0.001f ? dz / len : 0.0f;
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
    }

    // ========== Box ESP target filter ==========
    private static boolean isBoxEspTarget(Entity e) {
        int mode = FengBoxClient.boxEspTargets;
        if (mode == 0) {
            return e instanceof PlayerEntity || e instanceof Monster || e instanceof AnimalEntity;
        }
        if (mode == 1) return e instanceof PlayerEntity;
        if (mode == 2) return e instanceof Monster;
        if (mode == 3) return e instanceof AnimalEntity;
        return false;
    }

    // ========== Box ESP color helper ==========
    private static int getBoxEspColor(Entity e) {
        if (FengBoxClient.boxEspColorMode == 1) {
            return FengBoxClient.boxEspColor;
        }
        if (e instanceof Monster) return 0xFFFF0000;
        if (e instanceof PlayerEntity) return 0xFF00FFFF;
        if (e instanceof AnimalEntity) return 0xFF00FF00;
        return 0xFFFFFFFF;
    }
}
