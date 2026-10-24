package com.fengbox.mixin;

import com.fengbox.FengBoxClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class TridentItemMixin {
    /**
     * Trident impulse: redirect the getBonusAttackDamage call inside
     * PlayerEntity.attack() to add extra damage when the player holds
     * a trident. This avoids relying on DamageSource.getSource(), which
     * may return null in 1.21.11 where DamageSource is created via
     * ItemStack.getDamageSource() instead of playerAttack().
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getBonusAttackDamage(Lnet/minecraft/entity/Entity;FLnet/minecraft/entity/damage/DamageSource;)F"))
    private float redirectGetBonusAttackDamage(Item item, Entity target, float baseAttackDamage, DamageSource damageSource) {
        float original = item.getBonusAttackDamage(target, baseAttackDamage, damageSource);
        if (FengBoxClient.tridentImpulseEnabled) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (player.getMainHandStack().getItem() instanceof TridentItem) {
                return original + FengBoxClient.tridentImpulseSpeed;
            }
        }
        return original;
    }
}
