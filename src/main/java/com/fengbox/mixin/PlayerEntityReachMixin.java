/*
 * Mixin for PlayerEntity (1.21.1):
 * ClientPlayerInteractionManager.getReachDistance 已移除，1.21.1 的交互距离
 * 由 PlayerEntity.getBlockInteractionRange() / getEntityInteractionRange() 提供。
 */
package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerEntity.class})
public class PlayerEntityReachMixin {
    @Inject(method={"getBlockInteractionRange"}, at={@At(value="RETURN")}, cancellable=true)
    private void extendBlockRange(CallbackInfoReturnable<Double> cir) {
        if (FengBoxClient.reachEnabled) {
            cir.setReturnValue(Double.valueOf(Math.max(cir.getReturnValue().doubleValue(), (double)FengBoxClient.reachRange)));
        }
    }

    @Inject(method={"getEntityInteractionRange"}, at={@At(value="RETURN")}, cancellable=true)
    private void extendEntityRange(CallbackInfoReturnable<Double> cir) {
        if (FengBoxClient.reachEnabled) {
            cir.setReturnValue(Double.valueOf(Math.max(cir.getReturnValue().doubleValue(), (double)FengBoxClient.reachEntityRange)));
        }
    }
}