package com.fengbox.module;

import com.fengbox.FengBoxClient;
import com.fengbox.module.setting.BooleanSetting;
import com.fengbox.module.setting.ModeSetting;
import com.fengbox.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        // === Combat ===
        addModule("Aim Assist", "自动瞄准附近敌人", Category.COMBAT, "AimAssist", "aimAssistEnabled")
            .addSetting(floatSetting("Range", "瞄准范围", 1.0, 8.0, 0.1,
                () -> FengBoxClient.aimAssistRange, v -> FengBoxClient.aimAssistRange = v))
            .addSetting(floatSetting("Speed", "每秒转动角度", 1.0, 180.0, 1.0,
                () -> FengBoxClient.aimAssistSpeed, v -> FengBoxClient.aimAssistSpeed = v));

        addModule("Auto Anchor", "自动放置和引爆重生锚", Category.COMBAT, "AutoAnchor", "autoAnchorEnabled")
            .addSetting(floatSetting("Min Health", "触发最低生命值", 1.0, 20.0, 0.5,
                () -> FengBoxClient.autoAnchorMinHealth, v -> FengBoxClient.autoAnchorMinHealth = v))
            .addSetting(intSetting("Place Delay", "放置延迟(刻)", 1, 20, 1,
                () -> FengBoxClient.autoAnchorPlaceDelay, v -> FengBoxClient.autoAnchorPlaceDelay = v))
            .addSetting(intSetting("Break Delay", "引爆延迟(刻)", 1, 20, 1,
                () -> FengBoxClient.autoAnchorBreakDelay, v -> FengBoxClient.autoAnchorBreakDelay = v));

        addModule("Auto Block", "自动使用盾牌格挡", Category.COMBAT, "AutoBlock", "autoBlockEnabled")
            .addSetting(boolSetting("Only Target", "仅当有目标时格挡",
                () -> FengBoxClient.autoBlockOnlyTarget, v -> FengBoxClient.autoBlockOnlyTarget = v));

        addModule("Auto Clicker", "自动点击攻击", Category.COMBAT, "AutoClicker", "clickerEnabled")
            .addSetting(intSetting("Min CPS", "每秒最少点击次数", 1, 30, 1,
                () -> FengBoxClient.clickerMinCps, v -> FengBoxClient.clickerMinCps = v))
            .addSetting(intSetting("Max CPS", "每秒最多点击次数", 1, 30, 1,
                () -> FengBoxClient.clickerMaxCps, v -> FengBoxClient.clickerMaxCps = v))
            .addSetting(modeSetting("Random", "点击随机模式", List.of("Uniform", "Phase", "Human"),
                () -> FengBoxClient.clickerRandomMode, v -> FengBoxClient.clickerRandomMode = v));

        addModule("Auto Crystal", "自动放置和引爆末地水晶", Category.COMBAT, "AutoCrystal", "autoCrystalEnabled")
            .addSetting(floatSetting("Walls Range", "穿墙范围", 1.0, 16.0, 0.5,
                () -> FengBoxClient.autoCrystalWallsRange, v -> FengBoxClient.autoCrystalWallsRange = v))
            .addSetting(boolSetting("Smart Delay", "智能延迟模式",
                () -> FengBoxClient.autoCrystalSmartDelay, v -> FengBoxClient.autoCrystalSmartDelay = v))
            .addSetting(boolSetting("Ignore Naked", "忽略无甲玩家",
                () -> FengBoxClient.autoCrystalIgnoreNakeds, v -> FengBoxClient.autoCrystalIgnoreNakeds = v))
            .addSetting(boolSetting("Support", "支持模式",
                () -> FengBoxClient.autoCrystalSupport, v -> FengBoxClient.autoCrystalSupport = v))
            .addSetting(boolSetting("Fast Break", "快速引爆",
                () -> FengBoxClient.autoCrystalFastBreak, v -> FengBoxClient.autoCrystalFastBreak = v));

        addModule("Auto Rod", "自动使用鱼竿拉人", Category.COMBAT, "AutoRod", "autoRodEnabled")
            .addSetting(floatSetting("Range", "鱼竿范围", 1.0, 40.0, 1.0,
                () -> FengBoxClient.autoRodRange, v -> FengBoxClient.autoRodRange = v))
            .addSetting(floatSetting("Trigger Range", "触发范围", 1.0, 16.0, 0.5,
                () -> FengBoxClient.autoRodTriggerRange, v -> FengBoxClient.autoRodTriggerRange = v));

        addModule("Auto Totem", "自动使用图腾保命", Category.COMBAT, null, "autoTotemEnabled");

        addModule("Auto Web", "自动放置蜘蛛网困人", Category.COMBAT, "AutoWeb", "autoWebEnabled")
            .addSetting(floatSetting("Range", "放置范围", 3.0, 5.0, 0.1,
                () -> FengBoxClient.autoWebRange, v -> FengBoxClient.autoWebRange = v))
            .addSetting(intSetting("Delay", "放置延迟(刻)", 1, 20, 1,
                () -> FengBoxClient.autoWebDelay, v -> FengBoxClient.autoWebDelay = v));

        addModule("Bow Aim", "弓箭自动瞄准", Category.COMBAT, "BowAim", "bowAimEnabled")
            .addSetting(floatSetting("Speed", "瞄准速度", 5.0, 90.0, 1.0,
                () -> FengBoxClient.bowAimSpeed, v -> FengBoxClient.bowAimSpeed = v));

        addModule("Criticals", "自动暴击", Category.COMBAT, "Criticals", "criticalsEnabled")
            .addSetting(modeSetting("Mode", "暴击模式", List.of("Off", "Packet"),
                () -> FengBoxClient.criticalsMode, v -> FengBoxClient.criticalsMode = v));

        addModule("Kill Aura", "自动攻击范围内敌人", Category.COMBAT, "KillAura", "killAuraEnabled")
            .addSetting(floatSetting("Range", "攻击范围", 2.0, 8.0, 0.5,
                () -> FengBoxClient.killAuraRange, v -> {
                    FengBoxClient.killAuraRange = v;
                    if (FengBoxClient.killAuraWallRange > v) FengBoxClient.killAuraWallRange = v;
                }))
            .addSetting(floatSetting("CPS", "每秒攻击次数", 1.0, 20.0, 1.0,
                () -> FengBoxClient.killAuraCps, v -> FengBoxClient.killAuraCps = v))
            .addSetting(boolSetting("Auto", "自动寻找目标",
                () -> FengBoxClient.killAuraAuto, v -> FengBoxClient.killAuraAuto = v))
            .addSetting(boolSetting("Weapon Switch", "自动切换武器",
                () -> FengBoxClient.killAuraWeaponSwitch, v -> FengBoxClient.killAuraWeaponSwitch = v))
            .addSetting(modeSetting("Rotate", "转向模式", List.of("Head", "Cam", "Off"),
                () -> FengBoxClient.autoAimRotMode, v -> FengBoxClient.autoAimRotMode = v));

        addModule("No Slow", "解除使用物品时的减速", Category.COMBAT, "NoSlow", "noSlowEnabled");

        addModule("Projectile Dodge", "自动躲避弹射物", Category.COMBAT, "ProjectileDodge", "projectileDodgeEnabled");

        addModule("Totem Ghost", "图腾幽灵模式", Category.COMBAT, "TotemGhost", "totemGhostEnabled");

        addModule("Velocity", "修改击退效果", Category.COMBAT, "Velocity", "velocityEnabled")
            .addSetting(intSetting("Horizontal", "水平击退削减(%)", 0, 100, 5,
                () -> FengBoxClient.velocityHPercent, v -> FengBoxClient.velocityHPercent = v))
            .addSetting(intSetting("Vertical", "垂直击退削减(%)", 0, 100, 5,
                () -> FengBoxClient.velocityVPercent, v -> FengBoxClient.velocityVPercent = v));

        addModule("Velocity Flight", "利用击退飞行", Category.COMBAT, "VelocityFlight", "velocityFlightEnabled");

        // === Movement ===
        addModule("Anti Fall", "防止摔落伤害", Category.MOVEMENT, "AntiFall", "antiFallEnabled")
            .addSetting(modeSetting("Mode", "防摔模式", List.of("Spoof", "Packet", "MLG", "Jump"),
                () -> FengBoxClient.antiFallMode, v -> FengBoxClient.antiFallMode = v));

        addModule("Auto Walk", "自动向前行走", Category.MOVEMENT, "AutoWalk", "autoWalkEnabled");

        addModule("Elytra Flight", "鞘翅飞行增强", Category.MOVEMENT, "ElytraFlight", "elytraFlyEnabled")
            .addSetting(intSetting("FW Cooldown", "烟花冷却(毫秒)", 100, 5000, 50,
                () -> FengBoxClient.elytraFireworkCooldownMs, v -> FengBoxClient.elytraFireworkCooldownMs = v))
            .addSetting(boolSetting("Sync Aura", "与 Kill Aura 同步",
                () -> FengBoxClient.elytraFireworkSyncAura, v -> FengBoxClient.elytraFireworkSyncAura = v));

        addModule("Flight", "创造模式飞行", Category.MOVEMENT, "Flight", "flightEnabled")
            .addSetting(floatSetting("Speed", "飞行速度", 0.2, 1.0, 0.05,
                () -> FengBoxClient.flightSpeed, v -> FengBoxClient.flightSpeed = v))
            .addSetting(boolSetting("Stealth", "静默模式",
                () -> FengBoxClient.flightStealth, v -> FengBoxClient.flightStealth = v));

        addModule("Gui Move", "在GUI中移动", Category.MOVEMENT, "GuiMove", "guiMoveEnabled");

        addModule("Jesus", "水上行走", Category.MOVEMENT, "Jesus", "jesusEnabled");

        addModule("Multi Jump", "多段跳跃", Category.MOVEMENT, "MultiJump", "multiJumpEnabled")
            .addSetting(intSetting("Max Jumps", "最大跳跃次数", 2, 10, 1,
                () -> FengBoxClient.multiJumpMax, v -> FengBoxClient.multiJumpMax = v));

        addModule("Speed", "移动速度修改", Category.MOVEMENT, "Speed", "speedEnabled")
            .addSetting(floatSetting("Amount", "加速量", 0.05, 0.8, 0.01,
                () -> FengBoxClient.speedAmount, v -> FengBoxClient.speedAmount = v))
            .addSetting(boolSetting("Fast Mode", "快速模式",
                () -> FengBoxClient.speedFastMode, v -> FengBoxClient.speedFastMode = v));

        addModule("Sprint", "自动疾跑", Category.MOVEMENT, "Sprint", "sprintEnabled");

        addModule("Step", "自动走上一格高", Category.MOVEMENT, "Step", "stepEnabled");

        // === Player ===
        addModule("Anti Afk", "防止挂机被踢", Category.PLAYER, "AntiAfk", "antiAfkEnabled");

        addModule("Auto Bridge", "自动搭桥", Category.PLAYER, "AutoBridge", "autoBridgeEnabled")
            .addSetting(modeSetting("Rotate", "转向模式", List.of("Head", "Cam", "Off"),
                () -> FengBoxClient.autoBridgeRotMode, v -> FengBoxClient.autoBridgeRotMode = v));

        addModule("Auto Eat", "自动进食", Category.PLAYER, "AutoEat", "autoEatEnabled")
            .addSetting(floatSetting("Health", "触发最低生命值", 2.0, 20.0, 0.5,
                () -> FengBoxClient.autoEatHealHealth, v -> FengBoxClient.autoEatHealHealth = v))
            .addSetting(intSetting("Hunger", "触发最低饥饿值", 1, 20, 1,
                () -> FengBoxClient.autoEatHungerThreshold, v -> FengBoxClient.autoEatHungerThreshold = v));

        addModule("Auto Respawn", "死亡后自动重生", Category.PLAYER, "AutoRespawn", "autoRespawnEnabled");

        addModule("Auto Tool", "自动选择最佳工具", Category.PLAYER, "AutoTool", "autoToolEnabled");

        addModule("Chest Stealer", "自动拿取箱子物品", Category.PLAYER, "ChestStealer", "chestStealerEnabled")
            .addSetting(intSetting("Delay", "拿取间隔(刻)", 1, 10, 1,
                () -> FengBoxClient.chestStealerDelay, v -> FengBoxClient.chestStealerDelay = v))
            .addSetting(boolSetting("Smart Filter", "智能过滤",
                () -> FengBoxClient.chestStealerSmartFilter, v -> FengBoxClient.chestStealerSmartFilter = v));

        addModule("Dummy Interact", "与假人交互", Category.PLAYER, "DummyInteract", "dummyInteractEnabled");

        addModule("Fast Break", "快速破坏方块", Category.PLAYER, "FastBreak", "fastBreakEnabled");

        addModule("Fast Use", "快速使用物品", Category.PLAYER, "FastUse", "fastUseEnabled");

        addModule("Hotbar Organizer", "快捷栏自动整理", Category.PLAYER, "HotbarOrganizer", "hotbarOrganizerEnabled");

        addModule("Inventory Manager", "库存管理", Category.PLAYER, "InventoryManager", "invManagerEnabled")
            .addSetting(intSetting("Delay", "操作延迟(毫秒)", 10, 2000, 5,
                () -> FengBoxClient.invManagerDelay, v -> FengBoxClient.invManagerDelay = v))
            .addSetting(boolSetting("Auto Armor", "自动穿甲",
                () -> FengBoxClient.invManagerAutoArmor, v -> FengBoxClient.invManagerAutoArmor = v))
            .addSetting(boolSetting("Auto Weapon", "自动换武器",
                () -> FengBoxClient.invManagerAutoWeapon, v -> FengBoxClient.invManagerAutoWeapon = v))
            .addSetting(boolSetting("Auto Tools", "自动换工具",
                () -> FengBoxClient.invManagerAutoTools, v -> FengBoxClient.invManagerAutoTools = v));

        addModule("Replenish", "自动补充物品", Category.PLAYER, "Replenish", "replenishEnabled")
            .addSetting(intSetting("Threshold", "补充阈值", 0, 63, 1,
                () -> FengBoxClient.replenishThreshold, v -> FengBoxClient.replenishThreshold = v))
            .addSetting(intSetting("Delay", "补充延迟(毫秒)", 0, 500, 5,
                () -> FengBoxClient.replenishDelay, v -> FengBoxClient.replenishDelay = v));

        // === Visual ===
        addModule("Armor Save", "护甲耐久保护", Category.VISUAL, "ArmorSave", "armorSaveEnabled")
            .addSetting(intSetting("Threshold", "耐久阈值", 1, 100, 1,
                () -> FengBoxClient.armorSaveThreshold, v -> FengBoxClient.armorSaveThreshold = v));

        addModule("Fullbright", "全局光照", Category.VISUAL, "Fullbright", "fullbrightEnabled");

        addModule("Night Vision", "夜视效果", Category.VISUAL, "NightVision", "nightVisionEnabled");

        addModule("No Hurt Shake", "取消受伤抖动", Category.VISUAL, "NoHurtShake", "noHurtShakeEnabled");

        // === Misc ===
        addModule("Clear Effects", "清除药水效果", Category.MISC, "ClearEffects", "clearEffectsEnabled");

        addModule("Freecam", "自由视角", Category.MISC, "Freecam", "freecamEnabled")
            .addSetting(floatSetting("Speed", "移动速度", 0.05, 2.0, 0.05,
                () -> (float) FengBoxClient.freecamSpeed, v -> FengBoxClient.freecamSpeed = v));

        addModule("Spin", "自动旋转", Category.MISC, "Spin", "spinEnabled");

        addModule("Reach Plus", "增加交互距离", Category.MISC, null, "reachEnabled")
            .addSetting(floatSetting("Range", "距离", 1.0, 512.0, 1.0,
                () -> FengBoxClient.reachRange, v -> FengBoxClient.reachRange = v));

        addModule("Auto Crit", "自动暴击", Category.COMBAT, null, "autoCritEnabled")
            .addSetting(modeSetting("Mode", "暴击模式", List.of("None", "Jump", "Packet"),
                () -> FengBoxClient.autoCritMode, v -> FengBoxClient.autoCritMode = v));

        addModule("Mace Crit", "重锤暴击增强", Category.COMBAT, null, "maceCritEnabled");

        addModule("Trident Impulse", "三叉戟冲刺增强", Category.COMBAT, null, "tridentImpulseEnabled");

        addModule("Packet Fly", "数据包飞行", Category.MOVEMENT, "PacketFly", "packetFlyEnabled");

        addModule("Web Aura", "自动放置蜘蛛网", Category.COMBAT, "WebAuraTick", "webAuraEnabled");

        addModule("Strafe", "平移加速", Category.MOVEMENT, "Strafe", "strafeEnabled");

        addModule("Anti Hunger", "防止饥饿消耗", Category.PLAYER, "AntiHunger", "antiHungerEnabled");

        addModule("No Fall", "防止摔落伤害", Category.MOVEMENT, "NoFall", "noFallEnabled");

        addModule("Auto Pot", "自动扔药水", Category.COMBAT, "AutoPot", "autoPotEnabled");

        addModule("Xin Burrow", "穿地放置", Category.COMBAT, "XinBurrow", "xinBurrowEnabled");

        addModule("Anti Cev", "防天花板挖掘", Category.COMBAT, "AntiCev", "antiCevEnabled");

        addModule("Blocker ESP", "方块透视", Category.VISUAL, null, "blockerEspEnabled");

        addModule("Capes", "自定义披风", Category.VISUAL, null, "capeEnabled");

        // 首次同步：把配置文件里读出的开关状态反映到模块对象上
        for (Module m : modules) {
            m.syncFromFlag();
        }

        // Register tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module m : modules) {
                // 按键/指令/配置文件等其它入口改动开关时，同步回模块状态
                m.syncFromFlag();
                if (m.isEnabled()) m.onTick();
            }
        });
    }

    /**
     * 公开注册接口：允许附属 mod 动态注册自定义模块。
     * 注册的模块会自动绑定按键并在 ClickGUI / HUD 中显示。
     */
    public static void register(Module module) {
        modules.add(module);
        module.initKeyBinding("Feng Box");
    }

    private static Module addModule(String name, String description, Category category, String handleMethodName, String flagName) {
        Module module;
        if (handleMethodName != null) {
            module = new HandleModule(name, description, category, handleMethodName, flagName);
        } else {
            module = new FlagModule(name, description, category, flagName);
        }
        register(module);
        return module;
    }

    // === 设置项构造helper：全部与 FengBoxClient 的后端字段双向绑定 ===

    private static NumberSetting floatSetting(String name, String description, double min, double max, double step,
                                              Supplier<Float> get, Consumer<Float> set) {
        NumberSetting setting = new NumberSetting(name, description, get.get(), min, max, step);
        setting.bind(() -> (double) get.get(), v -> set.accept(v.floatValue()));
        return setting;
    }

    private static NumberSetting intSetting(String name, String description, double min, double max, double step,
                                            IntSupplier get, IntConsumer set) {
        NumberSetting setting = new NumberSetting(name, description, get.getAsInt(), min, max, step);
        setting.bind(() -> (double) get.getAsInt(), v -> set.accept((int) Math.round(v)));
        return setting;
    }

    private static BooleanSetting boolSetting(String name, String description,
                                              BooleanSupplier get, Consumer<Boolean> set) {
        BooleanSetting setting = new BooleanSetting(name, description, get.getAsBoolean());
        setting.bind(get::getAsBoolean, set);
        return setting;
    }

    private static ModeSetting modeSetting(String name, String description, List<String> modes,
                                          IntSupplier get, IntConsumer set) {
        int index = get.getAsInt();
        if (index < 0 || index >= modes.size()) index = 0;
        ModeSetting setting = new ModeSetting(name, description, modes.get(index), modes);
        setting.bindIndex(get, set);
        return setting;
    }

    public static List<Module> getModules() { return modules; }
    public static List<Module> getModulesByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
    public static Module getModule(String name) {
        for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }
}