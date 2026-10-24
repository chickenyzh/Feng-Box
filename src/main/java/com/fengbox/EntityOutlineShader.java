/*
 * Entity outline shader helper.
 * In 1.21.11, GlUniform is now an interface with only close() - no set() methods.
 * The entire uniform-setting approach is broken and needs to be rewritten
 * for the new GPU rendering pipeline. All uniform code is disabled.
 */
package com.fengbox;

import com.fengbox.FengBoxClient;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityOutlineShader {
    private static PostEffectProcessor lastProcessor;

    private EntityOutlineShader() {
    }

    public static boolean tryInit(PostEffectProcessor processor) {
        if (lastProcessor == processor) {
            return true;
        }
        lastProcessor = processor;
        // TODO: GlUniform no longer has set() methods in 1.21.11
        // The uniform API was completely removed. Outline shader customization
        // needs to be rewritten using the new RenderPipeline/Sampler API.
        return false;
    }

    public static void applyUniforms() {
        // TODO: GlUniform.set() removed in 1.21.11 - uniform customization disabled
    }

    public static boolean shouldOutline(Entity entity) {
        if (!FengBoxClient.entityOutlineEnabled) {
            return false;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (entity == mc.player) {
            return false;
        }
        if (entity.isInvisible()) {
            return false;
        }
        if (mc.player != null) {
            double maxSq = (double)FengBoxClient.entityOutlineMaxDistance * (double)FengBoxClient.entityOutlineMaxDistance;
            if (entity.squaredDistanceTo((Entity)mc.player) > maxSq) {
                return false;
            }
        }
        switch (FengBoxClient.entityOutlineTargets) {
            case 1: {
                return entity instanceof Monster;
            }
            case 2: {
                return entity instanceof AnimalEntity;
            }
            case 3: {
                return entity instanceof PlayerEntity || entity instanceof Monster || entity instanceof AnimalEntity;
            }
        }
        return entity instanceof PlayerEntity;
    }
}
