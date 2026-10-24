package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.component.type.KineticWeaponComponent.class)
public class KineticWeaponComponentMixin {
    /**
     * Spear impulse: amplify the movement vector used for kinetic weapon
     * damage calculation. getAmplifiedMovement() returns the entity's
     * kinetic attack movement multiplied by 20.0. By further multiplying
     * this vector, the relativeMovement dot product in usageTick() becomes
     * larger, increasing damage for ALL targets hit by the spear charge.
     */
    @Inject(method = "getAmplifiedMovement", at = @At("RETURN"), cancellable = true)
    private static void onGetAmplifiedMovement(Entity entity, CallbackInfoReturnable<Vec3d> cir) {
        if (FengBoxClient.tridentImpulseEnabled && entity instanceof PlayerEntity player) {
            ItemStack mainHand = player.getMainHandStack();
            if (mainHand.contains(DataComponentTypes.KINETIC_WEAPON)) {
                Vec3d original = cir.getReturnValue();
                cir.setReturnValue(original.multiply(FengBoxClient.tridentImpulseSpeed));
            }
        }
    }
}
