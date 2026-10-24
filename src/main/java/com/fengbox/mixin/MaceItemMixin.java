package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaceItem.class)
public class MaceItemMixin {
    /**
     * Force mace crit: directly compute and return bonus damage based on the
     * configured fall distance, bypassing the normal fallDistance check.
     * This works on both single-player (integrated server) and client-side
     * prediction because it intercepts getBonusAttackDamage on whatever side
     * it runs, without relying on fallDistance sync.
     */
    /**
     * Force mace crit by modifying the attacker's fallDistance before
     * getBonusAttackDamage reads it, then returning true.
     * getBonusAttackDamage is called right after shouldDealAdditionalDamage,
     * so the modified fallDistance is used for damage calculation.
     */
    @Inject(method = "shouldDealAdditionalDamage", at = @At("HEAD"), cancellable = true)
    private static void onShouldDealAdditionalDamage(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (FengBoxClient.maceCritEnabled && entity instanceof PlayerEntity player) {
            if (player.getMainHandStack().getItem() instanceof MaceItem) {
                player.fallDistance = FengBoxClient.maceCritFallDistance;
                cir.setReturnValue(true);
            }
        }
    }
}
