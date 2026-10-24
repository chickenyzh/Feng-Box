# Feng Box

Feng Box 是一款面向 Minecraft 1.21.11（Fabric）的 PvP 实用模组，专为 2b2t 等无规则服务器优化，提供战斗、移动、玩家、视觉和杂项五大分类共 **48 个功能模块**。

---

## 快速开始

1. 确保已安装 **Minecraft 1.21.11** + **Fabric Loader**
2. 将 `feng_box-1.0.0-1.21.11-v58.jar` 放入 `mods` 文件夹
3. 启动游戏，按 **默认按键** 打开 Click GUI

---

## Click GUI 使用指南

### 界面布局
- **5 个分类窗口**：战斗（红）、移动（绿）、玩家（蓝）、视觉（黄）、杂项（品红）
- 每个窗口标题栏左侧带 **分类颜色方块 + 字母标识**（C/M/P/V/X）

### 基本操作
| 操作 | 说明 |
|------|------|
| **左键点击模块整行** | 开关该功能 |
| **右键点击模块整行** | 展开/折叠该模块的设置面板 |
| **鼠标滚轮** | 在悬停的窗口内滚动内容（窗口超过 200px 时自动出现滚动条）|
| **拖拽标题栏** | 移动窗口位置 |
| **点击标题栏右侧 -/+** | 最小化/展开整个窗口 |

### 设置项交互
- **数值滑块**：左键按下并左右拖动调整数值
- **布尔开关**：左键点击切换 开启/关闭（显示为「功能名: 开启/关闭」）
- **模式选择**：左键点击循环切换不同模式（如 Packet/Jump/Mini）

---

## 功能模块一览

### 战斗（Combat）— 16 个模块

| 模块 | 功能 | 设置项 |
|------|------|--------|
| 自瞄辅助 | 自动瞄准附近敌人 | Range / Speed / 模式（Normal/Silent/Legit）|
| 自动锚点 | 自动放置和引爆重生锚 | Min Health / Place Delay / Break Delay |
| 自动格挡 | 自动使用盾牌格挡 | Only Target |
| 自动连点 | 自动点击攻击 | CPS / 模式（Burst/Consistent/Random）|
| 自动水晶 | 自动放置和引爆末地水晶 | Walls Range / Smart Delay / Ignore Naked / Support / Fast Break |
| 自动鱼竿 | 自动使用鱼竿拉人 | Range / Trigger Range |
| 自动图腾 | 自动使用图腾保命 | — |
| 自动蛛网 | 自动放置蜘蛛网困人 | Range / Delay |
| 弓箭瞄准 | 弓箭自动瞄准 | Speed / 模式（Normal/Predict/Lock）|
| 暴击模式 | 自动跳跃暴击 | 模式（Packet/Jump/Mini）|
| 杀戮光环 | 自动攻击范围内敌人 | Range / CPS / Auto / Weapon Switch / 模式（Single/Multi/Switch）|
| 无减速 | 解除使用物品时的减速 | — |
| 弹矢闪避 | 自动躲避弹射物 | — |
| 图腾幽灵 | 图腾幽灵模式 | — |
| 速度修改 | 修改击退效果 | Horizontal / Vertical |
| 速度飞行 | 利用击退飞行 | — |
| 自动暴击 | 自动暴击 | 模式（Smart/Packet/Jump）|
| 重锤暴击 | 重锤暴击增强 | — |
| 三叉戟脉冲 | 三叉戟冲刺增强 | — |
| 自动药水 | 自动扔药水 | 类型（Heal/Speed/Strength/Fire）|
| 穿地放置 | 穿地放置 | — |
| 防天花板挖掘 | 防天花板挖掘 | — |
| 蜘蛛网光环 | 自动放置蜘蛛网 | Range / Sync Crystal |

### 移动（Movement）— 11 个模块

| 模块 | 功能 | 设置项 |
|------|------|--------|
| 防摔落 | 防止摔落伤害 | 模式（Elytra/Water/Web）|
| 自动行走 | 自动向前行走 | — |
| 鞘翅飞行 | 鞘翅飞行增强 | 模式（Boost/Firework/Control）|
| 飞行模式 | 创造模式飞行 | Speed / 模式（Normal/Vanilla/Fast）|
| 界面移动 | 在 GUI 中移动 | — |
| 水上行走 | 水上行走 | — |
| 多段跳跃 | 多段跳跃 | Max Jumps |
| 加速模式 | 移动速度修改 | Amount / 模式（Strafe/YPort/BHop）|
| 自动疾跑 | 自动疾跑 | — |
| 自动上坡 | 自动走上一格高 | 模式（Normal/Vanilla/NCP）|
| 平移加速 | 平移加速 | Speed |
| 数据包飞行 | 数据包飞行 | 模式（Normal/Fast/Slow）|
| 防摔落 | 防止摔落伤害 | 模式（Packet/Spoof/MLG）|

### 玩家（Player）— 11 个模块

| 模块 | 功能 | 设置项 |
|------|------|--------|
| 防挂机 | 防止挂机被踢 | — |
| 自动搭桥 | 自动搭桥 | 模式（Normal/God/Breezily）|
| 自动进食 | 自动进食 | Health / Hunger |
| 自动重生 | 死亡后自动重生 | — |
| 自动工具 | 自动选择最佳工具 | — |
| 箱子掠夺 | 自动拿取箱子物品 | Delay / Smart Filter |
| 假人交互 | 与假人交互 | — |
| 快速破坏 | 快速破坏方块 | — |
| 快速使用 | 快速使用物品 | — |
| 快捷栏整理 | 快捷栏自动整理 | — |
| 库存管理 | 库存管理 | Delay / Auto Armor / Auto Weapon / Auto Tools |
| 自动补充 | 自动补充物品 | Threshold / Delay |
| 防止饥饿 | 防止饥饿消耗 | — |

### 视觉（Visual）— 6 个模块

| 模块 | 功能 | 设置项 |
|------|------|--------|
| 护甲保护 | 护甲耐久保护 | Threshold |
| 全局光照 | 全局光照 | — |
| 夜视效果 | 夜视效果 | — |
| 取消抖动 | 取消受伤抖动 | — |
| 方块透视 | 方块透视 | — |
| 自定义披风 | 自定义披风 | — |

### 杂项（Misc）— 4 个模块

| 模块 | 功能 | 设置项 |
|------|------|--------|
| 清除效果 | 清除药水效果 | — |
| 自由视角 | 自由视角 | Speed |
| 自动旋转 | 自动旋转 | — |
| 增加距离 | 增加交互距离 | Range |

---

## HUD 面板

开启任意功能后，屏幕右侧会显示 **竖排半透明列表**：
- 按 **分类颜色** 着色（战斗=红、移动=绿、玩家=蓝、视觉=黄、杂项=品红）
- 显示当前启用的所有模块中文名

---

## 模式选择说明

大量核心模块支持 **模式选择**（Mode Setting），在设置面板点击即可循环切换：

| 模块 | 可选模式 |
|------|---------|
| 杀戮光环 | Single（单目标）/ Multi（多目标）/ Switch（切换目标）|
| 暴击模式 | Packet（发包）/ Jump（跳跃）/ Mini（迷你跳）|
| 飞行模式 | Normal / Vanilla / Fast |
| 加速模式 | Strafe（平移）/ YPort（Y轴瞬移）/ BHop（兔子跳）|
| 防摔落 | Elytra（鞘翅）/ Water（水）/ Web（蜘蛛网）/ Packet / Spoof / MLG |
| 自动搭桥 | Normal / God / Breezily |
| 鞘翅飞行 | Boost（推进）/ Firework（烟花）/ Control（操控）|
| 自动上坡 | Normal / Vanilla / NCP |
| 弓箭瞄准 | Normal / Predict（预判）/ Lock（锁定）|
| 自动连点 | Burst（爆发）/ Consistent（稳定）/ Random（随机）|

---

## 版本历史

| 版本 | 主要改动 |
|------|---------|
| v58 | 修复 Click GUI 全部设置项不生效：设置项与模块开关改为与客户端后端字段双向绑定（开关直接切换模块启用状态，数值/模式改动即时写入运行字段）；补齐 `Velocity Flight`、`Dummy Interact` 缺失的后端开关字段；`freecamSpeed` 由不可写常量改为可调字段 |
| v57 | 修复启动崩溃：移除对 `LevelRenderer.render` 的失效 Mixin 注入，世界渲染改由 Fabric API `WorldRenderEvents.END_MAIN` 回调驱动；fabric.mod.json 声明 fabric-api 依赖；清理残留 refmap 引用 |
| v56 | 修复 fabric.mod.json 自引用依赖导致加载失败的问题；统一包名与 README 文档 |
| v50 | 开放附属 mod API（FengBoxAPI），支持动态注册自定义模块，附带完整示例 |
| v49 | 分类标题图标、15 个新功能模块、设置项中文解释、重复功能模式选择、删除重复贴图 |
| v48 | 修复设置组件越界渲染、修复设置项交互失效、清理 41 个空壳模块 |
| v47 | ClickGUI 滚轮滚动支持 |
| v46 | HUD 面板 + 全中文界面 |
| v45 | 功能修复（Velocity Flight、Auto Crit 等）|
| v44 | 红黑主题 GUI（整行点击开关）|

---

## 技术信息

- **Minecraft 版本**：1.21.11
- **加载器**：Fabric
- **映射**：Yarn
- **构建工具**：Gradle 9.2.1 + Fabric Loom 1.14.10
- **品牌名**：Feng Box

---

## 附属开发指南

Feng Box 从 **v50** 开始开放附属 mod API，允许第三方 mod 在不改动主 mod 源码的情况下，注册自定义功能模块。注册后，新模块会自动出现在 ClickGUI 和 HUD 面板中。

### 前置要求

| 项目 | 要求 |
|------|------|
| Minecraft | 1.21.11 |
| 加载器 | Fabric |
| 依赖 mod | Feng Box 主 mod（feng_box） |
| Java | ≥ 21 |

### API 调用方式

所有注册通过 `com.fengbox.api.FengBoxAPI` 类的静态方法完成，提供 **三种注册方式**：

#### 方式一：Handle 模块（推荐）

模块开启后，每 tick 自动调用 `FengBoxClient.handleXxx()` 方法。

```java
FengBoxAPI.registerHandleModule(
    String name,              // 功能名称（中文，显示在 GUI）
    String description,       // 功能解释（中文，用于提示）
    Category category,          // 分类（COMBAT/MOVEMENT/PLAYER/VISUAL/MISC）
    String handleMethodName,    // FengBoxClient 中的 handle 方法名
    String flagName,            // FengBoxClient 中的 boolean 字段名
    Setting<?>... settings      // 可选设置项
);
```

#### 方式二：Flag 模块

模块开启后，自动设置 `FengBoxClient` 中对应的 boolean 字段。

```java
FengBoxAPI.registerFlagModule(
    String name,              // 功能名称
    String description,       // 功能解释
    Category category,        // 分类
    String flagName,          // FengBoxClient 中的 boolean 字段名
    Setting<?>... settings    // 可选设置项
);
```

#### 方式三：自定义模块（最灵活）

完全自定义逻辑，需要继承 `Module` 类实现 `onTick/onEnable/onDisable`。

```java
Module myModule = new MyCustomModule();  // 继承 Module
FengBoxAPI.registerCustomModule(myModule);
```

### 设置项类型

| 类型 | 类名 | 用途 |
|------|------|------|
| 数值 | `NumberSetting` | 滑块调整（如 Range、Speed）|
| 布尔 | `BooleanSetting` | 开关切换（如 Only Ground）|
| 模式 | `ModeSetting` | 模式循环（如 Normal/Silent/Legit）|

### 完整示例

#### fabric.mod.json（附属 mod 配置）

```json
{
  "schemaVersion": 1,
  "id": "fengbox-addon-example",
  "version": "1.0.0",
  "name": "Feng Box Addon Example",
  "description": "Feng Box 附属 mod 示例",
  "authors": ["Your Name"],
  "environment": "client",
  "entrypoints": {
    "client": ["com.example.fengboxaddon.AddonExample"]
  },
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.11",
    "java": ">=21",
    "feng_box": ">=1.0.0"
  }
}
```

#### AddonExample.java（注册入口）

```java
package com.example.fengboxaddon;

import com.fengbox.api.FengBoxAPI;
import com.fengbox.module.Category;
import com.fengbox.module.setting.BooleanSetting;
import com.fengbox.module.setting.ModeSetting;
import com.fengbox.module.setting.NumberSetting;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

public class AddonExample implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册自定义瞄准模块
        FengBoxAPI.registerHandleModule(
            "自定义瞄准",
            "自动瞄准附近敌人并平滑转向",
            Category.COMBAT,
            "CustomAimAssist",
            "customAimAssistEnabled",
            new NumberSetting("Range", "瞄准范围", 5.0, 1.0, 8.0, 0.1),
            new NumberSetting("Speed", "转向速度", 60.0, 10.0, 180.0, 5.0),
            new ModeSetting("Mode", "瞄准模式", "Normal", List.of("Normal", "Silent", "Legit"))
        );

        // 注册自定义闪烁模块
        FengBoxAPI.registerFlagModule(
            "自定义闪烁",
            "短时间内传送一小段距离",
            Category.MOVEMENT,
            "customBlinkEnabled",
            new NumberSetting("Distance", "闪烁距离", 3.0, 1.0, 10.0, 0.5),
            new BooleanSetting("Only Ground", "仅在地面上触发", true)
        );

        // 注册完全自定义模块
        FengBoxAPI.registerCustomModule(new CustomSpeedModule());
    }
}
```

#### CustomSpeedModule.java（自定义模块）

```java
package com.example.fengboxaddon;

import com.fengbox.module.Category;
import com.fengbox.module.Module;
import com.fengbox.module.setting.NumberSetting;

public class CustomSpeedModule extends Module {
    private final NumberSetting speedSetting;

    public CustomSpeedModule() {
        super("自定义加速", "基于代码实现的自定义加速功能", Category.MOVEMENT);
        speedSetting = new NumberSetting("Speed", "移动速度倍率", 1.5, 1.0, 5.0, 0.1);
        addSetting(speedSetting);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.sendMessage(
                net.minecraft.text.Text.literal("[Feng Box] 自定义加速已开启"), true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.sendMessage(
                net.minecraft.text.Text.literal("[Feng Box] 自定义加速已关闭"), true);
        }
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.isOnGround()) {
            double speed = speedSetting.getValue();
            double dx = mc.player.getVelocity().x * speed;
            double dz = mc.player.getVelocity().z * speed;
            mc.player.setVelocity(dx, mc.player.getVelocity().y, dz);
        }
    }
}
```

### build.gradle 依赖配置

```gradle
dependencies {
    minecraft "com.mojang:minecraft:1.21.11"
    mappings "net.fabricmc:yarn:1.21.11+build.4:v2"
    modImplementation "net.fabricmc:fabric-loader:0.18.4"
    modImplementation "net.fabricmc.fabric-api:fabric-api:0.141.3+1.21.11"

    // Feng Box 主 mod（通过 flatDir 或本地 Maven 引入）
    modImplementation name: 'feng_box-1.0.0-1.21.11-v58'
}
```

### 注意事项

1. **handle 方法名**：如果使用 `registerHandleModule`，确保 `FengBoxClient` 中存在对应的 `public static void handleXxx()` 方法，否则模块开启后不会有任何效果。
2. **flag 字段名**：如果使用 `registerFlagModule`，确保 `FengBoxClient` 中存在对应的 `public static boolean xxxEnabled` 字段。
3. **自定义模块**：继承 `Module` 类时，构造函数中通过 `addSetting()` 添加的设置项会自动出现在 ClickGUI 的设置面板中。
4. **命名冲突**：不同附属 mod 注册的模块名称不要重复，否则后注册的会覆盖先注册的。
5. **分类颜色**：ClickGUI 和 HUD 会自动根据 `Category` 着色，无需额外配置。

---

*本模组仅供学习和单人/私人服务器使用，请遵守各服务器的规则。*
