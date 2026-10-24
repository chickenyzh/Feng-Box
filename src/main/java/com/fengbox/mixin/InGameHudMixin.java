package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import com.fengbox.module.Category;
import com.fengbox.module.Module;
import com.fengbox.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Renders the Feng Box HUD overlay showing enabled modules on the right side.
 * Injects after the main HUD render to draw on top.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        List<Module> enabled = ModuleManager.getModules().stream()
            .filter(Module::isEnabled)
            .sorted(Comparator.comparing(m -> m.getCategory().ordinal() * 1000 + m.getName().length()))
            .collect(Collectors.toList());

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int y = 4;
        int lineHeight = client.textRenderer.fontHeight + 2;
        int padding = 4;

        // Draw enabled module list on the right
        for (Module m : enabled) {
            String name = m.getDisplayName();
            int color = getCategoryColor(m.getCategory());
            int textWidth = client.textRenderer.getWidth(name);
            int x = screenWidth - textWidth - padding;

            // Draw semi-transparent background strip
            context.fill(x - padding, y, screenWidth - padding, y + lineHeight, 0x66000000);
            // Draw module name with shadow for readability
            context.drawTextWithShadow(client.textRenderer, name, x, y + 1, color);

            y += lineHeight;
        }

        // Player Tracker HUD
        if (FengBoxClient.playerTrackerEnabled) {
            String trackerText = getPlayerTrackerText(client);
            if (trackerText != null) {
                int tw = client.textRenderer.getWidth(trackerText);
                int tx = (screenWidth - tw) / 2;
                int ty = screenHeight - lineHeight - 4;
                context.fill(tx - 4, ty - 2, tx + tw + 4, ty + lineHeight + 2, 0xCC000000);
                context.drawTextWithShadow(client.textRenderer, trackerText, tx, ty, 0xFF00FFFF);
            }
        }

        // Name Tags HUD (2D list showing nearby entity info)
        if (FengBoxClient.nameTagsEnabled) {
            List<String> tags = getEntityTagList(client);
            if (!tags.isEmpty()) {
                int tagY = screenHeight / 2;
                for (String tag : tags) {
                    int tw = client.textRenderer.getWidth(tag);
                    int tx = screenWidth - tw - padding - 2;
                    context.fill(tx - 2, tagY - 1, screenWidth - padding, tagY + lineHeight + 1, 0xAA000000);
                    context.drawTextWithShadow(client.textRenderer, tag, tx, tagY, 0xFF00FF00);
                    tagY += lineHeight;
                }
            }
        }
    }

    private List<String> getEntityTagList(MinecraftClient mc) {
        List<String> list = new java.util.ArrayList<>();
        if (mc.world == null || mc.player == null) return list;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!(e instanceof LivingEntity)) continue;
            double dist = Math.sqrt(mc.player.squaredDistanceTo(e));
            if (dist > 64.0) continue;
            LivingEntity le = (LivingEntity) e;
            String name = e.getName().getString();
            float health = le.getHealth();
            float maxHealth = le.getMaxHealth();
            String line = String.format("%s %.1f/%.1f (%.1fm)", name, health, maxHealth, dist);
            list.add(line);
        }
        return list;
    }

    private String getPlayerTrackerText(MinecraftClient mc) {
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

    private int getCategoryColor(Category cat) {
        return switch (cat) {
            case COMBAT -> 0xFFFF5555;   // Red
            case MOVEMENT -> 0xFF55FF55; // Green
            case PLAYER -> 0xFF5555FF;   // Blue
            case VISUAL -> 0xFFFFFF55;   // Yellow
            case MISC -> 0xFFFF55FF;     // Magenta
        };
    }
}
