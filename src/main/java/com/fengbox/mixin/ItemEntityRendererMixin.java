package com.fengbox.mixin;

import net.minecraft.client.render.entity.ItemEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    // Item ESP is handled by the WorldRenderEvents.END_MAIN callback calling EspRenderer.drawItemEsp()
    // which iterates all ItemEntities in the world each frame.
    // No per-entity Mixin injection needed in 1.21.11 render-state mode.
}
