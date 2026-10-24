/*
 * Mixin for InGameHud (1.21.11):
 * renderNausea was moved from GameRenderer to InGameHud as renderNauseaOverlay.
 */
package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={InGameHud.class})
public class GameRendererMixin {
    @Inject(method={"renderNauseaOverlay(Lnet/minecraft/client/gui/DrawContext;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderNauseaOverlay(DrawContext context, float distortionStrength, CallbackInfo ci) {
        if (FengBoxClient.clearEffectsEnabled) {
            ci.cancel();
        }
    }
}

