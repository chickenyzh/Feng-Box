package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Screen.class})
public class TitleScreenMixin {
    @Inject(method={"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"}, at={@At(value="HEAD")})
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(((Object)this) instanceof TitleScreen)) {
            return;
        }
        FengBoxClient.renderScreenBackground(context, (Screen)(Object)this);
    }

    @Redirect(method={"renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIZ)V"))
    private void skipPanorama(RotatingCubeMapRenderer renderer, DrawContext context, int width, int height, boolean render) {
        if (!FengBoxClient.shouldSkipBgEffects()) {
            renderer.render(context, width, height, render);
        }
    }
}
