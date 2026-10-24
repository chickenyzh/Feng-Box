/*
 * 
 */
package com.fengbox;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class InventoryUtils {
    public static final Map<String, Integer> TIER_MAP = Map.of("netherite", 5, "diamond", 4, "iron", 3, "golden", 2, "chainmail", 2, "stone", 2, "leather", 1, "wooden", 1);

    private InventoryUtils() {
    }

    public static float weaponScore(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0f;
        }
        Item item = stack.getItem();
        // TODO: SwordItem removed in 1.21.2+. Use ToolComponent for accurate damage.
        // if (item instanceof SwordItem) {
        //     return ((SwordItem)item).getMaterial().getAttackDamage() + 1.0f;
        // }
        item = stack.getItem();
        if (item instanceof AxeItem) {
            AxeItem axe = (AxeItem)item;
            return InventoryUtils.getAxeMaterialDamage(axe) + 0.5f;
        }
        return 0.0f;
    }

    public static int findBestWeaponSlot(ClientPlayerEntity player) {
        int bestSlot = -1;
        float bestScore = 0.0f;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            float s = InventoryUtils.weaponScore(stack);
            if (!(s > bestScore)) continue;
            bestScore = s;
            bestSlot = i;
        }
        return bestSlot;
    }

    public static float weaponDpsScore(ItemStack stack) {
        float atkSpd;
        float dmg = 0.0f;
        if (stack.isEmpty()) {
            return 0.0f;
        }
        Item item = stack.getItem();
        // TODO: SwordItem removed in 1.21.2+
        if (item instanceof AxeItem) {
            AxeItem axe = (AxeItem)item;
            atkSpd = 1.0f;
            dmg = InventoryUtils.getAxeMaterialDamage(axe) + 1.0f;
        } else {
            return 0.0f;
        }
        dmg += (float)InventoryUtils.enchLevel(stack, Enchantments.SHARPNESS) * 0.5f;
        return (dmg += (float)Math.max(InventoryUtils.enchLevel(stack, Enchantments.SMITE), InventoryUtils.enchLevel(stack, Enchantments.BANE_OF_ARTHROPODS)) * 1.25f) * atkSpd;
    }

    public static int findBestDamageWeaponSlot(ClientPlayerEntity player) {
        int bestSlot = -1;
        float bestScore = 0.0f;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            float s = InventoryUtils.weaponDpsScore(stack);
            if (!(s > bestScore)) continue;
            bestScore = s;
            bestSlot = i;
        }
        return bestSlot;
    }

    public static int findBestAxeSlot(ClientPlayerEntity player) {
        int bestSlot = -1;
        float bestScore = 0.0f;
        for (int i = 0; i < 9; ++i) {
            float s;
            ItemStack stack = player.getInventory().getStack(i);
            if (!(stack.getItem() instanceof AxeItem) || !((s = InventoryUtils.weaponScore(stack)) > bestScore)) continue;
            bestScore = s;
            bestSlot = i;
        }
        return bestSlot;
    }

    public static int findBestToolSlot(PlayerInventory inv, String type) {
        int bestSlot = -1;
        int bestTier = 0;
        int bestEnch = 0;
        for (int i = 0; i < 36; ++i) {
            String path;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || !(path = Registries.ITEM.getId(stack.getItem()).getPath()).endsWith("_" + type)) continue;
            int t = InventoryUtils.getGearTier(stack);
            int e = InventoryUtils.getEnchantScore(stack);
            if (t <= bestTier && (t != bestTier || e <= bestEnch)) continue;
            bestTier = t;
            bestEnch = e;
            bestSlot = i;
        }
        return bestSlot;
    }

    public static int getGearTier(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) {
            return 0;
        }
        String path = id.getPath();
        for (Map.Entry<String, Integer> e : TIER_MAP.entrySet()) {
            if (!path.startsWith(e.getKey() + "_")) continue;
            return e.getValue();
        }
        return 0;
    }

    public static int getEnchantScore(ItemStack stack) {
        if (!stack.hasEnchantments()) {
            return 0;
        }
        int score = 0;
        for (var e : EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries()) {
            score += e.getIntValue();
        }
        return score;
    }

    private static int enchLevel(ItemStack stack, RegistryKey<Enchantment> key) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return 0;
        }
        return EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key), stack);
    }
    private static float getAxeMaterialDamage(AxeItem axe) {
        String path = Registries.ITEM.getId(axe).getPath();
        if (path.contains("netherite")) return 4.0f;
        if (path.contains("diamond")) return 3.0f;
        if (path.contains("iron")) return 2.0f;
        if (path.contains("stone")) return 1.0f;
        return 0.0f; // wooden/golden
    }
}

