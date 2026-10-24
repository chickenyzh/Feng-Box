package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntryListWidget.class)
public class EntryListWidgetMixin {
    @Redirect(method = "drawMenuListBackground(Lnet/minecraft/client/gui/DrawContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void skipDarkLayer(DrawContext context, RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        Identifier menuBg = Identifier.ofVanilla("textures/gui/menu_list_background.png");
        Identifier inworldBg = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");
        if (FengBoxClient.shouldSkipListBackground() && (menuBg.equals(texture) || inworldBg.equals(texture))) {
            return;
        }
        context.drawTexture(pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
}

