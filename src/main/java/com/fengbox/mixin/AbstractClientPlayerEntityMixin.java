/*
 * Mixin for AbstractClientPlayerEntity (1.21.11):
 * cape/elytra texture override via SkinTextures record.
 */
package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayerEntity.class})
public class AbstractClientPlayerEntityMixin {
    private static final Identifier FENG_CAPE = Identifier.of("feng_box", "textures/capes/feng_cape.png");
    private static final Identifier FENG_ELYTRA = Identifier.of("feng_box", "textures/elytra/feng_elytra.png");

    @Inject(method={"getSkin()Lnet/minecraft/entity/player/SkinTextures;"}, at={@At(value="RETURN")}, cancellable=true)
    private void onGetSkin(CallbackInfoReturnable<SkinTextures> cir) {
        if (((Object)this) != MinecraftClient.getInstance().player) {
            return;
        }
        if (!FengBoxClient.capeEnabled && !FengBoxClient.elytraSkinEnabled) {
            return;
        }
        SkinTextures st = cir.getReturnValue();
        if (st == null) {
            return;
        }
        // In 1.21.11, SkinTextures is a record with body(), cape(), elytra(), model(), secure()
        // Each texture field is AssetInfo.TextureAsset which has texturePath() returning Identifier
        AssetInfo.TextureAsset capeAsset = FengBoxClient.capeEnabled
            ? new AssetInfo.TextureAsset() { @Override public Identifier texturePath() { return FENG_CAPE; } @Override public Identifier id() { return FENG_CAPE; } }
            : st.cape();
        AssetInfo.TextureAsset elytraAsset = FengBoxClient.elytraSkinEnabled
            ? new AssetInfo.TextureAsset() { @Override public Identifier texturePath() { return FENG_ELYTRA; } @Override public Identifier id() { return FENG_ELYTRA; } }
            : st.elytra();
        cir.setReturnValue(new SkinTextures(st.body(), capeAsset, elytraAsset, st.model(), st.secure()));
    }
}
