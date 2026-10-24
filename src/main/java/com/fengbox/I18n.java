package com.fengbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight localization helper for module display names.
 * Maps English module names to Chinese equivalents.
 */
public class I18n {
    private static final Map<String, String> CHINESE_MAP = new HashMap<>();
    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        // === Combat ===
        CHINESE_MAP.put("Aim Assist", "自瞄辅助");
        CHINESE_MAP.put("Auto Anchor", "自动锚点");
        CHINESE_MAP.put("Auto Block", "自动格挡");
        CHINESE_MAP.put("Auto Clicker", "自动连点");
        CHINESE_MAP.put("Auto Crystal", "自动水晶");
        CHINESE_MAP.put("Auto Rod", "自动鱼竿");
        CHINESE_MAP.put("Auto Totem", "自动图腾");
        CHINESE_MAP.put("Auto Web", "自动蛛网");
        CHINESE_MAP.put("Bow Aim", "弓箭瞄准");
        CHINESE_MAP.put("Criticals", "暴击模式");
        CHINESE_MAP.put("Kill Aura", "杀戮光环");
        CHINESE_MAP.put("No Slow", "无减速");
        CHINESE_MAP.put("Projectile Dodge", "弹矢闪避");
        CHINESE_MAP.put("Totem Ghost", "图腾幽灵");
        CHINESE_MAP.put("Velocity", "速度修改");
        CHINESE_MAP.put("Velocity Flight", "速度飞行");
        CHINESE_MAP.put("Auto Crit", "自动暴击");
        CHINESE_MAP.put("Mace Crit", "重锤暴击");
        CHINESE_MAP.put("Trident Impulse", "三叉戟脉冲");
        CHINESE_MAP.put("Auto Sword", "自动换剑");
        CHINESE_MAP.put("Auto Shield", "自动举盾");
        CHINESE_MAP.put("Auto Gap", "自动金苹果");
        CHINESE_MAP.put("Super Knockback", "超级击退");
        CHINESE_MAP.put("Auto Pearl", "自动珍珠");
        CHINESE_MAP.put("Auto Shoot", "自动射击");

        // === Movement ===
        CHINESE_MAP.put("Anti Fall", "防摔落");
        CHINESE_MAP.put("Auto Walk", "自动行走");
        CHINESE_MAP.put("Elytra Flight", "鞘翅飞行");
        CHINESE_MAP.put("Flight", "飞行模式");
        CHINESE_MAP.put("Gui Move", "界面移动");
        CHINESE_MAP.put("Jesus", "水上行走");
        CHINESE_MAP.put("Multi Jump", "多段跳跃");
        CHINESE_MAP.put("Speed", "加速模式");
        CHINESE_MAP.put("Sprint", "自动疾跑");
        CHINESE_MAP.put("Step", "自动上坡");
        CHINESE_MAP.put("Scaffold", "自动搭路");
        CHINESE_MAP.put("Packet Fly", "数据包飞行");
        CHINESE_MAP.put("Tower", "自动搭高");
        CHINESE_MAP.put("Long Jump", "长跳模式");
        CHINESE_MAP.put("High Jump", "高跳模式");
        CHINESE_MAP.put("Air Jump", "空中跳跃");
        CHINESE_MAP.put("Air Place", "空中放置");
        CHINESE_MAP.put("Blink", "闪烁模式");
        CHINESE_MAP.put("Bunny Hop", "兔子跳跃");

        // === Player ===
        CHINESE_MAP.put("Anti Afk", "防挂机");
        CHINESE_MAP.put("Auto Bridge", "自动搭桥");
        CHINESE_MAP.put("Auto Eat", "自动进食");
        CHINESE_MAP.put("Auto Respawn", "自动重生");
        CHINESE_MAP.put("Auto Tool", "自动换工具");
        CHINESE_MAP.put("Chest Stealer", "自动拿箱");
        CHINESE_MAP.put("Dummy Interact", "假人交互");
        CHINESE_MAP.put("Fast Break", "快速挖掘");
        CHINESE_MAP.put("Fast Use", "快速使用");
        CHINESE_MAP.put("Hotbar Organizer", "快捷栏整理");
        CHINESE_MAP.put("Inventory Manager", "库存管理");
        CHINESE_MAP.put("Replenish", "自动补货");
        CHINESE_MAP.put("Auto Armor", "自动穿甲");
        CHINESE_MAP.put("Auto Armor Swap", "自动换甲");
        CHINESE_MAP.put("Auto Farm", "自动农场");
        CHINESE_MAP.put("Auto Fish", "自动钓鱼");
        CHINESE_MAP.put("Auto Fishing Rod", "自动鱼竿");
        CHINESE_MAP.put("Auto Log", "自动退出");
        CHINESE_MAP.put("Auto Mine", "自动挖矿");
        CHINESE_MAP.put("Auto Pot", "自动药水");
        CHINESE_MAP.put("Auto Soup", "自动喝汤");
        CHINESE_MAP.put("Auto Steal", "自动偷窃");
        CHINESE_MAP.put("Auto Drop", "自动丢弃");
        CHINESE_MAP.put("Auto Tunnel", "自动隧道");
        CHINESE_MAP.put("Auto Trade", "自动交易");
        CHINESE_MAP.put("Auto Disconnect", "自动断连");
        CHINESE_MAP.put("Anti Death", "防死亡");
        CHINESE_MAP.put("Anti Cactus", "防仙人掌");
        CHINESE_MAP.put("Anti Cobweb", "防蛛网");
        CHINESE_MAP.put("Anti Weakness", "防虚弱");

        // === Visual ===
        CHINESE_MAP.put("Armor Save", "护甲保护");
        CHINESE_MAP.put("Fullbright", "全亮模式");
        CHINESE_MAP.put("Night Vision", "夜视模式");
        CHINESE_MAP.put("No Hurt Shake", "无受伤抖动");
        CHINESE_MAP.put("Box ESP", "方块透视");
        CHINESE_MAP.put("Chams", "透视着色");
        CHINESE_MAP.put("Item Physics", "物品物理");
        CHINESE_MAP.put("Armor HUD", "护甲显示");
        CHINESE_MAP.put("Arrow Count", "箭矢计数");
        CHINESE_MAP.put("Array List", "模块列表");
        CHINESE_MAP.put("Break Indicator", "破坏指示");
        CHINESE_MAP.put("Cape", "自定义披风");
        CHINESE_MAP.put("Capture Frame", "捕获画面");

        // === Misc ===
        CHINESE_MAP.put("Clear Effects", "清除效果");
        CHINESE_MAP.put("Freecam", "自由视角");
        CHINESE_MAP.put("Spin", "旋转模式");
        CHINESE_MAP.put("Reach Plus", "长臂猿");
        CHINESE_MAP.put("Auto Give", "自动给予");
        CHINESE_MAP.put("Auto Reply", "自动回复");
        CHINESE_MAP.put("Chat Spammer", "刷屏机器");
        CHINESE_MAP.put("Auto Loot", "自动拾取");
        CHINESE_MAP.put("Auto Respawn Delay", "重生延迟");
        CHINESE_MAP.put("Auto Rod Popper", "鱼竿爆击");
        CHINESE_MAP.put("Bedrock Breaker", "基岩破坏");

        // Category names
        CATEGORY_MAP.put("Combat", "战斗");
        CATEGORY_MAP.put("Movement", "移动");
        CATEGORY_MAP.put("Player", "玩家");
        CATEGORY_MAP.put("Visual", "视觉");
        CATEGORY_MAP.put("Misc", "其他");
    }

    public static String getModuleName(String englishName) {
        return CHINESE_MAP.getOrDefault(englishName, englishName);
    }

    public static String getCategoryName(String englishName) {
        return CATEGORY_MAP.getOrDefault(englishName, englishName);
    }
}
