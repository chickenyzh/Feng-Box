/*
 * 
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL11
 */
package com.fengbox;

import com.fengbox.AutoClickerTimingState;
import com.fengbox.InventoryUtils;
import com.fengbox.RenderUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.gl.RenderPipelines;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TridentItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potion;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potions;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
// import net.minecraft.client.render.BufferRenderer; // TODO: removed in 1.21.2+
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.Unsafe;

public class FengBoxClient
implements ClientModInitializer {
    public static FengBoxClient INSTANCE;
    public static boolean autoCritEnabled = false;
    // ClickGUI 模块开关对应的后端字段（由 ModuleManager 注册表双向绑定）
    public static boolean velocityFlightEnabled = false;
    public static boolean dummyInteractEnabled = false;
    public static final int AUTO_CRIT_NONE = 0;
    public static final int AUTO_CRIT_JUMP = 1;
    public static final int AUTO_CRIT_PACKET = 2;
    public static int autoCritMode = 2;
    public static boolean pendingCritJump = false;
    public static boolean violentMode = false;
    public static boolean freecamEnabled = false;
    public static boolean clearEffectsEnabled = false;
    public static boolean antiFallEnabled = false;
    public static final int ANTI_FALL_SPOOF_GROUND = 0;
    public static final int ANTI_FALL_PACKET = 1;
    public static final int ANTI_FALL_MLG = 2;
    public static final int ANTI_FALL_FORCE_JUMP = 3;
    public static int antiFallMode = 0;
    public static int antiFallNotMask = 16;
    public static int mlgCooldown;
    public static boolean hudEnabled;
    public static boolean nightVisionEnabled;
    public static boolean noHurtShakeEnabled;
    public static boolean playerInfoEnabled;
    public static boolean autoCrystalEnabled;
    public static boolean storageEspEnabled;
    public static boolean playerTrackerEnabled;
    public static boolean hitboxEspEnabled;
    public static boolean autoTotemEnabled;
    public static boolean autoBridgeEnabled;
    public static boolean spinEnabled;
    public static boolean vanillaUiEnabled;
    public static float spinYaw;
    public static int themeMode;
    public static final int[] T_BG;
    public static final int[] T_BORDER;
    public static final int[] T_ACCENT;
    public static final int[] T_FROST;
    public static final int[] T_ITEM_BG;
    public static final int[] T_ITEM_HOV;
    public static final int[] T_TEXT_MAIN;
    public static final int[] T_TEXT_DIM;
    public static final int[] T_SW_ON;
    public static final int[] T_SW_OFF;
    public static boolean killAuraEnabled;
    public static boolean speedEnabled;
    public static boolean speedFastMode;
    public static boolean stepEnabled;
    public static boolean sprintEnabled;
    public static boolean autoWalkEnabled;
    public static boolean fullbrightEnabled;
    public static boolean tracersEnabled;
    public static boolean nameTagsEnabled;
    public static boolean itemEspEnabled;
    public static boolean chestStealerEnabled;
    public static int chestStealerCooldown;
    public static int chestStealerDelay;
    public static boolean chestStealerSmartFilter;
    public static String chestStealerTrashCustom;
    public static boolean hotbarOrganizerEnabled;
    public static long lastOrganizeTime;
    public static boolean replenishEnabled;
    public static int replenishThreshold;
    public static int replenishDelay;
    public static int replenishInsideOf;
    public static long lastReplenishTime;


    public static final Item[] replenishTracked;
    public static boolean armorSaveEnabled;
    public static int armorSaveThreshold;
    public static int armorSaveDelay;
    public static int armorSaveCooldown;
    public static boolean autoToolEnabled;
    public static boolean autoRespawnEnabled;
    public static boolean antiAfkEnabled;
    public static boolean autoEatEnabled;
    public static boolean multiJumpEnabled;
    public static int multiJumpMax;
    public static int multiJumpLeft;
    public static float autoEatHealHealth;
    public static int autoEatHungerThreshold;
    public static boolean autoEatCombatPause;
    public static float autoEatCombatRange;
    public static float themeTransition;
    public static final Path CONFIG_PATH;
    private LivingEntity trackedPlayer = null;
    public static int hudX;
    public static int hudY;
    public static float playerInfoSlideOffset;
    public static float piSlideVel;
    public static final int FEATURE_COUNT = 50;
    public static final float[] featureAlphas;
    public static float slideOffset;
    public static float slideVel;
    public static final Text[] FEATURE_TEXTS;
    public static final int[] FEATURE_WIDTHS;
    public static boolean featureTextsInit;
    public static int lastHpInt;
    public static Text cachedHpText;
    public static float hitFlash;
    public static float barDisplayHealth;
    public static float barDisplayVel;
    public static int lastMainHandItem;
    public static Text cachedMainHandName;
    public static int lastOffHandItem;
    public static Text cachedOffHandName;
    public static boolean flightEnabled;
    public static float autoAimSpeed;
    public static float autoCrystalRange;
    public static float flightSpeed = 0.7f;
    public static float fastMoveSpeed;
    public static boolean flightStealth;
    public static boolean fastMoveStealth;
    public static boolean speedStealth;
    public static float storageEspRange;
    public static float playerTrackerRange;
    public static final int ROT_HEAD = 0;
    public static final int ROT_CAM = 1;
    public static final int ROT_NONE = 2;
    public static int autoAimRotMode;
    public static int autoCrystalRotMode;
    public static int autoBridgeRotMode;
    public static boolean invManagerEnabled;
    public static int invManagerDelay;
    public static int invManagerCleanDelay;
    public static int invManagerThrowDelay;
    public static int invManagerOffhandMode;
    public static int invManagerCleanMode;
    public static int invManagerSortMode;
    public static boolean invManagerAutoArmor;
    public static boolean invManagerAutoWeapon;
    public static boolean invManagerAutoTools;
    public static boolean invManagerAutoBow;
    public static boolean invManagerAutoOffhand;
    public static boolean invManagerSilentSprint;
    public static boolean invManagerFunctionalBlocks;
    public static int invManagerSwordSlot;
    public static int invManagerPickaxeSlot;
    public static int invManagerAxeSlot;
    public static int invManagerShovelSlot;
    public static int invManagerBlockSlot;
    public static int invManagerGappleSlot;
    public static int invManagerBowSlot;
    public static int invManagerRodSlot;
    public static int invManagerShieldSlot;
    public static int invManagerMaxBlocks;
    public static int invManagerMaxFood;
    public static int invManagerMaxProjectiles;
    public static int invManagerMaxPearls;
    public static int invManagerMaxBuckets;
    public static int invManagerMaxSlimeBalls;
    public static int invManagerMaxCrystals;
    public static int invManagerMaxArrows;
    public static long invManagerLastAction;
    public static long invManagerLastClean;
    public static long invManagerLastThrow;
    public static boolean invManagerSprintWasSprinting;
    public static boolean invManagerSprintKeyHeld;
    public static boolean customOutlineEnabled;
    public static boolean customOutlineAnimation;
    public static float customOutlineAnimationSpeed;
    public static float customOutlineMoveSpeed;
    public static float customOutlineRed;
    public static float customOutlineGreen;
    public static float customOutlineBlue;
    public static float customOutlineAlpha;
    public static boolean customOutlineFill;
    public static float customOutlineFillRed;
    public static float customOutlineFillGreen;
    public static float customOutlineFillBlue;
    public static float customOutlineFillAlpha;
    public static float customOutlineWidth;
    public static boolean customOutlineThroughWalls;
    public static float outlineVisibility;
    public static float outlineRenderX;
    public static float outlineRenderY;
    public static float outlineRenderZ;
    public static BlockPos outlineTarget;
    public static long lastOutlineTime;
    public static float killAuraRange;
    public static float killAuraCps;
    public static boolean killAuraAuto;
    public static boolean killAuraWeaponSwitch;
    public static boolean killAuraShieldBreak;
    public static boolean killAuraOnlyWeapons;
    public static int killAuraSwitchDelay;
    public static int killAuraSwitchCooldown;
    public static int killAuraPrevSlot;
    public static float killAuraWallRange;
    public static boolean killAuraCheckLineOfSight;
    public static float killAuraScanExtra;
    public static boolean killAuraIgnoreOpenInv;
    public static float killAuraClickJitter;
    public static int killAuraEspMode;
    public static boolean captureFrameEnabled;
    public static float captureAnimScale;
    public static Entity captureAnimTarget;
    public static float killAuraAttacks;
    public static boolean clickerEnabled;
    public static boolean clickerLeft;
    public static boolean clickerRight;
    public static int clickerMinCps;
    public static int clickerMaxCps;
    public static int clickerRandomMode;
    public static boolean clickerHoldOnly;
    public static boolean clickerOnlyWeapons;
    public static boolean clickerInGui;
    public static float clickerAccumMs;
    public static long clickerLastDelayMs;
    public static int clickerBurstCount;
    public static int clickerBurstLength;
    public static boolean clickerBurstActive;
    public static boolean clickerFastPhase;
    public static int clickerFastPhaseClicks;
    public static int clickerSlowPhaseClicks;
    public static int clickerSlowPhaseLength;
    public static int clickerFastPhaseLength;
    public static final SplittableRandom clickerRandom;
    public static final AutoClickerTimingState clickerTiming;
    public static float currentKillAuraScanRange;
    public static boolean reachEnabled;
    public static float reachRange;
    public static float reachEntityRange;
    public static float speedAmount;
    public static int autoEatCooldown;
    public static boolean autoRodEnabled;
    public static float autoRodRange;
    public static float autoRodTriggerRange;
    public static float autoRodSpeed;
    public static int autoRodCooldown;
    public static Entity autoRodTarget;
    public static boolean autoWebEnabled;
    public static float autoWebRange;
    public static int autoWebDelay;
    public static boolean autoWebRender;
    public static boolean autoWebGroundWeb;
    public static boolean autoWebLava;
    public static boolean autoWebLavaWaitWater;
    public static boolean autoWebDebug;
    public static float autoWebSpeed;
    public static int webPhase;
    public static int webWaitTicks;
    public static int webRetrieveTicks;
    public static int webRetrieveAttempts;
    public static int webSourceWaitCount;
    public static int webLavaReapplyCount;
    public static boolean webLavaPlaced;
    public static boolean webRetriedLavaPlace;
    public static boolean webPlaceSent;
    public static int webBreakTicks;
    public static boolean webBreakStarted;
    public static int webBreakCooldown;
    public static Entity webTarget;
    public static BlockPos webPlacePos;
    public static BlockPos webSupportPos;
    public static Direction webFace;
    public static Vec3d webAimPoint;
    public static int webPlaceType;
    public static BlockPos webFootPos;
    public static BlockPos webLavaPos;
    public static boolean webSecondWebPending;
    public static BlockPos webGroundBreakTarget;
    public static BlockPos webLastSupportPos;
    public static Direction webLastFace;
    public static Vec3d webLastAimPoint;
    public static boolean bowAimEnabled;
    public static boolean bowAimAutoShoot;
    public static float bowAimRange;
    public static float bowAimAngleRange;
    public static float bowAimAngleThreshold;
    public static float bowAimSpeed;
    public static int bowAimCooldown;
    public static Entity bowAimTarget;
    public static boolean fastUseEnabled;
    public static int fastUseInterval;
    public static int fastUseTimer;
    public static boolean fastBreakEnabled;
    public static int fastBreakInterval;
    public static int fastBreakTimer;
    public static boolean autoBlockEnabled;
    public static boolean autoBlockOnlyTarget;
    public static int autoBlockDropTicks;
    public static boolean projectileDodgeEnabled;
    public static float projectileDodgeRange;
    public static float projectileDodgeDanger;
    public static int projectileDodgeHoldTicks;
    public static int projectileDodgeTimer;
    public static int projectileDodgeDir;
    public static Entity dodgeThreatEntity;
    public static long dodgeThreatUntil;
    public static boolean totemGhostEnabled;
    public static int totemGhostDuration;
    public static boolean prevHadTotem;
    public static int totemGhostTimer;
    public static boolean itemPhysicsEnabled;
    public static float itemPhysicsSpeed;
    public static boolean rotHumanize;
    public static int elytraFireworkCooldownMs;
    public static boolean elytraFireworkSyncAura;
    public static long elytraLastFireworkTime;
    public static Vec3d freecamFrozenPos;
    public static boolean freecamPrevFlight;
    public static boolean freecamPrevFlying;
    public static Entity cameraMarker;
    public static int freecamSyncTimer;
    // 自由视角每 tick 位移系数；由 ClickGUI 的 Freecam "Speed" 设置项双向绑定写入
    public static double freecamSpeed = 0.25;
    public static float bridgeYaw;
    public static int bridgePlaceTicks;
    public static float bridgeUserPitch;
    public static BlockPos bridgePreviewPos;
    public static boolean autoBridgeTower;
    public static int autoBridgeDoNotUseBelow;
    public static Entity killAuraTarget;
    public static long lastKillAuraAttackTime;
    public static int lastAuraAttackTick;
    public static double prevGamma;
    public static long lastAfkTime;
    public static boolean isAutoEating;
    public static boolean titleBgEnabled;
    public static boolean settingBgEnabled;
    public static boolean mpBgEnabled;
    public static boolean gameBgEnabled;
    public static boolean titleBgDim;
    public static float titleBgDimAmount;
    public static boolean loadingBgEnabled;
    public static boolean customBgEnabled;
    public static float bgAlpha;
    public static BgTex crossBg;
    public static float crossAlpha;
    public static BgTex lastStaticBg;
    public static final BgTex MENU_BG;
    public static final BgTex SETTING_BG;
    public static final BgTex MP_BG;
    public static final BgTex GAME_BG;
    public static final AnimatedGif LOADING_BG;
    public static final Map<String, KeyBinding> featureKeyBindings;
    public static KeyBinding autoCritKey;
    public static KeyBinding violentKey;
    public static KeyBinding flightKey;
    public static KeyBinding freecamKey;
    public static KeyBinding guiKey;
    public static int guiKeyCode;
    public static boolean guiDown;
    public static KeyBinding clearEffectsKey;
    public static KeyBinding antiFallKey;
    public static KeyBinding nightVisionKey;
    public static KeyBinding noHurtShakeKey;
    public static KeyBinding playerInfoKey;
    public static KeyBinding autoCrystalKey;
    public static KeyBinding storageEspKey;
    public static KeyBinding playerTrackerKey;
    public static KeyBinding hitboxEspKey;
    public static KeyBinding testDummyKey;
    public static KeyBinding autoTotemKey;
    public static KeyBinding autoBridgeKey;
    public static KeyBinding spinKey;
    public static KeyBinding whiteThemeKey;
    public static KeyBinding vanillaUiKey;
    public static KeyBinding killAuraKey;
    public static KeyBinding speedKey;
    public static KeyBinding stepKey;
    public static KeyBinding sprintKey;
    public static KeyBinding autoWalkKey;
    public static KeyBinding fullbrightKey;
    public static KeyBinding tracersKey;
    public static KeyBinding nameTagsKey;
    public static KeyBinding itemEspKey;
    public static KeyBinding chestStealerKey;
    public static KeyBinding autoToolKey;
    public static KeyBinding autoRespawnKey;
    public static KeyBinding antiAfkKey;
    public static KeyBinding autoEatKey;
    public static KeyBinding multiJumpKey;
    public static KeyBinding autoAnchorKey;
    public static KeyBinding guiMoveKey;
    public static KeyBinding entityOutlineKey;
    public static KeyBinding replenishKey;
    public static KeyBinding armorSaveKey;
    public static KeyBinding boxEspKey;
    public static KeyBinding bowAimKey;
    public static KeyBinding fastUseKey;
    public static KeyBinding fastBreakKey;
    public static KeyBinding autoBlockKey;
    public static KeyBinding projectileDodgeKey;
    public static KeyBinding totemGhostKey;
    public static KeyBinding itemPhysicsKey;
    public static KeyBinding maceCritKey;
    public static KeyBinding tridentImpulseKey;
    public static int autoTotemCooldown;
    public static PlayerEntity testDummy;
    public static boolean dummyAttackWasPressed;
    public static int crystalCooldown;
    public static int crystalStep;
    public static BlockPos crystalPlacePos;
    public static int crystalRetries;
    public static boolean guiMoveEnabled;
    public static boolean guiMoveActive;
    public static boolean entityOutlineEnabled;
    public static int entityOutlineColor;
    public static float entityOutlineWidth;
    public static int entityOutlineTargets;
    public static int entityOutlineMaxDistance;
    public static boolean boxEspEnabled;
    public static int boxEspMaxDistance;
    public static int boxEspTargets;
    public static int boxEspColorMode;
    public static int boxEspColor;
    public static boolean boxEspFill;
    public static float boxEspFillAlpha;
    public static boolean boxEspOutline;
    public static boolean boxEspHealthBar;
    public static float boxEspExpand;
    public static final List<float[]> boxEspScreens;
    public static boolean capeEnabled;
    public static boolean blockerEspEnabled;
    public static boolean elytraSkinEnabled;
    public static boolean autoAnchorEnabled;
    public static float autoAnchorRange;
    public static float autoAnchorMinHealth;
    public static boolean autoAnchorSuicide;
    public static int autoAnchorRotMode;
    public static int autoAnchorPlaceDelay;
    public static int autoAnchorBreakDelay;
    public static boolean maceCritEnabled = false;
    public static float maceCritFallDistance = 5.0f;
    public static boolean tridentImpulseEnabled = false;
    public static float tridentImpulseSpeed = 3.0f;
    // ===== v34 Combat expand =====)
    public static boolean aimAssistEnabled = false;
    public static float aimAssistRange = 4.5f;
    public static float aimAssistSpeed = 45.0f;
    public static boolean velocityEnabled = false;
    public static int velocityHPercent = 90;
    public static int velocityVPercent = 100;
    public static boolean noSlowEnabled = false;
    public static int noSlowMode = 0;
    public static boolean criticalsEnabled = false;
    public static int criticalsMode = 1;
    public static boolean jesusEnabled = false;
    public static int jesusMode = 0;
    public static boolean longJumpEnabled = false;
    public static float longJumpSpeed = 1.0f;
    public static boolean highJumpEnabled = false;
    public static float highJumpHeight = 2.0f;
    public static boolean scaffoldEnabled = false;
    public static int scaffoldExtend = 1;
    public static boolean scaffoldTower = false;
    public static boolean scaffoldSwing = true;
    public static boolean elytraFlyEnabled = false;
    public static float elytraFlySpeed = 1.2f;
    public static boolean autoSwordEnabled = false;
    public static boolean autoShieldEnabled = false;
    public static boolean autoGapEnabled = false;
    public static float autoGapHealth = 8.0f;
    public static boolean killAuraAura = true;
    public static boolean killAuraThroughWalls = false;
    public static boolean reachPlusEnabled = false;
    public static float reachPlusDistance = 3.5f;
    public static boolean superKnockbackEnabled = false;
    public static float superKnockbackMult = 1.5f;
    public static boolean autoPearlEnabled = false;
    public static boolean projectileAimEnabled = false;
    public static float projectileAimLead = 1.0f;
    public static boolean autoShootEnabled = false;
    public static int autoShootCps = 10;
    public static boolean wtapEnabled = false;
    public static boolean stapEnabled = false;
    public static boolean hitSelectEnabled = false;
    public static int hitSelectDelay = 2;
    public static boolean comboModeEnabled = false;
    // ===== v34 Movement expand =====)
    public static boolean iceSpeedEnabled = false;
    public static float iceSpeedAmount = 1.0f;
    public static boolean safeWalkEnabled = false;
    public static boolean climbEnabled = false;
    public static boolean sneakEnabled = false;
    public static boolean autoSprintEnabled = false;
    public static boolean dolphinEnabled = false;
    public static boolean noFallEnabled = false;
    public static boolean phaseEnabled = false;
    public static boolean blinkEnabled = false;
    public static boolean timerEnabled = false;
    public static float timerAmount = 1.2f;
    public static boolean spiderEnabled = false;
    public static boolean airJumpEnabled = false;
    public static int airJumpMax = 2;
    public static boolean bunnyHopEnabled = false;
    public static float bunnyHopSpeed = 0.6f;
    public static boolean strafeEnabled = false;
    public static boolean wallClimbEnabled = false;
    public static boolean reverseStepEnabled = false;
    public static boolean anarchySpeedEnabled = false;
    public static float anarchySpeedAmount = 1.4f;
    public static boolean waterWalkEnabled = false;
    public static boolean lavaWalkEnabled = false;
    public static boolean tpsSpeedEnabled = false;
    public static boolean packetFlyEnabled = false;
    public static boolean flyNcpEnabled = false;
    public static boolean flyAscendEnabled = false;
    public static boolean flyDescendEnabled = false;
    public static boolean flightHover = false;
    public static float speedStrafeAmount = 0.5f;
    // ===== v34 Player expand =====)
    public static boolean autoArmorEnabled = false;
    public static int autoArmorDelay = 2;
    public static boolean autoArmorSwap = true;
    public static boolean autoPotEnabled = false;
    public static float autoPotHealth = 10.0f;
    public static boolean antiHungerEnabled = false;
    public static boolean webAuraEnabled = false;
    public static boolean xinBurrowEnabled = false;
    public static boolean antiCevEnabled = false;
    public static boolean autoSoupEnabled = false;
    public static float autoSoupHealth = 10.0f;
    public static boolean autoLogEnabled = false;
    public static float autoLogHealth = 6.0f;
    public static boolean autoFishEnabled = false;
    public static int autoFishDelay = 3;
    public static boolean autoFarmEnabled = false;
    public static float autoFarmRadius = 4.0f;
    public static boolean autoTradeEnabled = false;
    public static int autoTradeDelay = 5;
    public static boolean nukerEnabled = false;
    public static float nukerRange = 4.0f;
    public static int nukerDelay = 1;
    public static boolean nukerInstant = false;
    public static boolean searchEnabled = false;
    public static String searchBlock = "diamond_ore";
    public static int searchColor = -65536;
    public static boolean waypointsEnabled = false;
    public static boolean autoDisconnectEnabled = false;
    public static boolean noHungerEnabled = false;
    public static boolean godInvEnabled = false;
    public static boolean xcarryEnabled = false;
    public static boolean invSortEnabled = false;
    public static int invSortRows = 3;
    public static boolean autoLootEnabled = false;
    public static boolean refillEnabled = false;
    public static int refillSlot = 8;
    public static boolean autoRodPopperEnabled = false;
    public static boolean antiDeathEnabled = false;
    public static boolean deathCoordsEnabled = false;
    public static boolean autoRespawnDelayEnabled = true;
    public static boolean kickProtectEnabled = false;
    public static boolean antiWeaknessEnabled = false;
    public static boolean autoMineEnabled = false;
    public static float autoMineProgress = 0.8f;
    // ===== v34 World expand =====)
    public static boolean xrayEnabled = false;
    public static float xrayOpacity = 0.3f;
    public static int xrayMode = 0;
    public static boolean airPlaceEnabled = false;
    public static boolean towerEnabled = false;
    public static int towerMode = 0;
    public static boolean veinMinerEnabled = false;
    public static boolean excavateEnabled = false;
    public static float excavateRadius = 3.0f;
    public static boolean noWeatherEnabled = false;
    public static boolean fastLadderEnabled = false;
    public static boolean antiCactusEnabled = false;
    public static boolean antiCobwebEnabled = false;
    public static boolean autoTunnelEnabled = false;
    public static int tunnelSize = 2;
    public static boolean autoFishingRodEnabled = false;
    public static boolean kickOnGroundedEnabled = false;
    public static boolean bedrockBreakerEnabled = false;
    public static boolean fastBreakPlusEnabled = false;
    public static float fastBreakPlusRange = 4.0f;
    public static boolean chestAuraEnabled = false;
    public static float chestAuraRange = 4.0f;
    public static boolean autoStealEnabled = false;
    public static boolean autoDropEnabled = false;
    public static float autoDropRadius = 3.0f;
    public static boolean spawnerFinderEnabled = false;
    public static int spawnerFinderRange = 48;
    public static boolean structureFinderEnabled = false;
    public static boolean elytraBoostEnabled = false;
    // ===== v34 Visual expand =====)
    public static boolean espEnabled = false;
    public static int espMode = 0;
    public static int espColor = -16711936;
    public static boolean espThroughWalls = true;
    public static boolean tracersPlayers = true;
    public static boolean tracersMobs = false;
    public static boolean tracersAnimals = false;
    public static float tracersWidth = 1.5f;
    public static boolean chamsEnabled = false;
    public static int chamsMode = 0;
    public static int chamsColor = 2147483647;
    public static float chamsAlpha = 0.4f;
    public static boolean zoomEnabled = false;
    public static float zoomAmount = 6.0f;
    public static boolean trajectoriesEnabled = false;
    public static int trajectoriesColor = -1;
    public static boolean breakIndicatorEnabled = false;
    public static int breakIndicatorColor = -256;
    public static boolean arrayListEnabled = true;
    public static int arrayListMode = 0;
    public static int arrayListColor = -1;
    public static boolean watermarkEnabled = true;
    public static String watermarkText = "FengBox";
    public static boolean itemHighlightEnabled = false;
    public static boolean motionBlurEnabled = false;
    public static float motionBlurFactor = 0.5f;
    public static boolean cleanGlassEnabled = false;
    public static boolean noFovEnabled = false;
    public static boolean noHurtCamEnabled = false;
    public static boolean clearWaterEnabled = false;
    public static boolean itemInfoEnabled = false;
    public static boolean healthBarsEnabled = false;
    public static boolean arrowCountEnabled = false;
    public static boolean coordsEnabled = false;
    public static boolean directionHudEnabled = false;
    public static boolean serverListPingEnabled = false;
    public static boolean hotbarPreviewEnabled = false;
    public static boolean enchOverlayEnabled = false;
    public static boolean potionHudEnabled = false;
    public static boolean armorHudEnabled = false;
    public static boolean compassHudEnabled = false;
    // ===== v34 Misc expand =====)
    public static boolean chatSpammerEnabled = false;
    public static int chatSpammerDelay = 60;
    public static String chatSpammerMessage = "";
    public static boolean nameProtectEnabled = false;
    public static String nameProtectName = "Player";
    public static boolean serverSpoofEnabled = false;
    public static String serverSpoofAddress = "localhost";
    public static boolean autoReplyEnabled = false;
    public static String autoReplyMessage = "brb";
    public static boolean macroEnabled = false;
    public static String macroCommand = "/help";
    public static boolean panicEnabled = false;
    public static boolean middleClickEnabled = false;
    public static boolean selfAbuseEnabled = false;
    public static boolean holdSprintEnabled = false;
    public static boolean selfInvisEnabled = false;
    public static boolean noChatDelayEnabled = false;
    public static boolean autoGiveEnabled = false;
    public static boolean clickGuiOpacity = false;
    public static float clickGuiOpacityAmount = 0.9f;
    public static boolean customFontEnabled = false;
    public static boolean obviousBotsEnabled = false;
    public static int anchorCooldown;
    public static int anchorStep;
    public static BlockPos anchorPos;
    public static int anchorRetries;
    public static float autoCrystalWallsRange;
    public static boolean autoCrystalSmartDelay;
    public static boolean autoCrystalIgnoreNakeds;
    public static boolean autoCrystalSupport;
    public static boolean autoCrystalFastBreak;
    public static final String[] FEATURE_KEYS;
    public static final int[] FEATURE_ORDER;
    public static final int[] FEATURE_COLORS;
    public static final double[] ROD_RANGE_TABLE;
    public static final Set<String> DEFAULT_TRASH;
    public static final Set<String> TRASH_IDS;
    public static final Set<String> ARMOR_TYPES;
    public static final Set<String> TOOL_TYPES;
    public static final CmdFeature[] CMD_FEATURES;
    public static Identifier captureTexId;
    public static TextureManager captureTexMgr;
    public static long captureTexStamp;
    public static NativeImageBackedTexture captureTexBacked;


    public static boolean isClickGuiScreen(Screen screen) {
        return screen != null && screen.getClass().getName().equals("com.fengbox.clickgui.ClickGuiScreen");
    }

    public static void saveConfig() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("configVersion", (Number)2);
            json.addProperty("autoCrit", Boolean.valueOf(autoCritEnabled));
            json.addProperty("autoCritMode", (Number)autoCritMode);
            json.addProperty("violentMode", Boolean.valueOf(violentMode));
            json.addProperty("flight", Boolean.valueOf(flightEnabled));
            json.addProperty("freecam", Boolean.valueOf(freecamEnabled));
            json.addProperty("clearEffects", Boolean.valueOf(clearEffectsEnabled));
            json.addProperty("antiFall", Boolean.valueOf(antiFallEnabled));
            json.addProperty("antiFallMode", (Number)antiFallMode);
            json.addProperty("antiFallNotMask", (Number)antiFallNotMask);
            json.addProperty("nightVision", Boolean.valueOf(nightVisionEnabled));
            json.addProperty("noHurtShake", Boolean.valueOf(noHurtShakeEnabled));
            json.addProperty("playerInfo", Boolean.valueOf(playerInfoEnabled));
            json.addProperty("autoCrystal", Boolean.valueOf(autoCrystalEnabled));
            json.addProperty("storageEsp", Boolean.valueOf(storageEspEnabled));
            json.addProperty("playerTracker", Boolean.valueOf(playerTrackerEnabled));
            json.addProperty("hitboxEsp", Boolean.valueOf(hitboxEspEnabled));
            json.addProperty("autoTotem", Boolean.valueOf(autoTotemEnabled));
            json.addProperty("autoBridge", Boolean.valueOf(autoBridgeEnabled));
            json.addProperty("spin", Boolean.valueOf(spinEnabled));
            json.addProperty("themeMode", (Number)themeMode);
            json.addProperty("hud", Boolean.valueOf(hudEnabled));
            json.addProperty("hudX", (Number)hudX);
            json.addProperty("hudY", (Number)hudY);
            json.addProperty("autoAimSpeed", (Number)Float.valueOf(autoAimSpeed));
            json.addProperty("autoCrystalRange", (Number)Float.valueOf(autoCrystalRange));
            json.addProperty("flightSpeed", (Number)Float.valueOf(flightSpeed));
            json.addProperty("fastMoveSpeed", (Number)Float.valueOf(fastMoveSpeed));
            json.addProperty("flightStealth", Boolean.valueOf(flightStealth));
            json.addProperty("fastMoveStealth", Boolean.valueOf(fastMoveStealth));
            json.addProperty("speedStealth", Boolean.valueOf(speedStealth));
            json.addProperty("storageEspRange", (Number)Float.valueOf(storageEspRange));
            json.addProperty("playerTrackerRange", (Number)Float.valueOf(playerTrackerRange));
            json.addProperty("autoAimRotMode", (Number)autoAimRotMode);
            json.addProperty("rotHumanize", Boolean.valueOf(rotHumanize));
            json.addProperty("elytraFireworkCooldownMs", (Number)elytraFireworkCooldownMs);
            json.addProperty("elytraFireworkSyncAura", Boolean.valueOf(elytraFireworkSyncAura));
            json.addProperty("autoCrystalRotMode", (Number)autoCrystalRotMode);
            json.addProperty("autoBridgeRotMode", (Number)autoBridgeRotMode);
            json.addProperty("autoBridgeTower", Boolean.valueOf(autoBridgeTower));
            json.addProperty("autoBridgeDoNotUseBelow", (Number)autoBridgeDoNotUseBelow);
            json.addProperty("autoAnchor", Boolean.valueOf(autoAnchorEnabled));
            json.addProperty("autoAnchorRange", (Number)Float.valueOf(autoAnchorRange));
            json.addProperty("autoAnchorMinHealth", (Number)Float.valueOf(autoAnchorMinHealth));
            json.addProperty("autoAnchorSuicide", Boolean.valueOf(autoAnchorSuicide));
            json.addProperty("autoAnchorRotMode", (Number)autoAnchorRotMode);
            json.addProperty("autoAnchorPlaceDelay", (Number)autoAnchorPlaceDelay);
            json.addProperty("autoAnchorBreakDelay", (Number)autoAnchorBreakDelay);
            json.addProperty("maceCrit", Boolean.valueOf(maceCritEnabled));
            json.addProperty("maceCritFallDistance", Float.valueOf(maceCritFallDistance));
            json.addProperty("tridentImpulse", Boolean.valueOf(tridentImpulseEnabled));
            json.addProperty("tridentImpulseSpeed", Float.valueOf(tridentImpulseSpeed));
            json.addProperty("autoCrystalWallsRange", (Number)Float.valueOf(autoCrystalWallsRange));
            json.addProperty("autoCrystalSmartDelay", Boolean.valueOf(autoCrystalSmartDelay));
            json.addProperty("autoCrystalIgnoreNakeds", Boolean.valueOf(autoCrystalIgnoreNakeds));
            json.addProperty("autoCrystalSupport", Boolean.valueOf(autoCrystalSupport));
            json.addProperty("autoCrystalFastBreak", Boolean.valueOf(autoCrystalFastBreak));
            json.addProperty("killAura", Boolean.valueOf(killAuraEnabled));
            json.addProperty("autoRod", Boolean.valueOf(autoRodEnabled));
            json.addProperty("autoRodRange", (Number)Float.valueOf(autoRodRange));
            json.addProperty("autoRodTriggerRange", (Number)Float.valueOf(autoRodTriggerRange));
            json.addProperty("autoRodSpeed", (Number)Float.valueOf(autoRodSpeed));
            json.addProperty("autoWeb", Boolean.valueOf(autoWebEnabled));
            json.addProperty("autoWebRange", (Number)Float.valueOf(autoWebRange));
            json.addProperty("autoWebDelay", (Number)autoWebDelay);
            json.addProperty("autoWebRender", Boolean.valueOf(autoWebRender));
            json.addProperty("autoWebGroundWeb", Boolean.valueOf(autoWebGroundWeb));
            json.addProperty("autoWebLava", Boolean.valueOf(autoWebLava));
            json.addProperty("autoWebLavaWaitWater", Boolean.valueOf(autoWebLavaWaitWater));
            json.addProperty("autoWebDebug", Boolean.valueOf(autoWebDebug));
            json.addProperty("autoWebSpeed", (Number)Float.valueOf(autoWebSpeed));
            json.addProperty("bowAim", Boolean.valueOf(bowAimEnabled));
            json.addProperty("bowAimAutoShoot", Boolean.valueOf(bowAimAutoShoot));
            json.addProperty("bowAimRange", (Number)Float.valueOf(bowAimRange));
            json.addProperty("bowAimAngleRange", (Number)Float.valueOf(bowAimAngleRange));
            json.addProperty("bowAimAngleThreshold", (Number)Float.valueOf(bowAimAngleThreshold));
            json.addProperty("bowAimSpeed", (Number)Float.valueOf(bowAimSpeed));
            json.addProperty("fastUse", Boolean.valueOf(fastUseEnabled));
            json.addProperty("fastUseInterval", (Number)fastUseInterval);
            json.addProperty("fastBreak", Boolean.valueOf(fastBreakEnabled));
            json.addProperty("fastBreakInterval", (Number)fastBreakInterval);
            json.addProperty("autoBlock", Boolean.valueOf(autoBlockEnabled));
            json.addProperty("autoBlockOnlyTarget", Boolean.valueOf(autoBlockOnlyTarget));
            json.addProperty("autoBlockDropTicks", (Number)autoBlockDropTicks);
            json.addProperty("projectileDodge", Boolean.valueOf(projectileDodgeEnabled));
            json.addProperty("projectileDodgeRange", (Number)Float.valueOf(projectileDodgeRange));
            json.addProperty("projectileDodgeDanger", (Number)Float.valueOf(projectileDodgeDanger));
            json.addProperty("projectileDodgeHoldTicks", (Number)projectileDodgeHoldTicks);
            json.addProperty("totemGhost", Boolean.valueOf(totemGhostEnabled));
            json.addProperty("totemGhostDuration", (Number)totemGhostDuration);
            json.addProperty("itemPhysics", Boolean.valueOf(itemPhysicsEnabled));
            json.addProperty("itemPhysicsSpeed", (Number)Float.valueOf(itemPhysicsSpeed));
            json.addProperty("killAuraAuto", Boolean.valueOf(killAuraAuto));
            json.addProperty("killAuraWeaponSwitch", Boolean.valueOf(killAuraWeaponSwitch));
            json.addProperty("killAuraShieldBreak", Boolean.valueOf(killAuraShieldBreak));
            json.addProperty("killAuraOnlyWeapons", Boolean.valueOf(killAuraOnlyWeapons));
            json.addProperty("killAuraSwitchDelay", (Number)killAuraSwitchDelay);
            json.addProperty("speed", Boolean.valueOf(speedEnabled));
            json.addProperty("speedFastMode", Boolean.valueOf(speedFastMode));
            json.addProperty("step", Boolean.valueOf(stepEnabled));
            json.addProperty("sprint", Boolean.valueOf(sprintEnabled));
            json.addProperty("autoWalk", Boolean.valueOf(autoWalkEnabled));
            json.addProperty("fullbright", Boolean.valueOf(fullbrightEnabled));
            json.addProperty("tracers", Boolean.valueOf(tracersEnabled));
            json.addProperty("nameTags", Boolean.valueOf(nameTagsEnabled));
            json.addProperty("itemEsp", Boolean.valueOf(itemEspEnabled));
            json.addProperty("chestStealer", Boolean.valueOf(chestStealerEnabled));
            json.addProperty("chestStealerSmartFilter", Boolean.valueOf(chestStealerSmartFilter));
            json.addProperty("chestStealerTrashCustom", chestStealerTrashCustom);
            json.addProperty("hotbarOrganizer", Boolean.valueOf(hotbarOrganizerEnabled));
            json.addProperty("replenish", Boolean.valueOf(replenishEnabled));
            json.addProperty("replenishThreshold", (Number)replenishThreshold);
            json.addProperty("replenishDelay", (Number)replenishDelay);
            json.addProperty("replenishInsideOf", (Number)replenishInsideOf);
            json.addProperty("armorSave", Boolean.valueOf(armorSaveEnabled));
            json.addProperty("armorSaveThreshold", (Number)armorSaveThreshold);
            json.addProperty("armorSaveDelay", (Number)armorSaveDelay);
            json.addProperty("autoTool", Boolean.valueOf(autoToolEnabled));
            json.addProperty("autoRespawn", Boolean.valueOf(autoRespawnEnabled));
            json.addProperty("antiAfk", Boolean.valueOf(antiAfkEnabled));
            json.addProperty("autoEat", Boolean.valueOf(autoEatEnabled));
            json.addProperty("multiJump", Boolean.valueOf(multiJumpEnabled));
            json.addProperty("multiJumpMax", (Number)multiJumpMax);
            json.addProperty("guiMove", Boolean.valueOf(guiMoveEnabled));
            json.addProperty("entityOutline", Boolean.valueOf(entityOutlineEnabled));
            json.addProperty("entityOutlineColor", (Number)entityOutlineColor);
            json.addProperty("entityOutlineWidth", (Number)Float.valueOf(entityOutlineWidth));
            json.addProperty("entityOutlineTargets", (Number)entityOutlineTargets);
            json.addProperty("entityOutlineMaxDistance", (Number)entityOutlineMaxDistance);
            json.addProperty("boxEsp", Boolean.valueOf(boxEspEnabled));
            json.addProperty("boxEspMaxDistance", (Number)boxEspMaxDistance);
            json.addProperty("boxEspTargets", (Number)boxEspTargets);
            json.addProperty("boxEspColorMode", (Number)boxEspColorMode);
            json.addProperty("boxEspColor", (Number)boxEspColor);
            json.addProperty("boxEspFill", Boolean.valueOf(boxEspFill));
            json.addProperty("boxEspFillAlpha", (Number)Float.valueOf(boxEspFillAlpha));
            json.addProperty("boxEspOutline", Boolean.valueOf(boxEspOutline));
            json.addProperty("boxEspHealthBar", Boolean.valueOf(boxEspHealthBar));
            json.addProperty("boxEspExpand", (Number)Float.valueOf(boxEspExpand));
            json.addProperty("autoEatHealHealth", (Number)Float.valueOf(autoEatHealHealth));
            json.addProperty("autoEatHungerThreshold", (Number)autoEatHungerThreshold);
            json.addProperty("autoEatCombatPause", Boolean.valueOf(autoEatCombatPause));
            json.addProperty("autoEatCombatRange", (Number)Float.valueOf(autoEatCombatRange));
            json.addProperty("vanillaUi", Boolean.valueOf(vanillaUiEnabled));
            json.addProperty("killAuraRange", (Number)Float.valueOf(killAuraRange));
            json.addProperty("killAuraCps", (Number)Float.valueOf(killAuraCps));
            json.addProperty("killAuraWallRange", (Number)Float.valueOf(killAuraWallRange));
            json.addProperty("killAuraCheckLineOfSight", Boolean.valueOf(killAuraCheckLineOfSight));
            json.addProperty("killAuraScanExtra", (Number)Float.valueOf(killAuraScanExtra));
            json.addProperty("killAuraIgnoreOpenInv", Boolean.valueOf(killAuraIgnoreOpenInv));
            json.addProperty("killAuraClickJitter", (Number)Float.valueOf(killAuraClickJitter));
            json.addProperty("killAuraEspMode", (Number)killAuraEspMode);
            json.addProperty("captureFrameEnabled", Boolean.valueOf(captureFrameEnabled));
            json.addProperty("reach", Boolean.valueOf(reachEnabled));
            json.addProperty("reachRange", (Number)Float.valueOf(reachRange));
            json.addProperty("reachEntityRange", (Number)Float.valueOf(reachEntityRange));
            json.addProperty("speedAmount", (Number)Float.valueOf(speedAmount));
            json.addProperty("customOutline", Boolean.valueOf(customOutlineEnabled));
            json.addProperty("customOutlineAnimation", Boolean.valueOf(customOutlineAnimation));
            json.addProperty("customOutlineAnimationSpeed", (Number)Float.valueOf(customOutlineAnimationSpeed));
            json.addProperty("customOutlineMoveSpeed", (Number)Float.valueOf(customOutlineMoveSpeed));
            json.addProperty("customOutlineRed", (Number)Float.valueOf(customOutlineRed));
            json.addProperty("customOutlineGreen", (Number)Float.valueOf(customOutlineGreen));
            json.addProperty("customOutlineBlue", (Number)Float.valueOf(customOutlineBlue));
            json.addProperty("customOutlineAlpha", (Number)Float.valueOf(customOutlineAlpha));
            json.addProperty("customOutlineFill", Boolean.valueOf(customOutlineFill));
            json.addProperty("customOutlineFillRed", (Number)Float.valueOf(customOutlineFillRed));
            json.addProperty("customOutlineFillGreen", (Number)Float.valueOf(customOutlineFillGreen));
            json.addProperty("customOutlineFillBlue", (Number)Float.valueOf(customOutlineFillBlue));
            json.addProperty("customOutlineFillAlpha", (Number)Float.valueOf(customOutlineFillAlpha));
            json.addProperty("customOutlineWidth", (Number)Float.valueOf(customOutlineWidth));
            json.addProperty("customOutlineThroughWalls", Boolean.valueOf(customOutlineThroughWalls));
            json.addProperty("titleBg", Boolean.valueOf(titleBgEnabled));
            json.addProperty("settingBg", Boolean.valueOf(settingBgEnabled));
            json.addProperty("mpBg", Boolean.valueOf(mpBgEnabled));
            json.addProperty("gameBg", Boolean.valueOf(gameBgEnabled));
            json.addProperty("titleBgDim", Boolean.valueOf(titleBgDim));
            json.addProperty("titleBgDimAmount", (Number)Float.valueOf(titleBgDimAmount));
            json.addProperty("loadingBg", Boolean.valueOf(loadingBgEnabled));
            json.addProperty("customBg", Boolean.valueOf(customBgEnabled));
            json.addProperty("cape", Boolean.valueOf(capeEnabled));
            json.addProperty("elytraSkin", Boolean.valueOf(elytraSkinEnabled));
            json.addProperty("invManager", Boolean.valueOf(invManagerEnabled));
            json.addProperty("invManagerDelay", (Number)invManagerDelay);
            json.addProperty("invManagerCleanDelay", (Number)invManagerCleanDelay);
            json.addProperty("invManagerThrowDelay", (Number)invManagerThrowDelay);
            json.addProperty("invManagerOffhandMode", (Number)invManagerOffhandMode);
            json.addProperty("invManagerCleanMode", (Number)invManagerCleanMode);
            json.addProperty("invManagerSortMode", (Number)invManagerSortMode);
            json.addProperty("invManagerAutoArmor", Boolean.valueOf(invManagerAutoArmor));
            json.addProperty("invManagerAutoWeapon", Boolean.valueOf(invManagerAutoWeapon));
            json.addProperty("invManagerAutoTools", Boolean.valueOf(invManagerAutoTools));
            json.addProperty("invManagerAutoBow", Boolean.valueOf(invManagerAutoBow));
            json.addProperty("invManagerAutoOffhand", Boolean.valueOf(invManagerAutoOffhand));
            json.addProperty("invManagerSilentSprint", Boolean.valueOf(invManagerSilentSprint));
            json.addProperty("invManagerFunctionalBlocks", Boolean.valueOf(invManagerFunctionalBlocks));
            json.addProperty("invManagerSwordSlot", (Number)invManagerSwordSlot);
            json.addProperty("invManagerPickaxeSlot", (Number)invManagerPickaxeSlot);
            json.addProperty("invManagerAxeSlot", (Number)invManagerAxeSlot);
            json.addProperty("invManagerShovelSlot", (Number)invManagerShovelSlot);
            json.addProperty("invManagerBlockSlot", (Number)invManagerBlockSlot);
            json.addProperty("invManagerGappleSlot", (Number)invManagerGappleSlot);
            json.addProperty("invManagerBowSlot", (Number)invManagerBowSlot);
            json.addProperty("invManagerRodSlot", (Number)invManagerRodSlot);
            json.addProperty("invManagerShieldSlot", (Number)invManagerShieldSlot);
            json.addProperty("invManagerMaxBlocks", (Number)invManagerMaxBlocks);
            json.addProperty("invManagerMaxFood", (Number)invManagerMaxFood);
            json.addProperty("invManagerMaxProjectiles", (Number)invManagerMaxProjectiles);
            json.addProperty("invManagerMaxPearls", (Number)invManagerMaxPearls);
            json.addProperty("invManagerMaxBuckets", (Number)invManagerMaxBuckets);
            json.addProperty("invManagerMaxSlimeBalls", (Number)invManagerMaxSlimeBalls);
            json.addProperty("invManagerMaxCrystals", (Number)invManagerMaxCrystals);
            json.addProperty("invManagerMaxArrows", (Number)invManagerMaxArrows);
            json.addProperty("clicker", Boolean.valueOf(clickerEnabled));
            json.addProperty("clickerLeft", Boolean.valueOf(clickerLeft));
            json.addProperty("clickerRight", Boolean.valueOf(clickerRight));
            json.addProperty("clickerMinCps", (Number)clickerMinCps);
            json.addProperty("clickerMaxCps", (Number)clickerMaxCps);
            json.addProperty("clickerRandomMode", (Number)clickerRandomMode);
            json.addProperty("clickerHoldOnly", Boolean.valueOf(clickerHoldOnly));
            json.addProperty("clickerOnlyWeapons", Boolean.valueOf(clickerOnlyWeapons));
            json.addProperty("clickerInGui", Boolean.valueOf(clickerInGui));
            // v34 expanded fields
            json.addProperty("blockerEsp", Boolean.valueOf(blockerEspEnabled));
            json.addProperty("aimAssist", Boolean.valueOf(aimAssistEnabled));
            json.addProperty("velocity", Boolean.valueOf(velocityEnabled));
            json.addProperty("noSlow", Boolean.valueOf(noSlowEnabled));
            json.addProperty("criticals", Boolean.valueOf(criticalsEnabled));
            json.addProperty("jesus", Boolean.valueOf(jesusEnabled));
            json.addProperty("longJump", Boolean.valueOf(longJumpEnabled));
            json.addProperty("highJump", Boolean.valueOf(highJumpEnabled));
            json.addProperty("scaffold", Boolean.valueOf(scaffoldEnabled));
            json.addProperty("elytraFly", Boolean.valueOf(elytraFlyEnabled));
            json.addProperty("autoSword", Boolean.valueOf(autoSwordEnabled));
            json.addProperty("autoShield", Boolean.valueOf(autoShieldEnabled));
            json.addProperty("autoGap", Boolean.valueOf(autoGapEnabled));
            json.addProperty("reachPlus", Boolean.valueOf(reachPlusEnabled));
            json.addProperty("superKnockback", Boolean.valueOf(superKnockbackEnabled));
            json.addProperty("autoPearl", Boolean.valueOf(autoPearlEnabled));
            json.addProperty("projectileAim", Boolean.valueOf(projectileAimEnabled));
            json.addProperty("autoShoot", Boolean.valueOf(autoShootEnabled));
            json.addProperty("wtap", Boolean.valueOf(wtapEnabled));
            json.addProperty("stap", Boolean.valueOf(stapEnabled));
            json.addProperty("hitSelect", Boolean.valueOf(hitSelectEnabled));
            json.addProperty("comboMode", Boolean.valueOf(comboModeEnabled));
            json.addProperty("iceSpeed", Boolean.valueOf(iceSpeedEnabled));
            json.addProperty("safeWalk", Boolean.valueOf(safeWalkEnabled));
            json.addProperty("climb", Boolean.valueOf(climbEnabled));
            json.addProperty("sneak", Boolean.valueOf(sneakEnabled));
            json.addProperty("autoSprint", Boolean.valueOf(autoSprintEnabled));
            json.addProperty("dolphin", Boolean.valueOf(dolphinEnabled));
            json.addProperty("noFall", Boolean.valueOf(noFallEnabled));
            json.addProperty("phase", Boolean.valueOf(phaseEnabled));
            json.addProperty("blink", Boolean.valueOf(blinkEnabled));
            json.addProperty("timer", Boolean.valueOf(timerEnabled));
            json.addProperty("spider", Boolean.valueOf(spiderEnabled));
            json.addProperty("airJump", Boolean.valueOf(airJumpEnabled));
            json.addProperty("bunnyHop", Boolean.valueOf(bunnyHopEnabled));
            json.addProperty("strafe", Boolean.valueOf(strafeEnabled));
            json.addProperty("wallClimb", Boolean.valueOf(wallClimbEnabled));
            json.addProperty("reverseStep", Boolean.valueOf(reverseStepEnabled));
            json.addProperty("anarchySpeed", Boolean.valueOf(anarchySpeedEnabled));
            json.addProperty("waterWalk", Boolean.valueOf(waterWalkEnabled));
            json.addProperty("lavaWalk", Boolean.valueOf(lavaWalkEnabled));
            json.addProperty("tpsSpeed", Boolean.valueOf(tpsSpeedEnabled));
            json.addProperty("packetFly", Boolean.valueOf(packetFlyEnabled));
            json.addProperty("flyNcp", Boolean.valueOf(flyNcpEnabled));
            json.addProperty("flyAscend", Boolean.valueOf(flyAscendEnabled));
            json.addProperty("flyDescend", Boolean.valueOf(flyDescendEnabled));
            json.addProperty("autoArmor", Boolean.valueOf(autoArmorEnabled));
            json.addProperty("autoPot", Boolean.valueOf(autoPotEnabled));
            json.addProperty("autoSoup", Boolean.valueOf(autoSoupEnabled));
            json.addProperty("autoLog", Boolean.valueOf(autoLogEnabled));
            json.addProperty("autoFish", Boolean.valueOf(autoFishEnabled));
            json.addProperty("autoFarm", Boolean.valueOf(autoFarmEnabled));
            json.addProperty("autoTrade", Boolean.valueOf(autoTradeEnabled));
            json.addProperty("nuker", Boolean.valueOf(nukerEnabled));
            json.addProperty("search", Boolean.valueOf(searchEnabled));
            json.addProperty("waypoints", Boolean.valueOf(waypointsEnabled));
            json.addProperty("autoDisconnect", Boolean.valueOf(autoDisconnectEnabled));
            json.addProperty("noHunger", Boolean.valueOf(noHungerEnabled));
            json.addProperty("godInv", Boolean.valueOf(godInvEnabled));
            json.addProperty("xcarry", Boolean.valueOf(xcarryEnabled));
            json.addProperty("invSort", Boolean.valueOf(invSortEnabled));
            json.addProperty("autoLoot", Boolean.valueOf(autoLootEnabled));
            json.addProperty("refill", Boolean.valueOf(refillEnabled));
            json.addProperty("autoRodPopper", Boolean.valueOf(autoRodPopperEnabled));
            json.addProperty("antiDeath", Boolean.valueOf(antiDeathEnabled));
            json.addProperty("deathCoords", Boolean.valueOf(deathCoordsEnabled));
            json.addProperty("autoRespawnDelay", Boolean.valueOf(autoRespawnDelayEnabled));
            json.addProperty("kickProtect", Boolean.valueOf(kickProtectEnabled));
            json.addProperty("antiWeakness", Boolean.valueOf(antiWeaknessEnabled));
            json.addProperty("autoMine", Boolean.valueOf(autoMineEnabled));
            json.addProperty("xray", Boolean.valueOf(xrayEnabled));
            json.addProperty("airPlace", Boolean.valueOf(airPlaceEnabled));
            json.addProperty("tower", Boolean.valueOf(towerEnabled));
            json.addProperty("veinMiner", Boolean.valueOf(veinMinerEnabled));
            json.addProperty("excavate", Boolean.valueOf(excavateEnabled));
            json.addProperty("noWeather", Boolean.valueOf(noWeatherEnabled));
            json.addProperty("fastLadder", Boolean.valueOf(fastLadderEnabled));
            json.addProperty("antiCactus", Boolean.valueOf(antiCactusEnabled));
            json.addProperty("antiCobweb", Boolean.valueOf(antiCobwebEnabled));
            json.addProperty("autoTunnel", Boolean.valueOf(autoTunnelEnabled));
            json.addProperty("autoFishingRod", Boolean.valueOf(autoFishingRodEnabled));
            json.addProperty("kickOnGrounded", Boolean.valueOf(kickOnGroundedEnabled));
            json.addProperty("bedrockBreaker", Boolean.valueOf(bedrockBreakerEnabled));
            json.addProperty("fastBreakPlus", Boolean.valueOf(fastBreakPlusEnabled));
            json.addProperty("chestAura", Boolean.valueOf(chestAuraEnabled));
            json.addProperty("autoSteal", Boolean.valueOf(autoStealEnabled));
            json.addProperty("autoDrop", Boolean.valueOf(autoDropEnabled));
            json.addProperty("spawnerFinder", Boolean.valueOf(spawnerFinderEnabled));
            json.addProperty("structureFinder", Boolean.valueOf(structureFinderEnabled));
            json.addProperty("elytraBoost", Boolean.valueOf(elytraBoostEnabled));
            json.addProperty("esp", Boolean.valueOf(espEnabled));
            json.addProperty("chams", Boolean.valueOf(chamsEnabled));
            json.addProperty("zoom", Boolean.valueOf(zoomEnabled));
            json.addProperty("trajectories", Boolean.valueOf(trajectoriesEnabled));
            json.addProperty("breakIndicator", Boolean.valueOf(breakIndicatorEnabled));
            json.addProperty("arrayList", Boolean.valueOf(arrayListEnabled));
            json.addProperty("watermark", Boolean.valueOf(watermarkEnabled));
            json.addProperty("itemHighlight", Boolean.valueOf(itemHighlightEnabled));
            json.addProperty("motionBlur", Boolean.valueOf(motionBlurEnabled));
            json.addProperty("cleanGlass", Boolean.valueOf(cleanGlassEnabled));
            json.addProperty("noFov", Boolean.valueOf(noFovEnabled));
            json.addProperty("noHurtCam", Boolean.valueOf(noHurtCamEnabled));
            json.addProperty("clearWater", Boolean.valueOf(clearWaterEnabled));
            json.addProperty("itemInfo", Boolean.valueOf(itemInfoEnabled));
            json.addProperty("healthBars", Boolean.valueOf(healthBarsEnabled));
            json.addProperty("arrowCount", Boolean.valueOf(arrowCountEnabled));
            json.addProperty("coords", Boolean.valueOf(coordsEnabled));
            json.addProperty("directionHud", Boolean.valueOf(directionHudEnabled));
            json.addProperty("serverListPing", Boolean.valueOf(serverListPingEnabled));
            json.addProperty("hotbarPreview", Boolean.valueOf(hotbarPreviewEnabled));
            json.addProperty("enchOverlay", Boolean.valueOf(enchOverlayEnabled));
            json.addProperty("potionHud", Boolean.valueOf(potionHudEnabled));
            json.addProperty("armorHud", Boolean.valueOf(armorHudEnabled));
            json.addProperty("compassHud", Boolean.valueOf(compassHudEnabled));
            json.addProperty("chatSpammer", Boolean.valueOf(chatSpammerEnabled));
            json.addProperty("nameProtect", Boolean.valueOf(nameProtectEnabled));
            json.addProperty("serverSpoof", Boolean.valueOf(serverSpoofEnabled));
            json.addProperty("autoReply", Boolean.valueOf(autoReplyEnabled));
            json.addProperty("macro", Boolean.valueOf(macroEnabled));
            json.addProperty("panic", Boolean.valueOf(panicEnabled));
            json.addProperty("middleClick", Boolean.valueOf(middleClickEnabled));
            json.addProperty("selfAbuse", Boolean.valueOf(selfAbuseEnabled));
            json.addProperty("holdSprint", Boolean.valueOf(holdSprintEnabled));
            json.addProperty("selfInvis", Boolean.valueOf(selfInvisEnabled));
            json.addProperty("noChatDelay", Boolean.valueOf(noChatDelayEnabled));
            json.addProperty("autoGive", Boolean.valueOf(autoGiveEnabled));
            json.addProperty("customFont", Boolean.valueOf(customFontEnabled));
            json.addProperty("obviousBots", Boolean.valueOf(obviousBotsEnabled));
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            Files.writeString(CONFIG_PATH, (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)json), new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH, new LinkOption[0])) {
                return;
            }
            String content = Files.readString(CONFIG_PATH);
            JsonObject json = JsonParser.parseString((String)content).getAsJsonObject();
            autoCritEnabled = FengBoxClient.getBool(json, "autoCrit", false);
            autoCritMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoCritMode", 2), (int)0, (int)2);
            violentMode = FengBoxClient.getBool(json, "violentMode", false);
            flightEnabled = FengBoxClient.getBool(json, "flight", false);
            freecamEnabled = FengBoxClient.getBool(json, "freecam", false);
            clearEffectsEnabled = FengBoxClient.getBool(json, "clearEffects", false);
            antiFallEnabled = FengBoxClient.getBool(json, "antiFall", false);
            antiFallMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "antiFallMode", 0), (int)0, (int)3);
            antiFallNotMask = FengBoxClient.getInt(json, "antiFallNotMask", 16) & 0x1F;
            nightVisionEnabled = FengBoxClient.getBool(json, "nightVision", false);
            noHurtShakeEnabled = FengBoxClient.getBool(json, "noHurtShake", false);
            playerInfoEnabled = FengBoxClient.getBool(json, "playerInfo", false);
            autoCrystalEnabled = FengBoxClient.getBool(json, "autoCrystal", false);
            storageEspEnabled = FengBoxClient.getBool(json, "storageEsp", false);
            playerTrackerEnabled = FengBoxClient.getBool(json, "playerTracker", false);
            hitboxEspEnabled = FengBoxClient.getBool(json, "hitboxEsp", false);
            autoTotemEnabled = FengBoxClient.getBool(json, "autoTotem", false);
            autoBridgeEnabled = FengBoxClient.getBool(json, "autoBridge", false);
            spinEnabled = FengBoxClient.getBool(json, "spin", false);
            themeMode = FengBoxClient.getInt(json, "themeMode", 0);
            hudEnabled = FengBoxClient.getBool(json, "hud", true);
            hudX = FengBoxClient.getInt(json, "hudX", -1);
            hudY = FengBoxClient.getInt(json, "hudY", 4);
            autoAimSpeed = FengBoxClient.getFloat(json, "autoAimSpeed", 30.0f);
            autoCrystalRange = FengBoxClient.getFloat(json, "autoCrystalRange", 6.0f);
            flightSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "flightSpeed", 0.7f), (float)0.2f, (float)1.0f);
            fastMoveSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "fastMoveSpeed", 0.42f), (float)0.2f, (float)0.6f);
            flightStealth = FengBoxClient.getBool(json, "flightStealth", false);
            fastMoveStealth = FengBoxClient.getBool(json, "fastMoveStealth", false);
            speedStealth = FengBoxClient.getBool(json, "speedStealth", false);
            storageEspRange = FengBoxClient.getFloat(json, "storageEspRange", 32.0f);
            playerTrackerRange = FengBoxClient.getFloat(json, "playerTrackerRange", 64.0f);
            autoAimRotMode = FengBoxClient.getInt(json, "autoAimRotMode", 1);
            autoCrystalRotMode = FengBoxClient.getInt(json, "autoCrystalRotMode", 0);
            autoBridgeRotMode = FengBoxClient.getInt(json, "autoBridgeRotMode", 0);
            autoBridgeTower = FengBoxClient.getBool(json, "autoBridgeTower", false);
            autoBridgeDoNotUseBelow = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoBridgeDoNotUseBelow", 0), (int)0, (int)64);
            rotHumanize = FengBoxClient.getBool(json, "rotHumanize", true);
            elytraFireworkCooldownMs = MathHelper.clamp((int)FengBoxClient.getInt(json, "elytraFireworkCooldownMs", 750), (int)100, (int)5000);
            elytraFireworkSyncAura = FengBoxClient.getBool(json, "elytraFireworkSyncAura", false);
            autoAnchorEnabled = FengBoxClient.getBool(json, "autoAnchor", false);
            autoAnchorRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoAnchorRange", 5.0f), (float)1.0f, (float)12.0f);
            autoAnchorMinHealth = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoAnchorMinHealth", 8.0f), (float)1.0f, (float)20.0f);
            autoAnchorSuicide = FengBoxClient.getBool(json, "autoAnchorSuicide", false);
            autoAnchorRotMode = FengBoxClient.getInt(json, "autoAnchorRotMode", 0);
            autoAnchorPlaceDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoAnchorPlaceDelay", 4), (int)1, (int)20);
            autoAnchorBreakDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoAnchorBreakDelay", 3), (int)1, (int)20);
            maceCritEnabled = FengBoxClient.getBool(json, "maceCrit", false);
            maceCritFallDistance = MathHelper.clamp(FengBoxClient.getFloat(json, "maceCritFallDistance", 5.0f), 1.5f, 20.0f);
            tridentImpulseEnabled = FengBoxClient.getBool(json, "tridentImpulse", false);
            tridentImpulseSpeed = MathHelper.clamp(FengBoxClient.getFloat(json, "tridentImpulseSpeed", 3.0f), 0.0f, 10.0f);
            autoCrystalWallsRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoCrystalWallsRange", 6.0f), (float)1.0f, (float)16.0f);
            autoCrystalSmartDelay = FengBoxClient.getBool(json, "autoCrystalSmartDelay", true);
            autoCrystalIgnoreNakeds = FengBoxClient.getBool(json, "autoCrystalIgnoreNakeds", false);
            autoCrystalSupport = FengBoxClient.getBool(json, "autoCrystalSupport", true);
            autoCrystalFastBreak = FengBoxClient.getBool(json, "autoCrystalFastBreak", true);
            killAuraEnabled = FengBoxClient.getBool(json, "killAura", false);
            autoRodEnabled = FengBoxClient.getBool(json, "autoRod", false);
            autoRodRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoRodRange", 20.0f), (float)5.0f, (float)40.0f);
            autoRodTriggerRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoRodTriggerRange", 4.5f), (float)1.0f, (float)16.0f);
            autoRodSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoRodSpeed", 35.0f), (float)5.0f, (float)90.0f);
            if (autoRodTriggerRange > autoRodRange) {
                autoRodTriggerRange = autoRodRange;
            }
            autoWebEnabled = FengBoxClient.getBool(json, "autoWeb", false);
            autoWebRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoWebRange", 4.0f), (float)3.0f, (float)5.0f);
            autoWebDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoWebDelay", 4), (int)1, (int)20);
            autoWebRender = FengBoxClient.getBool(json, "autoWebRender", true);
            autoWebGroundWeb = FengBoxClient.getBool(json, "autoWebGroundWeb", false);
            autoWebLava = FengBoxClient.getBool(json, "autoWebLava", true);
            autoWebLavaWaitWater = FengBoxClient.getBool(json, "autoWebLavaWaitWater", true);
            autoWebDebug = FengBoxClient.getBool(json, "autoWebDebug", false);
            autoWebSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoWebSpeed", 30.0f), (float)5.0f, (float)90.0f);
            bowAimEnabled = FengBoxClient.getBool(json, "bowAim", false);
            bowAimAutoShoot = FengBoxClient.getBool(json, "bowAimAutoShoot", true);
            bowAimRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "bowAimRange", 50.0f), (float)5.0f, (float)150.0f);
            bowAimAngleRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "bowAimAngleRange", 30.0f), (float)5.0f, (float)90.0f);
            bowAimAngleThreshold = MathHelper.clamp((float)FengBoxClient.getFloat(json, "bowAimAngleThreshold", 6.0f), (float)1.0f, (float)20.0f);
            bowAimSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "bowAimSpeed", 30.0f), (float)5.0f, (float)90.0f);
            fastUseEnabled = FengBoxClient.getBool(json, "fastUse", false);
            fastUseInterval = MathHelper.clamp((int)FengBoxClient.getInt(json, "fastUseInterval", 3), (int)1, (int)10);
            fastBreakEnabled = FengBoxClient.getBool(json, "fastBreak", false);
            fastBreakInterval = MathHelper.clamp((int)FengBoxClient.getInt(json, "fastBreakInterval", 2), (int)1, (int)8);
            autoBlockEnabled = FengBoxClient.getBool(json, "autoBlock", false);
            autoBlockOnlyTarget = FengBoxClient.getBool(json, "autoBlockOnlyTarget", true);
            autoBlockDropTicks = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoBlockDropTicks", 6), (int)1, (int)20);
            projectileDodgeEnabled = FengBoxClient.getBool(json, "projectileDodge", false);
            projectileDodgeRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "projectileDodgeRange", 12.0f), (float)5.0f, (float)30.0f);
            projectileDodgeDanger = MathHelper.clamp((float)FengBoxClient.getFloat(json, "projectileDodgeDanger", 2.0f), (float)1.0f, (float)4.0f);
            projectileDodgeHoldTicks = MathHelper.clamp((int)FengBoxClient.getInt(json, "projectileDodgeHoldTicks", 4), (int)2, (int)10);
            totemGhostEnabled = FengBoxClient.getBool(json, "totemGhost", false);
            totemGhostDuration = MathHelper.clamp((int)FengBoxClient.getInt(json, "totemGhostDuration", 60), (int)20, (int)120);
            itemPhysicsEnabled = FengBoxClient.getBool(json, "itemPhysics", false);
            itemPhysicsSpeed = MathHelper.clamp((float)FengBoxClient.getFloat(json, "itemPhysicsSpeed", 1.0f), (float)0.2f, (float)3.0f);
            killAuraAuto = FengBoxClient.getBool(json, "killAuraAuto", true);
            killAuraWeaponSwitch = FengBoxClient.getBool(json, "killAuraWeaponSwitch", true);
            killAuraShieldBreak = FengBoxClient.getBool(json, "killAuraShieldBreak", true);
            killAuraOnlyWeapons = FengBoxClient.getBool(json, "killAuraOnlyWeapons", false);
            killAuraSwitchDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "killAuraSwitchDelay", 4), (int)0, (int)20);
            speedEnabled = FengBoxClient.getBool(json, "speed", false);
            speedFastMode = FengBoxClient.getBool(json, "speedFastMode", false);
            stepEnabled = FengBoxClient.getBool(json, "step", false);
            sprintEnabled = FengBoxClient.getBool(json, "sprint", false);
            autoWalkEnabled = FengBoxClient.getBool(json, "autoWalk", false);
            fullbrightEnabled = FengBoxClient.getBool(json, "fullbright", false);
            tracersEnabled = FengBoxClient.getBool(json, "tracers", false);
            nameTagsEnabled = FengBoxClient.getBool(json, "nameTags", false);
            itemEspEnabled = FengBoxClient.getBool(json, "itemEsp", false);
            chestStealerEnabled = FengBoxClient.getBool(json, "chestStealer", false);
            chestStealerSmartFilter = FengBoxClient.getBool(json, "chestStealerSmartFilter", true);
            chestStealerTrashCustom = FengBoxClient.getString(json, "chestStealerTrashCustom", "");
            if (!chestStealerTrashCustom.isEmpty()) {
                TRASH_IDS.clear();
                for (String s : chestStealerTrashCustom.split(";")) {
                    String t = s.trim();
                    if (t.isEmpty()) continue;
                    TRASH_IDS.add(t);
                }
            }
            hotbarOrganizerEnabled = FengBoxClient.getBool(json, "hotbarOrganizer", false);
            replenishEnabled = FengBoxClient.getBool(json, "replenish", false);
            replenishThreshold = MathHelper.clamp((int)FengBoxClient.getInt(json, "replenishThreshold", 5), (int)0, (int)63);
            replenishDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "replenishDelay", 40), (int)0, (int)1000);
            replenishInsideOf = MathHelper.clamp((int)FengBoxClient.getInt(json, "replenishInsideOf", 0), (int)0, (int)1);
            armorSaveEnabled = FengBoxClient.getBool(json, "armorSave", false);
            armorSaveThreshold = MathHelper.clamp((int)FengBoxClient.getInt(json, "armorSaveThreshold", 30), (int)1, (int)100);
            armorSaveDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "armorSaveDelay", 40), (int)1, (int)200);
            autoToolEnabled = FengBoxClient.getBool(json, "autoTool", false);
            autoRespawnEnabled = FengBoxClient.getBool(json, "autoRespawn", false);
            antiAfkEnabled = FengBoxClient.getBool(json, "antiAfk", false);
            autoEatEnabled = FengBoxClient.getBool(json, "autoEat", false);
            multiJumpEnabled = FengBoxClient.getBool(json, "multiJump", false);
            multiJumpMax = FengBoxClient.getInt(json, "multiJumpMax", 3);
            if (multiJumpMax < 1) {
                multiJumpMax = 1;
            }
            if (multiJumpMax > 10) {
                multiJumpMax = 10;
            }
            guiMoveEnabled = FengBoxClient.getBool(json, "guiMove", false);
            entityOutlineEnabled = FengBoxClient.getBool(json, "entityOutline", false);
            entityOutlineColor = FengBoxClient.getInt(json, "entityOutlineColor", -16711936);
            entityOutlineWidth = MathHelper.clamp((float)FengBoxClient.getFloat(json, "entityOutlineWidth", 1.0f), (float)0.0f, (float)4.0f);
            entityOutlineTargets = MathHelper.clamp((int)FengBoxClient.getInt(json, "entityOutlineTargets", 0), (int)0, (int)3);
            entityOutlineMaxDistance = MathHelper.clamp((int)FengBoxClient.getInt(json, "entityOutlineMaxDistance", 128), (int)1, (int)512);
            boxEspEnabled = FengBoxClient.getBool(json, "boxEsp", false);
            boxEspMaxDistance = MathHelper.clamp((int)FengBoxClient.getInt(json, "boxEspMaxDistance", 128), (int)1, (int)512);
            boxEspTargets = MathHelper.clamp((int)FengBoxClient.getInt(json, "boxEspTargets", 3), (int)0, (int)3);
            boxEspColorMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "boxEspColorMode", 1), (int)0, (int)1);
            boxEspColor = FengBoxClient.getInt(json, "boxEspColor", -16711936);
            boxEspFill = FengBoxClient.getBool(json, "boxEspFill", true);
            boxEspFillAlpha = MathHelper.clamp((float)FengBoxClient.getFloat(json, "boxEspFillAlpha", 0.2f), (float)0.05f, (float)0.6f);
            boxEspOutline = FengBoxClient.getBool(json, "boxEspOutline", true);
            boxEspHealthBar = FengBoxClient.getBool(json, "boxEspHealthBar", true);
            boxEspExpand = MathHelper.clamp((float)FengBoxClient.getFloat(json, "boxEspExpand", 0.05f), (float)0.0f, (float)0.5f);
            autoEatHealHealth = FengBoxClient.getFloat(json, "autoEatHealHealth", 10.0f);
            if (autoEatHealHealth < 1.0f) {
                autoEatHealHealth = 1.0f;
            }
            if (autoEatHealHealth > 20.0f) {
                autoEatHealHealth = 20.0f;
            }
            autoEatHungerThreshold = MathHelper.clamp((int)FengBoxClient.getInt(json, "autoEatHungerThreshold", 10), (int)1, (int)20);
            autoEatCombatPause = FengBoxClient.getBool(json, "autoEatCombatPause", true);
            autoEatCombatRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "autoEatCombatRange", 8.0f), (float)2.0f, (float)32.0f);
            vanillaUiEnabled = FengBoxClient.getBool(json, "vanillaUi", false);
            killAuraRange = FengBoxClient.getFloat(json, "killAuraRange", 4.5f);
            killAuraCps = FengBoxClient.getFloat(json, "killAuraCps", 12.0f);
            killAuraWallRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "killAuraWallRange", 3.0f), (float)1.0f, (float)8.0f);
            killAuraCheckLineOfSight = FengBoxClient.getBool(json, "killAuraCheckLineOfSight", true);
            killAuraScanExtra = MathHelper.clamp((float)FengBoxClient.getFloat(json, "killAuraScanExtra", 2.0f), (float)0.0f, (float)5.0f);
            killAuraIgnoreOpenInv = FengBoxClient.getBool(json, "killAuraIgnoreOpenInv", false);
            killAuraClickJitter = MathHelper.clamp((float)FengBoxClient.getFloat(json, "killAuraClickJitter", 0.15f), (float)0.0f, (float)0.5f);
            killAuraEspMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "killAuraEspMode", 2), (int)0, (int)3);
            captureFrameEnabled = FengBoxClient.getBool(json, "captureFrameEnabled", true);
            if (killAuraWallRange > killAuraRange) {
                killAuraWallRange = killAuraRange;
            }
            reachEnabled = FengBoxClient.getBool(json, "reach", false);
            reachRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "reachRange", 5.0f), (float)1.0f, (float)512.0f);
            reachEntityRange = MathHelper.clamp((float)FengBoxClient.getFloat(json, "reachEntityRange", 4.5f), (float)1.0f, (float)512.0f);
            speedAmount = FengBoxClient.getFloat(json, "speedAmount", 0.28f);
            customOutlineEnabled = FengBoxClient.getBool(json, "customOutline", false);
            customOutlineAnimation = FengBoxClient.getBool(json, "customOutlineAnimation", true);
            customOutlineAnimationSpeed = FengBoxClient.getFloat(json, "customOutlineAnimationSpeed", 6.0f);
            customOutlineMoveSpeed = FengBoxClient.getFloat(json, "customOutlineMoveSpeed", 8.0f);
            customOutlineRed = FengBoxClient.getFloat(json, "customOutlineRed", 1.0f);
            customOutlineGreen = FengBoxClient.getFloat(json, "customOutlineGreen", 0.25f);
            customOutlineBlue = FengBoxClient.getFloat(json, "customOutlineBlue", 0.25f);
            customOutlineAlpha = FengBoxClient.getFloat(json, "customOutlineAlpha", 0.9f);
            customOutlineFill = FengBoxClient.getBool(json, "customOutlineFill", true);
            customOutlineFillRed = FengBoxClient.getFloat(json, "customOutlineFillRed", 1.0f);
            customOutlineFillGreen = FengBoxClient.getFloat(json, "customOutlineFillGreen", 0.25f);
            customOutlineFillBlue = FengBoxClient.getFloat(json, "customOutlineFillBlue", 0.25f);
            customOutlineFillAlpha = FengBoxClient.getFloat(json, "customOutlineFillAlpha", 0.22f);
            customOutlineWidth = FengBoxClient.getFloat(json, "customOutlineWidth", 2.0f);
            customOutlineThroughWalls = FengBoxClient.getBool(json, "customOutlineThroughWalls", true);
            titleBgEnabled = FengBoxClient.getBool(json, "titleBg", true);
            settingBgEnabled = FengBoxClient.getBool(json, "settingBg", true);
            mpBgEnabled = FengBoxClient.getBool(json, "mpBg", true);
            gameBgEnabled = FengBoxClient.getBool(json, "gameBg", true);
            titleBgDim = FengBoxClient.getBool(json, "titleBgDim", true);
            titleBgDimAmount = FengBoxClient.getFloat(json, "titleBgDimAmount", 0.35f);
            loadingBgEnabled = FengBoxClient.getBool(json, "loadingBg", true);
            customBgEnabled = FengBoxClient.getBool(json, "customBg", true);
            capeEnabled = FengBoxClient.getBool(json, "cape", true);
            elytraSkinEnabled = FengBoxClient.getBool(json, "elytraSkin", true);
            invManagerEnabled = FengBoxClient.getBool(json, "invManager", false);
            invManagerDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerDelay", 80), (int)10, (int)2000);
            invManagerCleanDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerCleanDelay", 600), (int)50, (int)10000);
            invManagerThrowDelay = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerThrowDelay", 150), (int)10, (int)1000);
            invManagerOffhandMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerOffhandMode", 2), (int)0, (int)4);
            invManagerCleanMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerCleanMode", 1), (int)0, (int)2);
            invManagerSortMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerSortMode", 2), (int)0, (int)2);
            invManagerAutoArmor = FengBoxClient.getBool(json, "invManagerAutoArmor", true);
            invManagerAutoWeapon = FengBoxClient.getBool(json, "invManagerAutoWeapon", true);
            invManagerAutoTools = FengBoxClient.getBool(json, "invManagerAutoTools", true);
            invManagerAutoBow = FengBoxClient.getBool(json, "invManagerAutoBow", true);
            invManagerAutoOffhand = FengBoxClient.getBool(json, "invManagerAutoOffhand", true);
            invManagerSilentSprint = FengBoxClient.getBool(json, "invManagerSilentSprint", true);
            invManagerFunctionalBlocks = FengBoxClient.getBool(json, "invManagerFunctionalBlocks", true);
            invManagerSwordSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerSwordSlot", 0), (int)0, (int)8);
            invManagerPickaxeSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerPickaxeSlot", 1), (int)0, (int)8);
            invManagerAxeSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerAxeSlot", 2), (int)0, (int)8);
            invManagerShovelSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerShovelSlot", 3), (int)0, (int)8);
            invManagerBlockSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerBlockSlot", 4), (int)0, (int)8);
            invManagerGappleSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerGappleSlot", 5), (int)0, (int)8);
            invManagerBowSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerBowSlot", 6), (int)0, (int)8);
            invManagerRodSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerRodSlot", 7), (int)0, (int)8);
            invManagerShieldSlot = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerShieldSlot", 8), (int)0, (int)8);
            invManagerMaxBlocks = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxBlocks", 6), (int)1, (int)36);
            invManagerMaxFood = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxFood", 3), (int)1, (int)36);
            invManagerMaxProjectiles = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxProjectiles", 2), (int)1, (int)36);
            invManagerMaxPearls = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxPearls", 1), (int)1, (int)36);
            invManagerMaxBuckets = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxBuckets", 1), (int)1, (int)36);
            invManagerMaxSlimeBalls = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxSlimeBalls", 1), (int)1, (int)36);
            invManagerMaxCrystals = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxCrystals", 1), (int)1, (int)36);
            invManagerMaxArrows = MathHelper.clamp((int)FengBoxClient.getInt(json, "invManagerMaxArrows", 256), (int)1, (int)1024);
            clickerEnabled = FengBoxClient.getBool(json, "clicker", false);
            clickerLeft = FengBoxClient.getBool(json, "clickerLeft", true);
            clickerRight = FengBoxClient.getBool(json, "clickerRight", false);
            clickerMinCps = MathHelper.clamp((int)FengBoxClient.getInt(json, "clickerMinCps", 8), (int)1, (int)30);
            clickerMaxCps = MathHelper.clamp((int)FengBoxClient.getInt(json, "clickerMaxCps", 12), (int)1, (int)30);
            clickerRandomMode = MathHelper.clamp((int)FengBoxClient.getInt(json, "clickerRandomMode", 2), (int)0, (int)2);
            clickerHoldOnly = FengBoxClient.getBool(json, "clickerHoldOnly", false);
            clickerOnlyWeapons = FengBoxClient.getBool(json, "clickerOnlyWeapons", false);
            clickerInGui = FengBoxClient.getBool(json, "clickerInGui", false);
            if (FengBoxClient.getInt(json, "configVersion", 0) < 2) {
                titleBgEnabled = true;
                settingBgEnabled = true;
                mpBgEnabled = true;
                gameBgEnabled = true;
                loadingBgEnabled = true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static boolean getBool(JsonObject json, String key, boolean def) {
        return json.has(key) ? json.get(key).getAsBoolean() : def;
    }

    public static int getInt(JsonObject json, String key, int def) {
        return json.has(key) ? json.get(key).getAsInt() : def;
    }

    public static float getFloat(JsonObject json, String key, float def) {
        return json.has(key) ? json.get(key).getAsFloat() : def;
    }

    public static String getString(JsonObject json, String key, String def) {
        return json.has(key) ? json.get(key).getAsString() : def;
    }

    public void onInitializeClient() {
        INSTANCE = this;
        autoCritKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_crit", InputUtil.Type.KEYSYM, 77, KeyBinding.Category.GAMEPLAY));
        violentKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_violent", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        flightKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_flight", InputUtil.Type.KEYSYM, 293, KeyBinding.Category.GAMEPLAY));
        freecamKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_freecam", InputUtil.Type.KEYSYM, 66, KeyBinding.Category.GAMEPLAY));
        guiKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.open_gui", InputUtil.Type.KEYSYM, 75, KeyBinding.Category.GAMEPLAY));
        clearEffectsKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_clear_effects", InputUtil.Type.KEYSYM, 78, KeyBinding.Category.GAMEPLAY));
        antiFallKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_anti_fall", InputUtil.Type.KEYSYM, 72, KeyBinding.Category.GAMEPLAY));
        nightVisionKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_night_vision", InputUtil.Type.KEYSYM, 86, KeyBinding.Category.GAMEPLAY));
        noHurtShakeKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_no_hurt_shake", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        playerInfoKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_player_info", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoCrystalKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_crystal", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        storageEspKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_storage_esp", InputUtil.Type.KEYSYM, 85, KeyBinding.Category.GAMEPLAY));
        playerTrackerKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_player_tracker", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        hitboxEspKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_hitbox_esp", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        testDummyKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_test_dummy", InputUtil.Type.KEYSYM, 89, KeyBinding.Category.GAMEPLAY));
        autoTotemKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_totem", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoBridgeKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_bridge", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        spinKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_spin", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        whiteThemeKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_white_theme", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        vanillaUiKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_vanilla_ui", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        killAuraKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_kill_aura", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        speedKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_speed", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        stepKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_step", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        sprintKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_sprint", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoWalkKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_walk", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        fullbrightKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_fullbright", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        tracersKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_tracers", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        nameTagsKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_name_tags", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        itemEspKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_item_esp", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        chestStealerKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_chest_stealer", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoToolKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_tool", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoRespawnKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_respawn", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        antiAfkKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_anti_afk", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoEatKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_eat", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        multiJumpKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_multi_jump", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        autoAnchorKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_anchor", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        guiMoveKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_gui_move", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        entityOutlineKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_entity_outline", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        replenishKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_replenish", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        armorSaveKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_armor_save", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        boxEspKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_box_esp", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        bowAimKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_bow_aim", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        fastUseKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_fast_use", InputUtil.Type.KEYSYM, 71, KeyBinding.Category.GAMEPLAY));
        fastBreakKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_fast_break", InputUtil.Type.KEYSYM, 67, KeyBinding.Category.GAMEPLAY));
        autoBlockKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_auto_block", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        projectileDodgeKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_projectile_dodge", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        totemGhostKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_totem_ghost", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        itemPhysicsKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_item_physics", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        maceCritKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_mace_crit", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        tridentImpulseKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.feng_box.toggle_trident_impulse", InputUtil.Type.KEYSYM, -1, KeyBinding.Category.GAMEPLAY));
        featureKeyBindings.put("auto_crit", autoCritKey);
        featureKeyBindings.put("violent", violentKey);
        featureKeyBindings.put("flight", flightKey);
        featureKeyBindings.put("freecam", freecamKey);
        featureKeyBindings.put("clear_effects", clearEffectsKey);
        featureKeyBindings.put("anti_fall", antiFallKey);
        featureKeyBindings.put("night_vision", nightVisionKey);
        featureKeyBindings.put("no_hurt_shake", noHurtShakeKey);
        featureKeyBindings.put("player_info", playerInfoKey);
        featureKeyBindings.put("auto_crystal", autoCrystalKey);
        featureKeyBindings.put("storage_esp", storageEspKey);
        featureKeyBindings.put("player_tracker", playerTrackerKey);
        featureKeyBindings.put("hitbox_esp", hitboxEspKey);
        featureKeyBindings.put("test_dummy", testDummyKey);
        featureKeyBindings.put("auto_totem", autoTotemKey);
        featureKeyBindings.put("auto_bridge", autoBridgeKey);
        featureKeyBindings.put("spin", spinKey);
        featureKeyBindings.put("white_theme", whiteThemeKey);
        featureKeyBindings.put("vanilla_ui", vanillaUiKey);
        featureKeyBindings.put("kill_aura", killAuraKey);
        featureKeyBindings.put("speed", speedKey);
        featureKeyBindings.put("step", stepKey);
        featureKeyBindings.put("sprint", sprintKey);
        featureKeyBindings.put("auto_walk", autoWalkKey);
        featureKeyBindings.put("fullbright", fullbrightKey);
        featureKeyBindings.put("tracers", tracersKey);
        featureKeyBindings.put("name_tags", nameTagsKey);
        featureKeyBindings.put("item_esp", itemEspKey);
        featureKeyBindings.put("chest_stealer", chestStealerKey);
        featureKeyBindings.put("auto_tool", autoToolKey);
        featureKeyBindings.put("auto_respawn", autoRespawnKey);
        featureKeyBindings.put("anti_afk", antiAfkKey);
        featureKeyBindings.put("auto_eat", autoEatKey);
        featureKeyBindings.put("multi_jump", multiJumpKey);
        featureKeyBindings.put("auto_anchor", autoAnchorKey);
        featureKeyBindings.put("gui_move", guiMoveKey);
        featureKeyBindings.put("entity_outline", entityOutlineKey);
        featureKeyBindings.put("replenish", replenishKey);
        featureKeyBindings.put("armor_save", armorSaveKey);
        featureKeyBindings.put("box_esp", boxEspKey);
        featureKeyBindings.put("bow_aim", bowAimKey);
        featureKeyBindings.put("fast_use", fastUseKey);
        featureKeyBindings.put("fast_break", fastBreakKey);
        featureKeyBindings.put("auto_block", autoBlockKey);
        featureKeyBindings.put("projectile_dodge", projectileDodgeKey);
        featureKeyBindings.put("totem_ghost", totemGhostKey);
        featureKeyBindings.put("item_physics", itemPhysicsKey);
        featureKeyBindings.put("mace_crit", maceCritKey);
        featureKeyBindings.put("trident_impulse", tridentImpulseKey);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        FengBoxClient.loadConfig();
        NetworkHelper.registerDisconnectCallback();
        com.fengbox.module.ModuleManager.init();
        WorldRenderEvents.END_MAIN.register(context -> {
            MatrixStack matrices = context.matrices();
            if (matrices == null) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null || mc.player == null) return;
            float tickDelta = mc.getRenderTickCounter().getTickProgress(true);
            drawStorageEsp(matrices, tickDelta);
            drawBoxEsp(matrices, tickDelta);
            drawHitboxEsp(matrices, tickDelta);
            drawTracers(matrices, tickDelta);
            drawItemEsp(matrices, tickDelta);
            drawEntityOutlines(matrices, tickDelta);
        });
    }

    private void onEndTick(MinecraftClient client) {
        if (autoTotemCooldown > 0) {
            --autoTotemCooldown;
        }
        this.handleKeyToggles(client);
        // All module ticks handled by ModuleManager
        this.trackNearestPlayer(client);
        this.updateAnimations(client);
        float themeTarget = themeMode;
        themeTransition += (themeTarget - themeTransition) * 0.12f;
        if (Math.abs(themeTransition - themeTarget) < 5.0E-4f) {
            themeTransition = themeTarget;
        }
    }

    public static int themeColor(int[] colors) {
        return FengBoxClient.themeColor(colors[0], colors[1], colors[2], themeTransition);
    }

    public static int themeColor(int c0, int c1, int c2, float t) {
        if (t <= 0.0f) {
            return c0;
        }
        if (t >= 2.0f) {
            return c2;
        }
        if (t <= 1.0f) {
            return FengBoxClient.lerpColor(c0, c1, t);
        }
        return FengBoxClient.lerpColor(c1, c2, t - 1.0f);
    }

    public static boolean isGuiKey(int keyCode) {
        return keyCode == guiKeyCode;
    }

    public static KeyBinding getFeatureKeyBinding(String featureName) {
        return featureKeyBindings.get(featureName);
    }

    public void handleKeyToggles(MinecraftClient client) {
        while (autoCritKey.wasPressed()) {
            boolean bl = autoCritEnabled = !autoCritEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_crit." + (autoCritEnabled ? "on" : "off"))), true);
        }
        while (violentKey.wasPressed()) {
            boolean bl = violentMode = !violentMode;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.violent." + (violentMode ? "on" : "off"))), true);
        }
        while (flightKey.wasPressed()) {
            this.toggleFlight(client);
        }
        while (freecamKey.wasPressed()) {
            this.toggleFreecam(client);
        }
        while (guiKey.wasPressed()) {
            if (isClickGuiScreen(client.currentScreen)) {
                client.setScreen(null);
            } else {
                client.setScreen(new com.fengbox.clickgui.ClickGuiScreen());
            }
        }
        while (clearEffectsKey.wasPressed()) {
            boolean bl = clearEffectsEnabled = !clearEffectsEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.clear_effects." + (clearEffectsEnabled ? "on" : "off"))), true);
        }
        while (antiFallKey.wasPressed()) {
            boolean bl = antiFallEnabled = !antiFallEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.anti_fall." + (antiFallEnabled ? "on" : "off"))), true);
        }
        while (nightVisionKey.wasPressed()) {
            boolean bl = nightVisionEnabled = !nightVisionEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.night_vision." + (nightVisionEnabled ? "on" : "off"))), true);
        }
        while (noHurtShakeKey.wasPressed()) {
            boolean bl = noHurtShakeEnabled = !noHurtShakeEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.no_hurt_shake." + (noHurtShakeEnabled ? "on" : "off"))), true);
        }
        while (playerInfoKey.wasPressed()) {
            boolean bl = playerInfoEnabled = !playerInfoEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.player_info." + (playerInfoEnabled ? "on" : "off"))), true);
        }
        while (autoCrystalKey.wasPressed()) {
            boolean bl = autoCrystalEnabled = !autoCrystalEnabled;
            if (!autoCrystalEnabled) {
                crystalStep = 0;
                crystalCooldown = 0;
                crystalPlacePos = null;
                crystalRetries = 0;
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_crystal." + (autoCrystalEnabled ? "on" : "off"))), true);
        }
        while (storageEspKey.wasPressed()) {
            boolean bl = storageEspEnabled = !storageEspEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.storage_esp." + (storageEspEnabled ? "on" : "off"))), true);
        }
        while (playerTrackerKey.wasPressed()) {
            boolean bl = playerTrackerEnabled = !playerTrackerEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.player_tracker." + (playerTrackerEnabled ? "on" : "off"))), true);
        }
        while (hitboxEspKey.wasPressed()) {
            boolean bl = hitboxEspEnabled = !hitboxEspEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.hitbox_esp." + (hitboxEspEnabled ? "on" : "off"))), true);
        }
        while (autoTotemKey.wasPressed()) {
            boolean bl = autoTotemEnabled = !autoTotemEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_totem." + (autoTotemEnabled ? "on" : "off"))), true);
        }
        while (autoBridgeKey.wasPressed()) {
            boolean bl = autoBridgeEnabled = !autoBridgeEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_bridge." + (autoBridgeEnabled ? "on" : "off"))), true);
        }
        while (spinKey.wasPressed()) {
            boolean bl = spinEnabled = !spinEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.spin." + (spinEnabled ? "on" : "off"))), true);
        }
        while (whiteThemeKey.wasPressed()) {
            themeMode = (themeMode + 1) % 3;
            if (client.player == null) continue;
            String[] modeNames = new String[]{"\u00a7cDark", "\u00a7bWhite", "\u00a74Blood"};
            client.player.sendMessage((Text)Text.translatable((String)"msg.feng_box.white_theme.on").append(" \u00a78\u00bb ").append(modeNames[themeMode]), true);
        }
        while (vanillaUiKey.wasPressed()) {
            boolean bl = vanillaUiEnabled = !vanillaUiEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.vanilla_ui." + (vanillaUiEnabled ? "on" : "off"))), true);
        }
        while (testDummyKey.wasPressed()) {
            this.toggleTestDummy(client);
        }
        while (killAuraKey.wasPressed()) {
            boolean bl = killAuraEnabled = !killAuraEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.kill_aura." + (killAuraEnabled ? "on" : "off"))), true);
        }
        while (speedKey.wasPressed()) {
            boolean bl = speedEnabled = !speedEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.speed." + (speedEnabled ? "on" : "off"))), true);
        }
        while (stepKey.wasPressed()) {
            boolean bl = stepEnabled = !stepEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.step." + (stepEnabled ? "on" : "off"))), true);
        }
        while (sprintKey.wasPressed()) {
            boolean bl = sprintEnabled = !sprintEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.sprint." + (sprintEnabled ? "on" : "off"))), true);
        }
        while (autoWalkKey.wasPressed()) {
            boolean bl = autoWalkEnabled = !autoWalkEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_walk." + (autoWalkEnabled ? "on" : "off"))), true);
        }
        while (fullbrightKey.wasPressed()) {
            boolean bl = fullbrightEnabled = !fullbrightEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.fullbright." + (fullbrightEnabled ? "on" : "off"))), true);
        }
        while (tracersKey.wasPressed()) {
            boolean bl = tracersEnabled = !tracersEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.tracers." + (tracersEnabled ? "on" : "off"))), true);
        }
        while (nameTagsKey.wasPressed()) {
            boolean bl = nameTagsEnabled = !nameTagsEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.name_tags." + (nameTagsEnabled ? "on" : "off"))), true);
        }
        while (itemEspKey.wasPressed()) {
            boolean bl = itemEspEnabled = !itemEspEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.item_esp." + (itemEspEnabled ? "on" : "off"))), true);
        }
        while (chestStealerKey.wasPressed()) {
            boolean bl = chestStealerEnabled = !chestStealerEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.chest_stealer." + (chestStealerEnabled ? "on" : "off"))), true);
        }
        while (autoToolKey.wasPressed()) {
            boolean bl = autoToolEnabled = !autoToolEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_tool." + (autoToolEnabled ? "on" : "off"))), true);
        }
        while (autoRespawnKey.wasPressed()) {
            boolean bl = autoRespawnEnabled = !autoRespawnEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_respawn." + (autoRespawnEnabled ? "on" : "off"))), true);
        }
        while (antiAfkKey.wasPressed()) {
            boolean bl = antiAfkEnabled = !antiAfkEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.anti_afk." + (antiAfkEnabled ? "on" : "off"))), true);
        }
        while (autoEatKey.wasPressed()) {
            boolean bl = autoEatEnabled = !autoEatEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_eat." + (autoEatEnabled ? "on" : "off"))), true);
        }
        while (multiJumpKey.wasPressed()) {
            boolean bl = multiJumpEnabled = !multiJumpEnabled;
            if (!multiJumpEnabled) {
                multiJumpLeft = 0;
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.multi_jump." + (multiJumpEnabled ? "on" : "off"))), true);
        }
        while (autoAnchorKey.wasPressed()) {
            boolean bl = autoAnchorEnabled = !autoAnchorEnabled;
            if (!autoAnchorEnabled) {
                anchorStep = 0;
                anchorCooldown = 0;
                anchorPos = null;
                anchorRetries = 0;
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_anchor." + (autoAnchorEnabled ? "on" : "off"))), true);
        }
        while (guiMoveKey.wasPressed()) {
            boolean bl = guiMoveEnabled = !guiMoveEnabled;
            if (!guiMoveEnabled) {
                this.releaseGuiMoveKeys();
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.gui_move." + (guiMoveEnabled ? "on" : "off"))), true);
        }
        while (entityOutlineKey.wasPressed()) {
            boolean bl = entityOutlineEnabled = !entityOutlineEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.entity_outline." + (entityOutlineEnabled ? "on" : "off"))), true);
        }
        while (boxEspKey.wasPressed()) {
            boolean bl = boxEspEnabled = !boxEspEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.box_esp." + (boxEspEnabled ? "on" : "off"))), true);
        }
        while (replenishKey.wasPressed()) {
            boolean bl = replenishEnabled = !replenishEnabled;
            if (!replenishEnabled) {
                Arrays.fill(replenishTracked, null);
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.replenish." + (replenishEnabled ? "on" : "off"))), true);
        }
        while (armorSaveKey.wasPressed()) {
            boolean bl = armorSaveEnabled = !armorSaveEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.armor_save." + (armorSaveEnabled ? "on" : "off"))), true);
        }
        while (bowAimKey.wasPressed()) {
            boolean bl = bowAimEnabled = !bowAimEnabled;
            if (!bowAimEnabled) {
                bowAimTarget = null;
                bowAimCooldown = 0;
            }
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.bow_aim." + (bowAimEnabled ? "on" : "off"))), true);
        }
        while (fastUseKey.wasPressed()) {
            boolean bl = fastUseEnabled = !fastUseEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.fast_use." + (fastUseEnabled ? "on" : "off"))), true);
        }
        while (fastBreakKey.wasPressed()) {
            boolean bl = fastBreakEnabled = !fastBreakEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.fast_break." + (fastBreakEnabled ? "on" : "off"))), true);
        }
        while (autoBlockKey.wasPressed()) {
            boolean bl = autoBlockEnabled = !autoBlockEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.auto_block." + (autoBlockEnabled ? "on" : "off"))), true);
        }
        while (projectileDodgeKey.wasPressed()) {
            boolean bl = projectileDodgeEnabled = !projectileDodgeEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.projectile_dodge." + (projectileDodgeEnabled ? "on" : "off"))), true);
        }
        while (totemGhostKey.wasPressed()) {
            boolean bl = totemGhostEnabled = !totemGhostEnabled;
            if (client.player == null) continue;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.totem_ghost." + (totemGhostEnabled ? "on" : "off"))), true);
        }
        while (itemPhysicsKey.wasPressed()) {
            boolean bl = itemPhysicsEnabled = !itemPhysicsEnabled;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.item_physics." + (itemPhysicsEnabled ? "on" : "off"))), true);
            this.saveConfig();
        }
        while (maceCritKey.wasPressed()) {
            boolean bl = maceCritEnabled = !maceCritEnabled;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.mace_crit." + (maceCritEnabled ? "on" : "off"))), true);
            this.saveConfig();
        }
        while (tridentImpulseKey.wasPressed()) {
            boolean bl = tridentImpulseEnabled = !tridentImpulseEnabled;
            client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.trident_impulse." + (tridentImpulseEnabled ? "on" : "off"))), true);
            this.saveConfig();
        }
        this.saveConfig();
    }

    public static void releaseGuiMoveKeys() {
        KeyBinding[] keys;
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.options == null) {
            return;
        }
        for (KeyBinding k : keys = new KeyBinding[]{c.options.forwardKey, c.options.backKey, c.options.leftKey, c.options.rightKey, c.options.jumpKey, c.options.sneakKey, c.options.sprintKey}) {
            k.setPressed(false);
        }
        guiMoveActive = false;
    }

    public static void toggleTestDummy(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }
        if (testDummy != null) {
            NetworkHelper.removeDummySkinEntry(client, FengBoxClient.testDummy);
            testDummy.discard();
            testDummy = null;
            if (client.player != null) {
                client.player.sendMessage((Text)Text.literal((String)"\u00a7b\u00a7lTest Dummy removed"), true);
            }
            return;
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), "\u00a7b\u00a7lTestPlayer");
        OtherClientPlayerEntity dummy = new OtherClientPlayerEntity(client.world, profile);
        Vec3d pos = new Vec3d(client.player.getX(), client.player.getY(), client.player.getZ()).add(client.player.getRotationVec(1.0f).multiply(3.0));
        dummy.refreshPositionAndAngles(pos.x, client.player.getY(), pos.z, 0.0f, 0.0f);
        dummy.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        dummy.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
        dummy.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        dummy.equipStack(net.minecraft.entity.EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
        dummy.getInventory().setStack(dummy.getInventory().getSelectedSlot(), new ItemStack(Items.NETHERITE_SWORD));
        dummy.setHealth(20.0f);
        NetworkHelper.addDummySkinEntry(client, (PlayerEntity)dummy, profile);
        client.world.addEntity(dummy);
        testDummy = dummy;
        client.player.sendMessage((Text)Text.literal((String)"\u00a7b\u00a7lSpawned Test Dummy"), true);
    }



    public static boolean isMultiplayer(MinecraftClient client) {
        return client.getServer() == null;
    }

    public void handleFreecam(MinecraftClient client) {
        if (!freecamEnabled || cameraMarker == null) {
            return;
        }
        if (client.player == null || freecamFrozenPos == null) {
            return;
        }
        client.player.input.playerInput = new net.minecraft.util.PlayerInput(false, false, false, false, false, false, false);
        client.player.noClip = true;
        client.player.setVelocity(Vec3d.ZERO);
        // client.// player.velocityModified = true; // TODO: field removed in 1.21.2+ // TODO: field removed in 1.21.2+
        cameraMarker.setYaw(client.player.getYaw());
        cameraMarker.setPitch(client.player.getPitch());
        double forward = client.options.forwardKey.isPressed() ? 1.0 : 0.0;
        double back = client.options.backKey.isPressed() ? 1.0 : 0.0;
        double left = client.options.leftKey.isPressed() ? 1.0 : 0.0;
        double right = client.options.rightKey.isPressed() ? 1.0 : 0.0;
        double up = client.options.jumpKey.isPressed() ? 1.0 : 0.0;
        double down = client.options.sneakKey.isPressed() ? 1.0 : 0.0;
        float yawRad = cameraMarker.getYaw() * ((float)Math.PI / 180);
        double strafe = left - right;
        double forwardBack = forward - back;
        double mx = strafe * Math.cos(yawRad) - forwardBack * Math.sin(yawRad);
        double mz = strafe * Math.sin(yawRad) + forwardBack * Math.cos(yawRad);
        double my = up - down;
        Vec3d move = new Vec3d(mx, my, mz).normalize().multiply(freecamSpeed);
        cameraMarker.setPosition(new Vec3d(cameraMarker.getX(), cameraMarker.getY(), cameraMarker.getZ()).add(move));
        if (this.isMultiplayer(client) && ++freecamSyncTimer >= 20) {
            freecamSyncTimer = 0;
            NetworkHelper.sendFreecamSync(client.player, FengBoxClient.freecamFrozenPos.x, FengBoxClient.freecamFrozenPos.y, FengBoxClient.freecamFrozenPos.z);
        }
        Vec3d frozen = freecamFrozenPos;
        client.player.setPos(frozen.x, frozen.y, frozen.z);
    }

    public static void toggleFreecam(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        boolean bl = freecamEnabled = !freecamEnabled;
        if (freecamEnabled) {
            FengBoxClient.enterFreecam(client);
        } else {
            FengBoxClient.exitFreecam(client);
        }
        client.player.sendMessage((Text)Text.translatable((String)("msg.feng_box.freecam." + (freecamEnabled ? "on" : "off"))), true);
    }

    public static void enterFreecam(MinecraftClient client) {
        IntegratedServer server;
        ClientPlayerEntity player = client.player;
        freecamFrozenPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        freecamPrevFlight = player.getAbilities().allowFlying;
        freecamPrevFlying = player.getAbilities().flying;
        if (client.world != null && (cameraMarker = EntityType.MARKER.create(client.world, net.minecraft.entity.SpawnReason.COMMAND)) != null) {
            Vec3d eyePos = player.getEyePos();
            cameraMarker.setPosition(eyePos);
            cameraMarker.setYaw(player.getYaw());
            cameraMarker.setPitch(player.getPitch());
            FengBoxClient.cameraMarker.noClip = true;
            client.world.addEntity(cameraMarker);
            client.setCameraEntity(cameraMarker);
        }
        if ((server = client.getServer()) != null) {
            Vec3d pos = freecamFrozenPos;
            server.execute(() -> FengBoxClient.lambda$enterFreecam$3((MinecraftServer)server, player, pos));
        }
        player.noClip = true;
    }

    public static void exitFreecam(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        client.setCameraEntity((Entity)player);
        if (cameraMarker != null) {
            cameraMarker.discard();
            cameraMarker = null;
        }
        if (freecamFrozenPos != null) {
            Vec3d pos = freecamFrozenPos;
            IntegratedServer server = client.getServer();
            if (server != null) {
                server.execute(() -> FengBoxClient.lambda$exitFreecam$4((MinecraftServer)server, player, pos));
            }
        }
        player.noClip = false;
        player.getAbilities().allowFlying = freecamPrevFlight;
        player.getAbilities().flying = false;
        if (freecamFrozenPos != null) {
            player.refreshPositionAndAngles(FengBoxClient.freecamFrozenPos.x, FengBoxClient.freecamFrozenPos.y, FengBoxClient.freecamFrozenPos.z, player.getYaw(), player.getPitch());
            player.setOnGround(true);
        }
        freecamFrozenPos = null;
    }

    public static float angleDiff(float target, float current) {
        float diff;
        for (diff = target - current; diff > 180.0f; diff -= 360.0f) {
        }
        while (diff < -180.0f) {
            diff += 360.0f;
        }
        return diff;
    }

    public static void rotateSmoothly(ClientPlayerEntity player, float targetYaw, float targetPitch, float speed, int mode) {
        float hbDiff;
        float yawDiff;
        float yStep = 0.0f;
        float yDir = 0.0f;
        if (mode == 2 || player == null) {
            return;
        }
        long t = System.currentTimeMillis();
        float effSpeed = speed;
        if (rotHumanize) {
            float factor = 1.0f;
            yawDiff = FengBoxClient.angleDiff(targetYaw, player.getYaw());
            float absDiff = Math.max(Math.abs(yawDiff), Math.abs(targetPitch - player.getPitch()));
            float wave = 1.0f + 0.35f * (float)Math.sin((double)t / 230.0) + 0.12f * (float)Math.sin((double)t / 97.0 + 1.7) + 0.05f * (float)(Math.random() * 2.0 - 1.0);
            float f = absDiff > 60.0f ? 1.6f : (factor = absDiff < 5.0f ? 0.8f : 1.0f);
            if (Math.abs(yawDiff) > 90.0f) {
                factor *= 1.25f;
            }
            effSpeed = speed * wave * factor;
        }
        if (mode == 1) {
            double q;
            double divisor;
            float yDiff = FengBoxClient.angleDiff(targetYaw, player.getYaw());
            yStep = Math.min(effSpeed, Math.abs(yDiff));
            yDir = Math.copySign(1.0f, yDiff);
            if (rotHumanize && (divisor = FengBoxClient.mouseGridDivisor()) > 1.0E-4 && Math.abs(q = (double)Math.round((double)yDiff / divisor) * divisor) > 0.0 && Math.abs(q) <= (double)Math.abs(yDiff)) {
                yStep = (float)Math.min((double)effSpeed, Math.abs(q));
                yDir = (float)Math.signum(q);
            }
            player.setYaw(player.getYaw() + yDir * yStep);
            player.headYaw = player.getYaw();
            float pDiff = targetPitch - player.getPitch();
            float pStep = Math.min(effSpeed * 0.5f, Math.abs(pDiff));
            player.setPitch(player.getPitch() + Math.copySign(pStep, pDiff));
            NetworkHelper.sendLookAndOnGround(player, player.getYaw(), player.getPitch(), player.isOnGround());
        } else {
            double q;
            double divisor;
            yawDiff = FengBoxClient.angleDiff(targetYaw, player.headYaw);
            yStep = Math.min(effSpeed, Math.abs(yawDiff));
            yDir = Math.copySign(1.0f, yawDiff);
            if (rotHumanize && (divisor = FengBoxClient.mouseGridDivisor()) > 1.0E-4 && Math.abs(q = (double)Math.round((double)yawDiff / divisor) * divisor) > 0.0 && Math.abs(q) <= (double)Math.abs(yawDiff)) {
                yStep = (float)Math.min((double)effSpeed, Math.abs(q));
                yDir = (float)Math.signum(q);
            }
            player.headYaw += yDir * yStep;
        }
        if (rotHumanize && Math.abs(FengBoxClient.angleDiff(targetYaw, player.headYaw)) < 5.0f) {
            float jitter = (float)(Math.sin((double)t / 80.0) * 0.8 + Math.cos((double)t / 120.0) * 0.5);
            player.headYaw += jitter;
        }
        if (Math.abs(hbDiff = FengBoxClient.angleDiff(player.headYaw, player.bodyYaw)) > 60.0f) {
            float bodyTarget = player.headYaw + (hbDiff > 0.0f ? -55.0f : 55.0f);
            float bd = FengBoxClient.angleDiff(bodyTarget, player.bodyYaw);
            float turnMag = Math.abs(hbDiff);
            float bodySpeed = Math.min(8.0f, Math.max(3.0f, effSpeed * 0.5f + turnMag * 0.03f));
            player.bodyYaw = Math.abs(bd) <= bodySpeed ? bodyTarget : (player.bodyYaw += MathHelper.clamp((float)bd, (float)(-bodySpeed), (float)bodySpeed));
        }
    }

    public static double mouseGridDivisor() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return 0.0;
        }
        double sens = (Double)client.options.getMouseSensitivity().getValue();
        double s = sens * 0.6 + 0.2;
        double multiplier = s * s * s * 8.0;
        return multiplier * 0.15;
    }

    public Entity findBestTarget(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return null;
        }
        double bestDist = Double.MAX_VALUE;
        Entity best = null;
        Vec3d eyePos = player.getEyePos();
        for (Entity entity : client.world.getEntities()) {
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator() || autoCrystalIgnoreNakeds && FengBoxClient.isNaked(living)) continue;
            double distance = eyePos.distanceTo(new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
            Vec3d targetEyes = new Vec3d(entity.getX(), entity.getY(), entity.getZ()).add(0.0, (double)entity.getHeight() * 0.5, 0.0);
            boolean blocked = client.world.raycast(new RaycastContext(eyePos, targetEyes, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)player)).getType() == HitResult.Type.BLOCK;
            double range = blocked ? (double)autoCrystalWallsRange : (double)autoCrystalRange;
            if (distance > range || !(distance < bestDist)) continue;
            bestDist = distance;
            best = entity;
        }
        return best;
    }

    public static boolean isNaked(LivingEntity e) {
        if (!(e instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity p = (PlayerEntity)e;
        for (ItemStack s : new ItemStack[]{p.getEquippedStack(EquipmentSlot.HEAD), p.getEquippedStack(EquipmentSlot.CHEST), p.getEquippedStack(EquipmentSlot.LEGS), p.getEquippedStack(EquipmentSlot.FEET)}) {
            if (s.isEmpty()) continue;
            return false;
        }
        return p.getMainHandStack().isEmpty() && p.getOffHandStack().isEmpty();
    }

    public void handleClearEffects(MinecraftClient client) {
        if (!clearEffectsEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive()) {
            return;
        }
        player.clearStatusEffects();
    }

    public void handleAntiFall(MinecraftClient client) {
        if (!antiFallEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.isUsingItem()) {
            return;
        }
        if (player.getAbilities().flying) {
            return;
        }
        if (this.notHit(player)) {
            return;
        }
        if (player.fallDistance <= 3.0f) {
            return;
        }
        switch (antiFallMode) {
            case 1: {
                if (!(player.getVelocity().y < 0.0)) break;
                NetworkHelper.sendOnGroundOnly(player, true);
                player.fallDistance = 0.0f;
                break;
            }
            case 2: {
                if (!(player.getVelocity().y < -0.5)) break;
                if (mlgCooldown > 0) {
                    --mlgCooldown;
                    break;
                }
                if (!(this.groundDistBelow(player) <= 2.0f)) break;
                this.mlgWater(player);
                break;
            }
            case 3: {
                if (!player.isOnGround() || !(player.fallDistance > 0.0f)) break;
                player.jump();
                player.fallDistance = 0.0f;
                break;
            }
            default: {
                float dist;
                if (!(player.getVelocity().y < -0.5) || !((dist = this.groundDistBelow(player)) <= 3.0f)) break;
                NetworkHelper.sendOnGroundOnly(player, true);
                player.fallDistance = 0.0f;
            }
        }
    }

    public static boolean notHit(ClientPlayerEntity player) {
        int mask = antiFallNotMask;
        if ((mask & 1) != 0 && player.isTouchingWater()) {
            return true;
        }
        if ((mask & 2) != 0 && player.isInLava()) {
            return true;
        }
        if ((mask & 4) != 0 && MinecraftClient.getInstance().world.getBlockState(player.getBlockPos()).getBlock() == Blocks.COBWEB) {
            return true;
        }
        if ((mask & 8) != 0 && player.isGliding()) {
            return true;
        }
        return (mask & 0x10) != 0 && player.isOnGround();
    }

    public static void mlgWater(ClientPlayerEntity player) {
        int bucketSlot = -1;
        for (int i = 0; i < 9; ++i) {
            if (player.getInventory().getStack(i).getItem() != Items.WATER_BUCKET) continue;
            bucketSlot = i;
            break;
        }
        if (bucketSlot < 0) {
            return;
        }
        player.getInventory().setSelectedSlot(bucketSlot);
        BlockPos below = player.getBlockPos().down();
        BlockHitResult hit = new BlockHitResult(new Vec3d((double)below.getX() + 0.5, (double)below.getY() + 0.5, (double)below.getZ() + 0.5), Direction.UP, below, false);
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.interactionManager != null) {
            mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
        }
        player.swingHand(Hand.MAIN_HAND);
        mlgCooldown = 10;
    }

    public static float groundDistBelow(ClientPlayerEntity player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        int bx = (int)Math.floor(x);
        int by = (int)Math.floor(y);
        int bz = (int)Math.floor(z);
        World w = MinecraftClient.getInstance().world;
        for (int dy = 0; dy <= 16; ++dy) {
            BlockPos bp = new BlockPos(bx, by - dy - 1, bz);
            BlockState st = w.getBlockState(bp);
            if (st.isAir() || !st.isSolidBlock((BlockView)w, bp)) continue;
            return (float)(y - (double)(bp.getY() + 1));
        }
        return 999.0f;
    }

    public void handleNightVision(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (nightVisionEnabled) {
            if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
            }
        } else {
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    public void handleNoHurtShake(MinecraftClient client) {
        if (!noHurtShakeEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        player.hurtTime = 0;
    }

    public void handleAutoCrystal(MinecraftClient client) {
        BlockPos feet;
        BlockState feetDown;
        boolean targetHurt;
        if (!autoCrystalEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null) {
            return;
        }
        if (!player.isAlive()) {
            return;
        }
        Entity target = this.findBestTarget(client);
        if (target == null) {
            crystalStep = 0;
            crystalPlacePos = null;
            crystalRetries = 0;
            return;
        }
        boolean bl = targetHurt = target instanceof LivingEntity && ((LivingEntity)target).hurtTime > 0;
        if (crystalCooldown > 0) {
            if (autoCrystalSmartDelay && targetHurt && crystalCooldown <= 2) {
                crystalCooldown = 0;
            } else {
                --crystalCooldown;
                return;
            }
        }
        int obsidianSlot = -1;
        int crystalSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.OBSIDIAN) {
                obsidianSlot = i;
            }
            if (stack.getItem() != Items.END_CRYSTAL) continue;
            crystalSlot = i;
        }
        if (autoCrystalSupport && crystalStep == 0 && obsidianSlot >= 0 && (feetDown = client.world.getBlockState((feet = target.getBlockPos()).down())).isAir() && client.world.getBlockState(feet).isAir() && player.squaredDistanceTo((double)feet.getX() + 0.5, (double)feet.getY() - 0.5, (double)feet.getZ() + 0.5) <= 36.0) {
            player.getInventory().setSelectedSlot(obsidianSlot);
            BlockHitResult hit = new BlockHitResult(new Vec3d((double)feet.getX() + 0.5, (double)feet.getY() - 0.5, (double)feet.getZ() + 0.5), Direction.UP, feet.down().down(), false);
            client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
            player.swingHand(Hand.MAIN_HAND);
            crystalCooldown = 3;
            return;
        }
        switch (crystalStep) {
            case 0: {
                BlockPos targetPos;
                BlockPos validPos;
                if (crystalPlacePos != null) {
                    if (client.world.getBlockState(crystalPlacePos).getBlock() == Blocks.OBSIDIAN && player.squaredDistanceTo((double)crystalPlacePos.getX() + 0.5, (double)crystalPlacePos.getY() + 0.5, (double)crystalPlacePos.getZ() + 0.5) <= 36.0) {
                        crystalRetries = 0;
                        crystalStep = 1;
                        break;
                    }
                    crystalPlacePos = null;
                }
                if ((validPos = this.findCrystalPlacePos(client, targetPos = target.getBlockPos())) == null) {
                    crystalCooldown = 3;
                    break;
                }
                crystalPlacePos = validPos;
                if (client.world.getBlockState(validPos).getBlock() == Blocks.OBSIDIAN) {
                    crystalRetries = 0;
                    crystalStep = 1;
                    break;
                }
                if (obsidianSlot < 0) break;
                player.getInventory().setSelectedSlot(obsidianSlot);
                BlockHitResult hit = new BlockHitResult(new Vec3d((double)validPos.getX() + 0.5, (double)validPos.getY(), (double)validPos.getZ() + 0.5), Direction.UP, validPos.down(), false);
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                player.swingHand(Hand.MAIN_HAND);
                crystalRetries = 0;
                crystalCooldown = 3;
                crystalStep = 1;
                break;
            }
            case 1: {
                if (crystalPlacePos != null && crystalSlot >= 0) {
                    if (client.world.getBlockState(crystalPlacePos).getBlock() == Blocks.OBSIDIAN) {
                        if (autoCrystalFastBreak && this.crystalEntityAt(client, crystalPlacePos) != null) {
                            crystalRetries = 0;
                            crystalStep = 2;
                            break;
                        }
                        player.getInventory().setSelectedSlot(crystalSlot);
                        BlockHitResult hit = new BlockHitResult(new Vec3d((double)crystalPlacePos.getX() + 0.5, (double)crystalPlacePos.getY() + 1.0, (double)crystalPlacePos.getZ() + 0.5), Direction.UP, crystalPlacePos, false);
                        client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                        player.swingHand(Hand.MAIN_HAND);
                        crystalRetries = 0;
                        crystalCooldown = 3;
                        crystalStep = 2;
                        break;
                    }
                    if (++crystalRetries < 40) {
                        crystalCooldown = 1;
                        break;
                    }
                }
                crystalStep = 0;
                crystalPlacePos = null;
                crystalRetries = 0;
                break;
            }
            case 2: {
                Entity nearestCrystal;
                Entity entity = nearestCrystal = autoCrystalFastBreak ? this.crystalEntityAt(client, crystalPlacePos) : null;
                if (nearestCrystal == null) {
                    double minDistSq = 36.0;
                    for (Entity e : client.world.getEntities()) {
                        double d;
                        if (e.getType() != EntityType.END_CRYSTAL || !e.isAlive() || !((d = player.squaredDistanceTo((Entity)e)) < minDistSq)) continue;
                        minDistSq = d;
                        nearestCrystal = e;
                    }
                }
                if (nearestCrystal != null) {
                    player.swingHand(Hand.MAIN_HAND);
                    client.interactionManager.attackEntity((PlayerEntity)player, nearestCrystal);
                    crystalRetries = 0;
                    crystalCooldown = 2;
                    crystalStep = 0;
                    crystalPlacePos = null;
                    break;
                }
                if (++crystalRetries < 40) {
                    crystalCooldown = 1;
                    break;
                }
                crystalStep = 0;
                crystalPlacePos = null;
                crystalRetries = 0;
            }
        }
        if (autoCrystalRotMode != 2 && player != null) {
            Vec3d lookTarget = null;
            if (crystalStep == 0 && crystalPlacePos != null) {
                lookTarget = new Vec3d((double)crystalPlacePos.getX() + 0.5, (double)crystalPlacePos.getY() + 0.5, (double)crystalPlacePos.getZ() + 0.5);
            } else if (crystalStep == 1 && crystalPlacePos != null) {
                lookTarget = new Vec3d((double)crystalPlacePos.getX() + 0.5, (double)crystalPlacePos.getY() + 1.0, (double)crystalPlacePos.getZ() + 0.5);
            } else if (crystalStep == 2) {
                Entity nearestCrystal;
                Entity entity = nearestCrystal = autoCrystalFastBreak ? this.crystalEntityAt(client, crystalPlacePos) : null;
                if (nearestCrystal == null) {
                    double minDistSq = 36.0;
                    for (Entity e : client.world.getEntities()) {
                        double d;
                        if (e.getType() != EntityType.END_CRYSTAL || !e.isAlive() || !((d = player.squaredDistanceTo(e)) < minDistSq)) continue;
                        minDistSq = d;
                        nearestCrystal = e;
                    }
                }
                if (nearestCrystal != null) {
                    lookTarget = new Vec3d(nearestCrystal.getX(), nearestCrystal.getY(), nearestCrystal.getZ());
                }
            }
            if (lookTarget != null) {
                Vec3d eyePos = player.getEyePos();
                Vec3d diff = lookTarget.subtract(eyePos);
                float tYaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
                float tPitch = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))))), (float)-90.0f, (float)90.0f);
                this.rotateSmoothly(player, tYaw, tPitch, 20.0f, autoCrystalRotMode);
            }
        }
    }

    public static Entity crystalEntityAt(MinecraftClient client, BlockPos pos) {
        if (client.world == null || pos == null) {
            return null;
        }
        for (Entity e : client.world.getEntities()) {
            BlockPos ep;
            if (e.getType() != EntityType.END_CRYSTAL || !e.isAlive() || (ep = e.getBlockPos()).getX() != pos.getX() || ep.getZ() != pos.getZ() || Math.abs(ep.getY() - (pos.getY() + 1)) > 1) continue;
            return e;
        }
        return null;
    }

    public void handleAutoWeb(MinecraftClient client) {
        if (!autoWebEnabled) {
            this.webReset();
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null || !player.isAlive() || freecamEnabled) {
            this.webReset();
            return;
        }
        switch (webPhase) {
            case 0: {
                this.webTickSearch(client);
                break;
            }
            case 1: {
                this.webTickPlace(client);
                break;
            }
            case 2: {
                this.webTickLavaRetrieve(client);
                break;
            }
            case 3: {
                this.webTickGroundWebBreak(client);
            }
        }
    }

    public void webTickSearch(MinecraftClient client) {
        int lavaSlot;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        int webSlot = FengBoxClient.webFindSlot(player, Items.COBWEB);
        int n = lavaSlot = autoWebLava ? FengBoxClient.webFindSlot(player, Items.LAVA_BUCKET) : -1;
        if (webSlot < 0 && lavaSlot < 0) {
            FengBoxClient.webReset();
            return;
        }
        if (webBreakCooldown > 0) {
            --webBreakCooldown;
            return;
        }
        if (webWaitTicks < autoWebDelay) {
            ++webWaitTicks;
            return;
        }
        webWaitTicks = 0;
        Vec3d eyePos = player.getEyePos();
        ArrayList<Entity> candidates = new ArrayList<Entity>();
        for (Entity entity : client.world.getEntities()) {
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator() || eyePos.distanceTo(new Vec3d(entity.getX(), entity.getY(), entity.getZ())) > (double)autoWebRange) continue;
            candidates.add(entity);
        }
        candidates.sort(Comparator.comparingDouble(e -> eyePos.distanceTo(new Vec3d(e.getX(), e.getY(), e.getZ()))));
        Iterator<Entity> iterator = candidates.iterator();
        while (iterator.hasNext()) {
            Entity entity;
            webTarget = entity = (Entity)iterator.next();
            BlockPos feet = entity.getBlockPos();
            if (client.world.getBlockState(feet).getBlock() == Blocks.COBWEB) {
                if (!this.webTryEscapePredict(client, entity, webSlot, lavaSlot)) continue;
                return;
            }
            if (!FengBoxClient.webPreparePlace(client, feet)) continue;
            if (webSlot >= 0) {
                webPlaceType = 0;
                webGroundBreakTarget = feet;
                webSecondWebPending = autoWebGroundWeb && FengBoxClient.webCanPlaceGroundWeb(entity);
            } else {
                if (!FengBoxClient.webShouldPlaceLava(client, entity, false)) continue;
                webPlaceType = 2;
            }
            FengBoxClient.webBeginPlace();
            return;
        }
        webTarget = null;
    }

    private boolean webTryEscapePredict(MinecraftClient client, Entity entity, int webSlot, int lavaSlot) {
        if (webSlot < 0 && lavaSlot < 0) {
            return false;
        }
        Vec3d vel = entity.getVelocity();
        double vx = vel.x;
        double vz = vel.z;
        if (vx * vx + vz * vz < 4.0E-4) {
            return false;
        }
        for (int tickAhead = 2; tickAhead <= 3; ++tickAhead) {
            Box predicted = entity.getBoundingBox().offset(vx * (double)tickAhead, 0.0, vz * (double)tickAhead);
            if (FengBoxClient.webIsAllCobweb(client, predicted)) continue;
            for (BlockPos[] pair : FengBoxClient.webPositionsForAabb(client, predicted)) {
                BlockPos foot = pair[0];
                BlockPos head = pair[1];
                if (lavaSlot >= 0 && FengBoxClient.webCanPlaceLavaAt(client, entity, head) && FengBoxClient.webPreparePlace(client, head)) {
                    webPlaceType = 3;
                    webLavaPos = head;
                    FengBoxClient.webBeginPlace();
                    return true;
                }
                if (webSlot < 0 || !FengBoxClient.webPreparePlace(client, foot)) continue;
                webPlaceType = 4;
                webGroundBreakTarget = foot;
                webSecondWebPending = true;
                FengBoxClient.webBeginPlace();
                return true;
            }
        }
        return false;
    }

    public static boolean webPreparePlace(MinecraftClient client, BlockPos place) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        if (!FengBoxClient.webIsBlockReplaceable(client, place)) {
            return false;
        }
        BlockPos support = FengBoxClient.webFindSupport(client, place);
        if (support == null) {
            return false;
        }
        Direction face = FengBoxClient.faceToward(support, place);
        Vec3d aim = FengBoxClient.webFindFaceHitPoint(client, support, face);
        if (aim == null) {
            return false;
        }
        webPlacePos = place;
        webSupportPos = support;
        webFace = face;
        webAimPoint = aim;
        webPlaceSent = false;
        return true;
    }

    public static void webBeginPlace() {
        webLavaPlaced = false;
        webRetriedLavaPlace = false;
        webWaitTicks = 0;
        webRetrieveTicks = 0;
        webRetrieveAttempts = 0;
        webSourceWaitCount = 0;
        webLastSupportPos = webSupportPos;
        webLastFace = webFace;
        webLastAimPoint = webAimPoint;
        webPhase = 1;
    }

    public void webTickPlace(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || webPlacePos == null || webSupportPos == null || webAimPoint == null) {
            FengBoxClient.webReset();
            return;
        }
        BlockState state = client.world.getBlockState(webPlacePos);
        if (webPlaceType == 0 || webPlaceType == 4) {
            if (webPlaceSent) {
                if (state.getBlock() == Blocks.COBWEB) {
                    webPlaceSent = false;
                    this.webAfterWebPlaced(client);
                    return;
                }
                if (++webRetrieveAttempts > 8) {
                    FengBoxClient.webReset();
                    return;
                }
                return;
            }
            if (state.getBlock() == Blocks.COBWEB) {
                this.webAfterWebPlaced(client);
                return;
            }
            if (!state.isAir()) {
                FengBoxClient.webReset();
                return;
            }
            if (!this.webTryInteract(client, Items.COBWEB, webPlacePos, webSupportPos, webFace, webAimPoint)) {
                return;
            }
            webPlaceSent = true;
            webRetrieveAttempts = 0;
            webBreakCooldown = autoWebDelay + 2;
        } else {
            if (state.getBlock() == Blocks.LAVA) {
                webLavaPlaced = true;
                webLavaPos = webPlacePos;
                webPhase = 2;
                return;
            }
            if (!state.isAir()) {
                FengBoxClient.webReset();
                return;
            }
            if (!this.webTryInteract(client, Items.LAVA_BUCKET, webPlacePos, webSupportPos, webFace, webAimPoint)) {
                return;
            }
            webLavaPlaced = true;
            webLavaPos = webPlacePos;
            webWaitTicks = 0;
            webRetrieveTicks = 0;
            webPhase = 2;
        }
    }

    private void webAfterWebPlaced(MinecraftClient client) {
        if (webPlaceType == 4 || webSecondWebPending) {
            if (webSecondWebPending) {
                webSecondWebPending = false;
                BlockPos second = webPlacePos.up();
                if (FengBoxClient.webPreparePlace(client, second)) {
                    webPhase = 1;
                    return;
                }
            }
            webFootPos = webGroundBreakTarget != null ? webGroundBreakTarget : webPlacePos;
            webBreakTicks = 0;
            webBreakStarted = false;
            webPhase = 3;
            return;
        }
        if (autoWebLava && webTarget != null && FengBoxClient.webFindSlot(client.player, Items.LAVA_BUCKET) >= 0 && FengBoxClient.webShouldPlaceLava(client, webTarget, true)) {
            BlockPos lavaPos = FengBoxClient.webPickLavaPos(client, webTarget);
            if (lavaPos == null) {
                lavaPos = FengBoxClient.webPickLavaGround(client, webTarget);
            }
            if (lavaPos == null) {
                FengBoxClient.webReset();
                return;
            }
            webLavaPos = lavaPos;
            webLavaPlaced = false;
            webRetriedLavaPlace = false;
            webWaitTicks = 0;
            webRetrieveTicks = 0;
            webRetrieveAttempts = 0;
            webSourceWaitCount = 0;
            webPhase = 2;
            return;
        }
        FengBoxClient.webReset();
    }

    public void webTickLavaRetrieve(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || webLavaPos == null) {
            FengBoxClient.webReset();
            return;
        }
        if (!webLavaPlaced) {
            if (webWaitTicks < Math.max(1, autoWebDelay / 2)) {
                ++webWaitTicks;
                return;
            }
            webWaitTicks = 0;
            BlockState st = client.world.getBlockState(webLavaPos);
            if (st.getBlock() == Blocks.LAVA) {
                webLavaPlaced = true;
                webRetrieveTicks = 0;
                return;
            }
            if (!st.isAir() && st.getBlock() != Blocks.COBWEB) {
                FengBoxClient.webReset();
                return;
            }
            BlockPos support = FengBoxClient.webFindSupport(client, webLavaPos);
            if (support == null) {
                FengBoxClient.webReset();
                return;
            }
            Direction face = FengBoxClient.faceToward(support, webLavaPos);
            if (!this.webTryInteract(client, Items.LAVA_BUCKET, webLavaPos, support, face, FengBoxClient.webFindFaceHitPoint(client, support, face))) {
                return;
            }
            webLavaPlaced = true;
            webRetrieveTicks = 0;
            return;
        }
        if (webRetrieveTicks < Math.max(1, autoWebDelay)) {
            ++webRetrieveTicks;
            return;
        }
        webRetrieveTicks = 0;
        if (!autoWebLavaWaitWater) {
            FengBoxClient.webReset();
            return;
        }
        if (FengBoxClient.webFindSlot(player, Items.WATER_BUCKET) < 0) {
            FengBoxClient.webReset();
            return;
        }
        BlockPos waterTarget = FengBoxClient.webFindWaterTarget(client, webLavaPos);
        if (waterTarget == null) {
            FengBoxClient.webReset();
            return;
        }
        BlockPos support = FengBoxClient.webFindSupport(client, waterTarget);
        if (support == null) {
            FengBoxClient.webReset();
            return;
        }
        Direction face = FengBoxClient.faceToward(support, waterTarget);
        if (!this.webTryInteract(client, Items.WATER_BUCKET, waterTarget, support, face, FengBoxClient.webFindFaceHitPoint(client, support, face))) {
            return;
        }
        FengBoxClient.webReset();
    }

    public void webTickGroundWebBreak(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || webFootPos == null) {
            FengBoxClient.webReset();
            return;
        }
        if (!webBreakStarted) {
            if (webBreakTicks < Math.max(1, autoWebDelay)) {
                ++webBreakTicks;
                return;
            }
            if (FengBoxClient.webFindSwordSlot(player) < 0) {
                FengBoxClient.webReset();
                return;
            }
            webBreakStarted = true;
            webBreakTicks = 0;
            return;
        }
        if (webBreakTicks < Math.max(1, autoWebDelay)) {
            ++webBreakTicks;
            return;
        }
        webBreakTicks = 0;
        int swordSlot = FengBoxClient.webFindSwordSlot(player);
        if (swordSlot < 0) {
            FengBoxClient.webReset();
            return;
        }
        Vec3d center = new Vec3d((double)webFootPos.getX() + 0.5, (double)webFootPos.getY() + 0.5, (double)webFootPos.getZ() + 0.5);
        FengBoxClient.webRotateTo(client, center);
        if (Math.abs(FengBoxClient.angleDiff(player.getYaw(), FengBoxClient.webYawTo(player, center))) > 15.0f) {
            return;
        }
        player.getInventory().setSelectedSlot(swordSlot);
        client.interactionManager.attackBlock(webFootPos, Direction.UP);
        player.swingHand(Hand.MAIN_HAND);
        FengBoxClient.webReset();
    }

    private boolean webTryInteract(MinecraftClient client, Item item, BlockPos place, BlockPos support, Direction face, Vec3d aim) {
        ClientPlayerEntity player = client.player;
        if (player == null || aim == null) {
            return false;
        }
        int slot = FengBoxClient.webFindSlot(player, item);
        if (slot < 0) {
            return false;
        }
        if (player.squaredDistanceTo((double)place.getX() + 0.5, (double)place.getY() + 0.5, (double)place.getZ() + 0.5) > 49.0) {
            return false;
        }
        FengBoxClient.webRotateTo(client, aim);
        if (Math.abs(FengBoxClient.angleDiff(player.getYaw(), FengBoxClient.webYawTo(player, aim))) > 12.0f) {
            return false;
        }
        player.getInventory().setSelectedSlot(slot);
        BlockHitResult hit = new BlockHitResult(aim, face, support, false);
        client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
        player.swingHand(Hand.MAIN_HAND);
        return true;
    }

    public static void webRotateTo(MinecraftClient client, Vec3d point) {
        ClientPlayerEntity player = client.player;
        if (player == null || point == null) {
            return;
        }
        Vec3d eye = player.getEyePos();
        Vec3d diff = point.subtract(eye);
        float yaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
        float pitch = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))))), (float)-90.0f, (float)90.0f);
        FengBoxClient.rotateSmoothly(player, yaw, pitch, autoWebSpeed, 1);
    }

    public static float webYawTo(ClientPlayerEntity player, Vec3d point) {
        Vec3d eye = player.getEyePos();
        Vec3d diff = point.subtract(eye);
        return (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
    }

    public static int webFindSlot(ClientPlayerEntity player, Item item) {
        if (player == null) {
            return -1;
        }
        for (int i = 0; i < 9; ++i) {
            if (player.getInventory().getStack(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int webFindSwordSlot(ClientPlayerEntity player) {
        if (player == null) {
            return -1;
        }
        for (int i = 0; i < 9; ++i) {
            if (!(player.getInventory().getStack(i).getItem() instanceof AxeItem)) continue; // TODO: SwordItem removed
            return i;
        }
        return -1;
    }

    public static boolean webShouldPlaceLava(MinecraftClient client, Entity entity, boolean inCobweb) {
        boolean inWeb;
        if (!autoWebLava) {
            return false;
        }
        if (entity.isOnFire()) {
            return false;
        }
        boolean bl = inWeb = inCobweb && client.world.getBlockState(entity.getBlockPos()).getBlock() == Blocks.COBWEB;
        if (!inWeb && !FengBoxClient.webIsEntityGrounded(entity)) {
            return false;
        }
        return inWeb || !FengBoxClient.webIsEntityMoving(entity);
    }

    public static BlockPos webPickLavaPos(MinecraftClient client, Entity entity) {
        BlockPos head = new BlockPos((int)Math.floor(entity.getX()), (int)Math.floor(entity.getY() + 1.0), (int)Math.floor(entity.getZ()));
        if (FengBoxClient.webCanPlaceLavaAt(client, entity, head)) {
            return head;
        }
        Box box = entity.getBoundingBox();
        int minX = (int)Math.floor(box.minX - 0.45);
        int maxX = (int)Math.floor(box.maxX + 0.45);
        int minZ = (int)Math.floor(box.minZ - 0.45);
        int maxZ = (int)Math.floor(box.maxZ + 0.45);
        int headY = (int)Math.floor(entity.getY() + 1.0);
        int topY = (int)Math.floor(box.maxY - 0.05);
        int yLo = Math.min(headY, topY);
        int yHi = Math.max(headY, topY);
        for (int y = yLo; y <= yHi; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!FengBoxClient.webCanPlaceLavaAt(client, entity, p)) continue;
                    return p;
                }
            }
        }
        return null;
    }

    public static BlockPos webPickLavaGround(MinecraftClient client, Entity entity) {
        Vec3d end;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return null;
        }
        Vec3d start = new Vec3d(entity.getX(), entity.getY() + 0.2, entity.getZ());
        BlockHitResult hit = client.world.raycast(new RaycastContext(start, end = new Vec3d(entity.getX(), entity.getY() - 2.2, entity.getZ()), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)player));
        if (hit == null || hit.getType() != HitResult.Type.BLOCK || hit.getSide() != Direction.UP) {
            return null;
        }
        BlockPos support = hit.getBlockPos();
        BlockPos lavaPos = support.up();
        if (!FengBoxClient.webIsBlockReplaceable(client, lavaPos)) {
            return null;
        }
        if (FengBoxClient.webFindFaceHitPoint(client, support, Direction.UP) == null) {
            return null;
        }
        return lavaPos;
    }

    public static boolean webCanPlaceLavaAt(MinecraftClient client, Entity entity, BlockPos place) {
        if (!FengBoxClient.webIsBlockReplaceable(client, place)) {
            return false;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        Vec3d eye = player.getEyePos();
        Direction nearest = Direction.getFacing((float)((float)(entity.getX() - eye.x)), (float)0.0f, (float)((float)(entity.getZ() - eye.z)));
        ArrayList<Direction> dirs = new ArrayList<Direction>();
        dirs.add(nearest);
        for (Direction d : Direction.Type.HORIZONTAL) {
            if (d == nearest) continue;
            dirs.add(d);
        }
        dirs.add(Direction.DOWN);
        dirs.add(Direction.UP);
        for (Direction d : dirs) {
            BlockPos support = place.offset(d);
            Direction face = d.getOpposite();
            if (!client.world.getBlockState(support).isSolidBlock((BlockView)client.world, support) || FengBoxClient.webFindFaceHitPoint(client, support, face) == null) continue;
            return true;
        }
        return false;
    }

    public static boolean webIntersectsPlayer(MinecraftClient client, BlockPos place) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        return player.getBoundingBox().intersects(new Box(place));
    }

    public static boolean webIsBlockReplaceable(MinecraftClient client, BlockPos place) {
        if (!client.world.getBlockState(place).isAir()) {
            return false;
        }
        if (!client.world.getFluidState(place).isEmpty()) {
            return false;
        }
        return !FengBoxClient.webIntersectsPlayer(client, place);
    }

    public static Vec3d webEntityMovement(Entity entity) {
        return new Vec3d(entity.getX() - entity.lastRenderX, 0.0, entity.getZ() - entity.lastRenderZ);
    }

    public static boolean webIsEntityMoving(Entity entity) {
        Vec3d m = FengBoxClient.webEntityMovement(entity);
        return m.x * m.x + m.z * m.z >= 0.01;
    }

    public static boolean webIsEntityGrounded(Entity entity) {
        double yFrac = entity.getY() - Math.floor(entity.getY());
        double yDist = Math.min(1.0 - yFrac, yFrac);
        if (entity.isOnGround()) {
            return true;
        }
        return yDist < 0.08 && Math.abs(entity.getVelocity().y) < 0.12;
    }

    public static boolean webCanPlaceGroundWeb(Entity entity) {
        return FengBoxClient.webIsEntityGrounded(entity) && !FengBoxClient.webIsEntityMoving(entity);
    }

    public static BlockPos webFindSupport(MinecraftClient client, BlockPos place) {
        if (client.world.getBlockState(place.down()).isSolidBlock((BlockView)client.world, place.down())) {
            return place.down();
        }
        for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos n = place.offset(d);
            if (!client.world.getBlockState(n).isSolidBlock((BlockView)client.world, n)) continue;
            return n;
        }
        return null;
    }

    public static BlockPos webFindWaterTarget(MinecraftClient client, BlockPos lavaPos) {
        BlockPos[] candidates;
        for (BlockPos c : candidates = new BlockPos[]{lavaPos.up(), lavaPos.north(), lavaPos.south(), lavaPos.east(), lavaPos.west()}) {
            if (!client.world.getBlockState(c).isAir()) continue;
            return c;
        }
        return null;
    }

    public static Direction faceToward(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (dy > 0) {
            return Direction.UP;
        }
        if (dy < 0) {
            return Direction.DOWN;
        }
        if (dx > 0) {
            return Direction.EAST;
        }
        if (dx < 0) {
            return Direction.WEST;
        }
        if (dz > 0) {
            return Direction.SOUTH;
        }
        if (dz < 0) {
            return Direction.NORTH;
        }
        return Direction.UP;
    }

    public static Vec3d webFaceCenter(BlockPos support, Direction face) {
        return new Vec3d((double)support.getX() + 0.5 + (double)face.getOffsetX() * 0.5, (double)support.getY() + 0.5 + (double)face.getOffsetY() * 0.5, (double)support.getZ() + 0.5 + (double)face.getOffsetZ() * 0.5);
    }

    public static Box webExtremeFaceBox(MinecraftClient client, BlockPos pos, Direction face) {
        VoxelShape shape = client.world.getBlockState(pos).getOutlineShape((BlockView)client.world, pos, ShapeContext.absent());
        if (shape.isEmpty()) {
            return null;
        }
        List<Box> boxes = shape.getBoundingBoxes();
        if (boxes.isEmpty()) {
            return null;
        }
        Box best = (Box)boxes.get(0);
        for (int i = 1; i < boxes.size(); ++i) {
            Box b = (Box)boxes.get(i);
            if (!(FengBoxClient.webFaceExtreme(b, face) > FengBoxClient.webFaceExtreme(best, face))) continue;
            best = b;
        }
        return best;
    }

    public static double webFaceExtreme(Box b, Direction face) {
        switch (face) {
            case UP: {
                return b.maxY;
            }
            case DOWN: {
                return b.minY;
            }
            case NORTH: {
                return b.minZ;
            }
            case SOUTH: {
                return b.maxZ;
            }
            case WEST: {
                return b.minX;
            }
        }
        return b.maxX;
    }

    public static Vec3d webFaceBoxPoint(BlockPos pos, Box b, Direction face, double u, double v) {
        double x = 0.0;
        double y = 0.0;
        return new Vec3d(x, y, switch (face) {
            case Direction.UP -> {
                x = (double)pos.getX() + FengBoxClient.webLerp(b.minX, b.maxX, u);
                y = (double)pos.getY() + b.maxY;
                yield (double)pos.getZ() + FengBoxClient.webLerp(b.minZ, b.maxZ, v);
            }
            case Direction.DOWN -> {
                x = (double)pos.getX() + FengBoxClient.webLerp(b.minX, b.maxX, u);
                y = (double)pos.getY() + b.minY;
                yield (double)pos.getZ() + FengBoxClient.webLerp(b.minZ, b.maxZ, v);
            }
            case Direction.NORTH -> {
                x = (double)pos.getX() + FengBoxClient.webLerp(b.minX, b.maxX, u);
                y = (double)pos.getY() + FengBoxClient.webLerp(b.minY, b.maxY, v);
                yield (double)pos.getZ() + b.minZ;
            }
            case Direction.SOUTH -> {
                x = (double)pos.getX() + FengBoxClient.webLerp(b.minX, b.maxX, u);
                y = (double)pos.getY() + FengBoxClient.webLerp(b.minY, b.maxY, v);
                yield (double)pos.getZ() + b.maxZ;
            }
            case Direction.WEST -> {
                x = (double)pos.getX() + b.minX;
                y = (double)pos.getY() + FengBoxClient.webLerp(b.minY, b.maxY, u);
                yield (double)pos.getZ() + FengBoxClient.webLerp(b.minZ, b.maxZ, v);
            }
            default -> {
                x = (double)pos.getX() + b.maxX;
                y = (double)pos.getY() + FengBoxClient.webLerp(b.minY, b.maxY, u);
                yield (double)pos.getZ() + FengBoxClient.webLerp(b.minZ, b.maxZ, v);
            }
        });
    }

    public static double webLerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static List<Vec3d> webGetFacePoints(MinecraftClient client, BlockPos pos, Direction face) {
        double[] f;
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        Box faceBox = FengBoxClient.webExtremeFaceBox(client, pos, face);
        if (faceBox == null) {
            list.add(FengBoxClient.webFaceCenter(pos, face));
            return list;
        }
        FengBoxClient.webAddUniquePoint(list, FengBoxClient.webFaceBoxPoint(pos, faceBox, face, 0.5, 0.5));
        for (double u : f = new double[]{0.5, 0.25, 0.75}) {
            for (double v : f) {
                FengBoxClient.webAddUniquePoint(list, FengBoxClient.webFaceBoxPoint(pos, faceBox, face, u, v));
            }
        }
        return list;
    }

    public static void webAddUniquePoint(List<Vec3d> list, Vec3d point) {
        for (Vec3d e : list) {
            if (!(e.squaredDistanceTo(point) < 1.0E-6)) continue;
            return;
        }
        list.add(point);
    }

    public static Vec3d webRaycastFace(MinecraftClient client, BlockPos pos, Direction face, Vec3d aim) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return null;
        }
        Vec3d offset = aim.add((double)(-face.getOffsetX()) * 0.01, (double)(-face.getOffsetY()) * 0.01, (double)(-face.getOffsetZ()) * 0.01);
        BlockHitResult hit = client.world.raycast(new RaycastContext(player.getEyePos(), offset, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)player));
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        if (!hit.getBlockPos().equals((Object)pos) || hit.getSide() != face) {
            return null;
        }
        return hit.getPos();
    }

    public static Vec3d webFindFaceHitPoint(MinecraftClient client, BlockPos pos, Direction face) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return null;
        }
        for (Vec3d p : FengBoxClient.webGetFacePoints(client, pos, face)) {
            Vec3d hit;
            if (player.squaredDistanceTo(p) > 49.0 || (hit = FengBoxClient.webRaycastFace(client, pos, face, p)) == null) continue;
            return hit;
        }
        return null;
    }

    public static boolean webIsAllCobweb(MinecraftClient client, Box box) {
        int minX = (int)Math.floor(box.minX);
        int maxX = (int)Math.floor(box.maxX);
        int minY = (int)Math.floor(box.minY);
        int maxY = (int)Math.floor(box.maxY);
        int minZ = (int)Math.floor(box.minZ);
        int maxZ = (int)Math.floor(box.maxZ);
        int count = 0;
        int web = 0;
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!box.intersects(new Box(p))) continue;
                    ++count;
                    if (client.world.getBlockState(p).getBlock() != Blocks.COBWEB) continue;
                    ++web;
                }
            }
        }
        return count > 0 && web == count;
    }

    public static List<BlockPos[]> webPositionsForAabb(MinecraftClient client, Box box) {
        int minX = (int)Math.floor(box.minX);
        int maxX = (int)Math.floor(box.maxX);
        int minY = (int)Math.floor(box.minY);
        int maxY = (int)Math.floor(box.maxY);
        int minZ = (int)Math.floor(box.minZ);
        int maxZ = (int)Math.floor(box.maxZ);
        int footY = (int)Math.floor(box.minY + 0.001);
        double centerX = (box.minX + box.maxX) * 0.5;
        double centerZ = (box.minZ + box.maxZ) * 0.5;
        ArrayList<BlockPos[]> list = new ArrayList<BlockPos[]>();
        HashSet<BlockPos> seen = new HashSet<BlockPos>();
        BlockPos center = new BlockPos((int)Math.floor(centerX), footY, (int)Math.floor(centerZ));
        if (FengBoxClient.webBoxNotCobweb(client, box, center)) {
            seen.add(center);
            list.add(new BlockPos[]{center, center.up()});
        }
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    int chosenY;
                    BlockPos f;
                    BlockPos p2 = new BlockPos(x, y, z);
                    if (!box.intersects(new Box(p2)) || client.world.getBlockState(p2).getBlock() == Blocks.COBWEB || !seen.add(f = new BlockPos(x, chosenY = y <= footY ? footY : y - 1, z)) || client.world.getBlockState(f).getBlock() == Blocks.COBWEB || client.world.getBlockState(f.up()).getBlock() == Blocks.COBWEB) continue;
                    list.add(new BlockPos[]{f, f.up()});
                }
            }
        }
        list.sort(Comparator.comparingDouble(p -> {
            double dx = (double)p[0].getX() + 0.5 - centerX;
            double dy = p[0].getY() - footY;
            double dz = (double)p[0].getZ() + 0.5 - centerZ;
            return dx * dx + dy * dy + dz * dz;
        }));
        return list;
    }

    public static boolean webBoxNotCobweb(MinecraftClient client, Box box, BlockPos pos) {
        return box.intersects(new Box(pos)) && client.world.getBlockState(pos).getBlock() != Blocks.COBWEB;
    }

    public static void webReset() {
        webPhase = 0;
        webWaitTicks = 0;
        webRetrieveTicks = 0;
        webRetrieveAttempts = 0;
        webSourceWaitCount = 0;
        webLavaReapplyCount = 0;
        webLavaPlaced = false;
        webRetriedLavaPlace = false;
        webPlaceSent = false;
        webBreakTicks = 0;
        webBreakStarted = false;
        webBreakCooldown = 0;
        webTarget = null;
        webPlacePos = null;
        webSupportPos = null;
        webAimPoint = null;
        webPlaceType = 0;
        webFootPos = null;
        webLavaPos = null;
        webSecondWebPending = false;
        webGroundBreakTarget = null;
    }




    public void handleAutoAnchor(MinecraftClient client) {
        if (!autoAnchorEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null || !player.isAlive() || freecamEnabled) {
            anchorStep = 0;
            anchorPos = null;
            return;
        }
        if (client.world.getRegistryKey() != World.NETHER) {
            anchorStep = 0;
            anchorPos = null;
            return;
        }
        if (anchorCooldown > 0) {
            --anchorCooldown;
            return;
        }
        Entity target = this.findBestTarget(client);
        if (target == null) {
            anchorStep = 0;
            anchorPos = null;
            anchorRetries = 0;
            return;
        }
        int anchorSlot = -1;
        int glowSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.RESPAWN_ANCHOR) {
                anchorSlot = i;
            }
            if (stack.getItem() != Items.GLOWSTONE) continue;
            glowSlot = i;
        }
        switch (anchorStep) {
            case 0: {
                BlockPos targetPos;
                BlockPos validPos;
                if (anchorPos != null) {
                    if (client.world.getBlockState(anchorPos).getBlock() == Blocks.RESPAWN_ANCHOR && player.squaredDistanceTo((double)anchorPos.getX() + 0.5, (double)anchorPos.getY() + 0.5, (double)anchorPos.getZ() + 0.5) <= (double)(autoAnchorRange * autoAnchorRange)) {
                        anchorRetries = 0;
                        anchorStep = 1;
                        break;
                    }
                    anchorPos = null;
                }
                if ((validPos = this.findAnchorPlacePos(client, targetPos = target.getBlockPos())) == null) {
                    anchorCooldown = 5;
                    break;
                }
                anchorPos = validPos;
                if (client.world.getBlockState(validPos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                    anchorRetries = 0;
                    anchorStep = 1;
                    break;
                }
                if (anchorSlot < 0) {
                    anchorCooldown = 5;
                    break;
                }
                player.getInventory().setSelectedSlot(anchorSlot);
                BlockHitResult hit = new BlockHitResult(new Vec3d((double)validPos.getX() + 0.5, (double)validPos.getY(), (double)validPos.getZ() + 0.5), Direction.UP, validPos.down(), false);
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                player.swingHand(Hand.MAIN_HAND);
                anchorRetries = 0;
                anchorCooldown = autoAnchorPlaceDelay;
                anchorStep = 1;
                break;
            }
            case 1: {
                if (anchorPos != null && glowSlot >= 0 && client.world.getBlockState(anchorPos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                    player.getInventory().setSelectedSlot(glowSlot);
                    BlockHitResult hit = new BlockHitResult(new Vec3d((double)anchorPos.getX() + 0.5, (double)anchorPos.getY() + 0.5, (double)anchorPos.getZ() + 0.5), Direction.UP, anchorPos, false);
                    client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                    player.swingHand(Hand.MAIN_HAND);
                    anchorCooldown = 2;
                    anchorStep = 2;
                    break;
                }
                if (++anchorRetries < 40) {
                    anchorCooldown = 1;
                    break;
                }
                anchorStep = 0;
                anchorPos = null;
                anchorRetries = 0;
                break;
            }
            case 2: {
                if (anchorPos != null && client.world.getBlockState(anchorPos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                    int detSlot = anchorSlot >= 0 ? anchorSlot : -1;
                    if (!autoAnchorSuicide && player.getHealth() - this.selfAnchorDamage(player, anchorPos) < autoAnchorMinHealth) {
                        anchorCooldown = 5;
                        break;
                    }
                    // Place blast-shield block between player and anchor to reduce self-damage
                    if (!autoAnchorSuicide) {
                        BlockPos shieldPos = this.findBlastShieldPos(client, player, anchorPos);
                        if (shieldPos != null) {
                            int shieldSlot = this.findShieldBlockSlot(player);
                            if (shieldSlot >= 0) {
                                player.getInventory().setSelectedSlot(shieldSlot);
                                BlockHitResult shieldHit = new BlockHitResult(
                                    new Vec3d(shieldPos.getX() + 0.5, shieldPos.getY(), shieldPos.getZ() + 0.5),
                                    Direction.UP, shieldPos.down(), false);
                                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, shieldHit);
                                player.swingHand(Hand.MAIN_HAND);
                                anchorCooldown = 2;
                                break;
                            }
                        }
                    }
                    if (detSlot < 0) {
                        for (int i = 0; i < 9; ++i) {
                            ItemStack s = player.getInventory().getStack(i);
                            // Only exclude glowstone (which charges); respawn anchor itself can detonate
                            if (!s.isEmpty() && s.getItem() == Items.GLOWSTONE) continue;
                            detSlot = i;
                            break;
                        }
                    }
                    if (detSlot < 0) {
                        anchorCooldown = 2;
                        break;
                    }
                    player.getInventory().setSelectedSlot(detSlot);
                    BlockHitResult hit = new BlockHitResult(new Vec3d((double)anchorPos.getX() + 0.5, (double)anchorPos.getY() + 0.5, (double)anchorPos.getZ() + 0.5), Direction.UP, anchorPos, false);
                    client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                    player.swingHand(Hand.MAIN_HAND);
                    anchorCooldown = autoAnchorBreakDelay;
                    anchorStep = 0;
                    anchorPos = null;
                    anchorRetries = 0;
                    break;
                }
                anchorStep = 0;
                anchorPos = null;
                anchorRetries = 0;
            }
        }
        if (autoAnchorRotMode != 2 && anchorPos != null) {
            Vec3d eyePos = player.getEyePos();
            Vec3d lookTarget = new Vec3d((double)anchorPos.getX() + 0.5, (double)anchorPos.getY() + 0.5, (double)anchorPos.getZ() + 0.5);
            Vec3d diff = lookTarget.subtract(eyePos);
            float tYaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
            float tPitch = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))))), (float)-90.0f, (float)90.0f);
            this.rotateSmoothly(player, tYaw, tPitch, 20.0f, autoAnchorRotMode);
        }
    }

    public static float selfAnchorDamage(ClientPlayerEntity player, BlockPos pos) {
        double dist = new Vec3d(player.getX(), player.getY(), player.getZ()).distanceTo(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
        float factor = (float)(1.0 - dist / 10.0);
        if (factor <= 0.0f) {
            return 0.0f;
        }
        return 97.5f * factor;
    }

    public static BlockPos findAnchorPlacePos(MinecraftClient client, BlockPos targetPos) {
        BlockPos[] candidates;
        for (BlockPos pos : candidates = new BlockPos[]{targetPos.down(), targetPos.up(2), targetPos.north(), targetPos.south(), targetPos.east(), targetPos.west(), targetPos.north().east(), targetPos.north().west(), targetPos.south().east(), targetPos.south().west()}) {
            if (client.player == null || client.player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > (double)(autoAnchorRange * autoAnchorRange) || client.world == null) continue;
            BlockState st = client.world.getBlockState(pos);
            if (st.getBlock() == Blocks.RESPAWN_ANCHOR) {
                return pos;
            }
            if (!st.isAir() || !client.world.getBlockState(pos.down()).isSolidBlock((BlockView)client.world, pos.down())) continue;
            return pos;
        }
        return null;
    }

    public static BlockPos findBlastShieldPos(MinecraftClient client, ClientPlayerEntity player, BlockPos anchorPos) {
        // Try to place a block between player and anchor to shield from blast
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d anchorCenter = new Vec3d(anchorPos.getX() + 0.5, anchorPos.getY() + 0.5, anchorPos.getZ() + 0.5);
        Vec3d dir = playerPos.subtract(anchorCenter).normalize();
        BlockPos shieldPos = BlockPos.ofFloored(playerPos.subtract(dir.multiply(1.5)));
        if (client.world != null && client.world.getBlockState(shieldPos).isAir()
            && client.world.getBlockState(shieldPos.down()).isSolidBlock((BlockView) client.world, shieldPos.down())) {
            return shieldPos;
        }
        // Fallback: check surrounding
        for (BlockPos offset : new BlockPos[]{ shieldPos.north(), shieldPos.south(), shieldPos.east(), shieldPos.west(), shieldPos.up(), shieldPos.down() }) {
            if (client.world != null && client.world.getBlockState(offset).isAir()
                && client.world.getBlockState(offset.down()).isSolidBlock((BlockView) client.world, offset.down())) {
                return offset;
            }
        }
        return null;
    }

    public static int findShieldBlockSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; ++i) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            Item item = s.getItem();
            if (item == Items.OBSIDIAN || item == Items.COBBLESTONE || item == Items.STONE
                || item == Items.DIRT || item == Items.NETHERRACK || item == Items.COBBLED_DEEPSLATE) {
                return i;
            }
        }
        return -1;
    }

    public static BlockPos findCrystalPlacePos(MinecraftClient client, BlockPos targetPos) {
        BlockPos[] candidates;
        for (BlockPos pos : candidates = new BlockPos[]{targetPos.north(), targetPos.south(), targetPos.east(), targetPos.west(), targetPos.north().east(), targetPos.north().west(), targetPos.south().east(), targetPos.south().west(), targetPos.north(2), targetPos.south(2), targetPos.east(2), targetPos.west(2)}) {
            if (client.player == null || client.player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > 36.0 || client.world == null || !client.world.getBlockState(pos).isAir() || !client.world.getBlockState(pos.down()).isSolidBlock((BlockView)client.world, pos.down())) continue;
            return pos;
        }
        for (BlockPos pos : candidates) {
            if (client.player == null || client.player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > 36.0 || client.world == null || client.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN || !client.world.getBlockState(pos.up()).isAir()) continue;
            return pos;
        }
        return null;
    }

    public void trackNearestPlayer(MinecraftClient client) {
        if (!playerTrackerEnabled || client.world == null || client.player == null) {
            this.trackedPlayer = null;
            return;
        }
        double minDist = Double.MAX_VALUE;
        LivingEntity nearest = null;
        for (PlayerEntity p : client.world.getPlayers()) {
            double dist;
            if (p == client.player || !((dist = (double)client.player.distanceTo((Entity)p)) < minDist)) continue;
            minDist = dist;
            nearest = p;
        }
        for (Entity entity : client.world.getEntities()) {
            double dist;
            String key;
            if (entity == client.player || entity instanceof PlayerEntity || !(entity instanceof LivingEntity) || !(key = entity.getType().getTranslationKey()).contains("fake_player") && !key.contains("fakeplayer") || !((dist = (double)client.player.distanceTo(entity)) < minDist)) continue;
            minDist = dist;
            nearest = (LivingEntity)entity;
        }
        this.trackedPlayer = nearest;
    }

    public void handleAutoTotem(MinecraftClient client) {
        if (!autoTotemEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        if (autoTotemCooldown > 0) {
            return;
        }
        if (player.getHealth() > 10.0f) {
            return;
        }
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        int selected = player.getInventory().getSelectedSlot();
        int totemSlot = -1;
        for (int i = 0; i < 36; ++i) {
            if (i == selected || player.getInventory().getStack(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
            totemSlot = i;
            break;
        }
        if (totemSlot < 0 && player.getInventory().getStack(selected).getItem() == Items.TOTEM_OF_UNDYING) {
            totemSlot = selected;
        }
        if (totemSlot < 0) {
            return;
        }
        int containerSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
        int offhandSlot = 45;
        int syncId = player.playerScreenHandler.syncId;
        client.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)player);
        client.interactionManager.clickSlot(syncId, offhandSlot, 0, SlotActionType.PICKUP, (PlayerEntity)player);
        client.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)player);
        autoTotemCooldown = 10;
    }

    public void handleAutoBridge(MinecraftClient client) {
        int fwdZ;
        int fwdX;
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (!autoBridgeEnabled || client.currentScreen != null || !player.isAlive() || freecamEnabled) {
            bridgePreviewPos = null;
            return;
        }
        float yaw = player.getYaw();
        float yawRad = yaw * ((float)Math.PI / 180);
        double mvF = player.input.playerInput.forward() ? 1.0 : (player.input.playerInput.backward() ? -1.0 : 0.0);
        double mvS = player.input.playerInput.right() ? 1.0 : (player.input.playerInput.left() ? -1.0 : 0.0);
        double mvLen = Math.hypot(mvF, mvS);
        if (mvLen > 0.01) {
            double wX = (mvF /= mvLen) * (double)(-MathHelper.sin((float)yawRad)) + (mvS /= mvLen) * (double)MathHelper.cos((float)yawRad);
            double wZ = mvF * (double)MathHelper.cos((float)yawRad) - mvS * (double)MathHelper.sin((float)yawRad);
            fwdX = (int)Math.round(wX);
            fwdZ = (int)Math.round(wZ);
            if (fwdX == 0 && fwdZ == 0) {
                fwdX = Math.round(-MathHelper.sin((float)yawRad));
                fwdZ = Math.round(MathHelper.cos((float)yawRad));
            }
        } else {
            fwdX = Math.round(-MathHelper.sin((float)yawRad));
            fwdZ = Math.round(MathHelper.cos((float)yawRad));
        }
        if (fwdX == 0 && fwdZ == 0) {
            fwdZ = 1;
        }
        boolean diagonal = fwdX != 0 && fwdZ != 0;
        BlockPos feet = player.getBlockPos();
        BlockPos frontDown = feet.add(fwdX, -1, fwdZ);
        BlockPos frontDown2 = frontDown.add(fwdX, 0, fwdZ);
        BlockPos frontDown3 = frontDown2.add(fwdX, 0, fwdZ);
        boolean frontAir = client.world.getBlockState(frontDown).isAir();
        boolean front2Air = client.world.getBlockState(frontDown2).isAir();
        boolean belowAir = client.world.getBlockState(feet.down()).isAir();
        ArrayList<BlockPos> targetList = new ArrayList<BlockPos>();
        if (!player.isOnGround() && belowAir) {
            targetList.add(feet.down());
        }
        if (diagonal) {
            targetList.add(feet.add(fwdX, -1, 0));
            targetList.add(feet.add(0, -1, fwdZ));
            targetList.add(frontDown);
            targetList.add(frontDown.add(fwdX, -1, 0));
            targetList.add(frontDown.add(0, -1, fwdZ));
            targetList.add(frontDown2);
        } else {
            targetList.add(frontDown);
            targetList.add(frontDown2);
            targetList.add(frontDown3);
        }
        BlockPos[] targets = targetList.toArray(new BlockPos[0]);
        if (frontAir || front2Air) {
            player.input.playerInput = new net.minecraft.util.PlayerInput(player.input.playerInput.forward(), player.input.playerInput.backward(), player.input.playerInput.left(), player.input.playerInput.right(), player.input.playerInput.jump(), true, player.input.playerInput.sprint());
        }
        boolean placed = false;
        int slot = -1;
        int emptySlot = -1;
        int bestCount = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) {
                if (emptySlot >= 0) continue;
                emptySlot = i;
                continue;
            }
            if (!(stack.getItem() instanceof BlockItem) || stack.getCount() <= autoBridgeDoNotUseBelow || stack.getCount() <= bestCount) continue;
            bestCount = stack.getCount();
            slot = i;
        }
        if (slot < 0) {
            int fallbackCount = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!(stack.getItem() instanceof BlockItem) || stack.getCount() <= fallbackCount) continue;
                fallbackCount = stack.getCount();
                slot = i;
            }
        }
        if (slot < 0 && emptySlot >= 0) {
            int invSlot = -1;
            for (int i = 9; i < 36; ++i) {
                if (!(player.getInventory().getStack(i).getItem() instanceof BlockItem)) continue;
                invSlot = i;
                break;
            }
            if (invSlot >= 0) {
                int containerSource = invSlot - 9;
                int containerDest = emptySlot + 27;
                int syncId = player.playerScreenHandler.syncId;
                client.interactionManager.clickSlot(syncId, containerSource, 0, SlotActionType.PICKUP, (PlayerEntity)player);
                client.interactionManager.clickSlot(syncId, containerDest, 0, SlotActionType.PICKUP, (PlayerEntity)player);
            }
        }
        if (autoBridgeTower && client.options.jumpKey.isPressed()) {
            BlockPos towerBelow;
            if (player.isOnGround()) {
                player.jump();
            }
            if (client.world.getBlockState(towerBelow = player.getBlockPos().down()).isAir() && slot >= 0) {
                bridgePreviewPos = towerBelow;
                player.getInventory().setSelectedSlot(slot);
                this.tryPlaceBlockAt(client, player, towerBelow);
            }
            return;
        }
        if (slot >= 0) {
            player.getInventory().setSelectedSlot(slot);
            bridgePreviewPos = null;
            for (BlockPos bp : targets) {
                if (!this.tryPlaceBlockAt(client, player, bp)) continue;
                placed = true;
                if (bridgePreviewPos != null) continue;
                bridgePreviewPos = bp;
            }
        }
        if ((bridgeYaw = (float)Math.toDegrees(Math.atan2(-fwdX, fwdZ))) < 0.0f) {
            bridgeYaw += 360.0f;
        }
        if (placed) {
            if (bridgePlaceTicks > 10) {
                bridgeUserPitch = player.getPitch();
            }
            bridgePlaceTicks = 0;
        } else {
            ++bridgePlaceTicks;
        }
        float targetPitch = bridgePlaceTicks <= 10 ? 45.0f : (bridgePlaceTicks <= 40 ? bridgeUserPitch : player.getPitch());
        if (autoBridgeRotMode != 2) {
            this.rotateSmoothly(player, bridgeYaw, targetPitch, 14.0f, autoBridgeRotMode);
            if (autoBridgeRotMode == 0) {
                float pDiff = targetPitch - player.getPitch();
                if (Math.abs(pDiff) <= 15.0f) {
                    player.setPitch(targetPitch);
                } else {
                    player.setPitch(player.getPitch() + MathHelper.clamp((float)pDiff, (float)-15.0f, (float)15.0f));
                }
            }
        }
        float packetYaw = autoBridgeRotMode == 1 ? bridgeYaw : player.getYaw();
        float packetPitch = player.getPitch();
        NetworkHelper.sendLookAndOnGround(player, packetYaw, packetPitch, player.isOnGround());
    }

    public boolean tryPlaceBlockAt(MinecraftClient client, ClientPlayerEntity player, BlockPos bp) {
        if (!client.world.getBlockState(bp).isAir()) {
            return false;
        }
        for (int pass = 0; pass < 2; ++pass) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = bp.offset(dir);
                BlockState neighborState = client.world.getBlockState(neighbor);
                if (neighborState.isAir() || pass == 0 && !neighborState.isSolidBlock((BlockView)client.world, neighbor)) continue;
                Direction face = dir.getOpposite();
                Vec3d hitPos = Vec3d.ofCenter((Vec3i)neighbor).add((double)face.getOffsetX() * 0.5, (double)face.getOffsetY() * 0.5, (double)face.getOffsetZ() * 0.5);
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(hitPos, face, neighbor, false));
                player.swingHand(Hand.MAIN_HAND);
                return true;
            }
        }
        return false;
    }

    public void handleSpin(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (spinEnabled && !autoBridgeEnabled) {
            if ((spinYaw += 15.0f) > 360.0f) {
                spinYaw -= 360.0f;
            }
            player.bodyYaw = spinYaw;
            player.headYaw = spinYaw;
            player.lastBodyYaw = spinYaw;
            player.lastHeadYaw = spinYaw;
            NetworkHelper.sendLookAndOnGround(player, spinYaw, player.getPitch(), player.isOnGround());
        }
    }

    public static boolean canCriticalHit(ClientPlayerEntity player, boolean ignoreOnGround) {
        if (flightEnabled) {
            return false;
        }
        if (player.isUsingItem()) {
            return false;
        }
        if (player.hasVehicle()) {
            return false;
        }
        if (player.isGliding()) {
            return false;
        }
        if (player.isTouchingWater() || player.isSubmergedInWater() || player.isInLava()) {
            return false;
        }
        if (player.isClimbing() && !ignoreOnGround) {
            return false;
        }
        if (player.isInsideWall()) {
            return false;
        }
        if (player.getAbilities().flying) {
            return false;
        }
        if (player.hasStatusEffect(StatusEffects.BLINDNESS) || player.hasStatusEffect(StatusEffects.SLOWNESS) || player.hasStatusEffect(StatusEffects.LEVITATION)) {
            return false;
        }
        if (MinecraftClient.getInstance().world != null) {
            Box bb = player.getBoundingBox();
            int minX = MathHelper.floor((double)bb.minX);
            int maxX = MathHelper.ceil((double)bb.maxX);
            int minY = MathHelper.floor((double)bb.minY);
            int maxY = MathHelper.ceil((double)bb.maxY);
            int minZ = MathHelper.floor((double)bb.minZ);
            int maxZ = MathHelper.ceil((double)bb.maxZ);
            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        if (!(MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof StairsBlock)) continue;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void handleKillAura(MinecraftClient client) {
        LivingEntity le;
        if (!killAuraEnabled) {
            killAuraTarget = null;
            killAuraAttacks = 0.0f;
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        boolean invOpen = client.currentScreen instanceof InventoryScreen;
        if (client.currentScreen != null && (!killAuraIgnoreOpenInv || !invOpen) || !player.isAlive() || freecamEnabled || player.isUsingItem()) {
            killAuraTarget = null;
            return;
        }
        if (killAuraSwitchCooldown > 0) {
            --killAuraSwitchCooldown;
        }
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        float auraMax = reachEnabled ? Math.max(killAuraRange, reachEntityRange) : killAuraRange;
        currentKillAuraScanRange = auraMax + (float)(Math.random() * (double)killAuraScanExtra);
        Vec3d eyePos = player.getEyePos();
        for (Entity entity : client.world.getEntities()) {
            double dist;
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator() || (dist = eyePos.distanceTo(new Vec3d(entity.getX(), entity.getY(), entity.getZ()))) > (double)currentKillAuraScanRange || dist >= bestDist || killAuraCheckLineOfSight && !this.hasLineOfSight(client, player, eyePos, entity) && dist > (double)killAuraWallRange) continue;
            bestDist = dist;
            best = entity;
        }
        killAuraTarget = best;
        if (best == null) {
            return;
        }
        Vec3d targetPos = new Vec3d(best.getX(), best.getY(), best.getZ()).add(0.0, (double)best.getHeight() * 0.5, 0.0);
        Vec3d diff = targetPos.subtract(eyePos);
        float targetYaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
        float targetPitch = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))))), (float)-90.0f, (float)90.0f);
        this.rotateSmoothly(player, targetYaw, targetPitch, autoAimSpeed, autoAimRotMode);
        boolean critAttackNow = false;
        if (pendingCritJump) {
            if (player.isOnGround()) {
                return; // still on ground, wait for jump to take effect
            } else if (player.fallDistance > 0.0f && this.canCriticalHit(player, false)) {
                pendingCritJump = false;
                critAttackNow = true;
            } else {
                return;
            }
        }
        if (!critAttackNow) {
            if (!killAuraAuto) {
                float jitterMul;
                float cps = Math.max(1.0f, Math.min(20.0f, killAuraCps));
                if ((killAuraAttacks += cps * (jitterMul = killAuraClickJitter > 0.0f ? 1.0f + (float)(Math.random() * 2.0 - 1.0) * killAuraClickJitter : 1.0f) / 20.0f) < 1.0f) {
                    return;
                }
                killAuraAttacks -= 1.0f;
            } else {
                int elapsed;
                float cdPerTick = player.getAttackCooldownProgressPerTick();
                int cdTicks = !Float.isFinite(cdPerTick) || cdPerTick <= 0.5f ? 10 : Math.max(2, Math.round(cdPerTick));
                if (violentMode) {
                    cdTicks = !Float.isFinite(cdPerTick) || cdPerTick <= 0.0f ? 20 : Math.max(1, Math.round(1.0f / cdPerTick));
                }
                if ((elapsed = player.age - lastAuraAttackTick) >= 0 && elapsed < cdTicks) {
                    return;
                }
            }
        }
        boolean shieldTarget = best instanceof LivingEntity && (le = (LivingEntity)best).isBlocking();
        int desiredSlot = -1;
        if (shieldTarget && killAuraShieldBreak) {
            desiredSlot = InventoryUtils.findBestAxeSlot(player);
        } else if (violentMode) {
            desiredSlot = InventoryUtils.findBestDamageWeaponSlot(player);
        } else if (killAuraWeaponSwitch) {
            desiredSlot = InventoryUtils.findBestWeaponSlot(player);
        }
        boolean swappedNow = false;
        if (desiredSlot >= 0 && desiredSlot != player.getInventory().getSelectedSlot() && killAuraSwitchCooldown <= 0) {
            killAuraPrevSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(desiredSlot);
            killAuraSwitchCooldown = killAuraSwitchDelay;
            swappedNow = true;
        }
        if (killAuraOnlyWeapons && InventoryUtils.weaponScore(player.getMainHandStack()) <= 0.0f) {
            return;
        }
        int critMode = autoCritMode;
        if (violentMode && critMode == 0) {
            critMode = 2;
        }
        if ((autoCritEnabled || violentMode) && critMode != 0 && !critAttackNow && this.canCriticalHit(player, false)) {
            if (critMode == 1 && player.isOnGround()) {
                player.jump();
                pendingCritJump = true;
                return;
            }
            if (critMode == 2 && player.isOnGround()) {
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();
                NetworkHelper.sendCritPackets(player, x, y, z);
            }
        }
        client.interactionManager.attackEntity((PlayerEntity)player, best);
        player.swingHand(Hand.MAIN_HAND);
        lastAuraAttackTick = player.age;
        if (swappedNow && killAuraPrevSlot >= 0) {
            player.getInventory().setSelectedSlot(killAuraPrevSlot);
            killAuraPrevSlot = -1;
        }
    }

    public void handleAutoRod(MinecraftClient client) {
        if (!autoRodEnabled) {
            autoRodTarget = null;
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null || !player.isAlive() || freecamEnabled || player.isUsingItem()) {
            autoRodTarget = null;
            return;
        }
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        Vec3d eyePos = player.getEyePos();
        for (Entity entity : client.world.getEntities()) {
            double dist;
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator() || (dist = eyePos.distanceTo(new Vec3d(entity.getX(), entity.getY(), entity.getZ()))) > (double)autoRodRange || dist >= bestDist) continue;
            bestDist = dist;
            best = entity;
        }
        autoRodTarget = best;
        if (best == null || bestDist <= (double)autoRodTriggerRange) {
            return;
        }
        Vec3d targetPos = new Vec3d(best.getX(), best.getY(), best.getZ()).add(0.0, (double)best.getHeight() * 0.5, 0.0);
        Vec3d diff = targetPos.subtract(eyePos);
        float targetYaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
        double hDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float targetPitch = -this.rodPitchForDistance(hDist);
        this.rotateSmoothly(player, targetYaw, targetPitch, autoRodSpeed, 1);
        int rodSlot = -1;
        for (int i = 0; i < 9; ++i) {
            if (player.getInventory().getStack(i).getItem() != Items.FISHING_ROD) continue;
            rodSlot = i;
            break;
        }
        if (rodSlot < 0) {
            return;
        }
        player.getInventory().setSelectedSlot(rodSlot);
        if (player.fishHook == null && autoRodCooldown <= 0) {
            float yawDiff = Math.abs(this.angleDiff(player.getYaw(), targetYaw));
            float pitchDiff = Math.abs(targetPitch - player.getPitch());
            if (yawDiff < 6.0f && pitchDiff < 6.0f) {
                client.interactionManager.interactItem((PlayerEntity)player, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND);
                autoRodCooldown = 6;
            }
        }
        if (autoRodCooldown > 0) {
            --autoRodCooldown;
        }
    }

    // ===== v36 Combat feature extensions =====

    public void handleAimAssist(MinecraftClient client) {
        if (!aimAssistEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null || !player.isAlive() || freecamEnabled || player.isUsingItem()) {
            return;
        }
        Entity target = null;
        double bestAngle = (double)(aimAssistRange * 0.017453292f);
        for (Entity entity : client.world.getEntities()) {
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator()) continue;
            Vec3d diff = new Vec3d(entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ()).subtract(player.getEyePos());
            double yawToTarget = Math.atan2(diff.z, diff.x) - Math.toRadians(player.getYaw() + 90);
            double pitchToTarget = Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) - Math.toRadians(-player.getPitch());
            double angle = Math.sqrt(yawToTarget * yawToTarget + pitchToTarget * pitchToTarget);
            if (angle < bestAngle) {
                bestAngle = angle;
                target = entity;
            }
        }
        if (target == null) {
            return;
        }
        Vec3d diff = new Vec3d(target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ()).subtract(player.getEyePos());
        float targetYaw = (float)(Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0);
        float targetPitch = (float)(-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))));
        float deltaYaw = this.angleDiff(player.getYaw(), targetYaw);
        float deltaPitch = targetPitch - player.getPitch();
        float speed = aimAssistSpeed / 20.0f;
        player.setYaw(player.getYaw() + deltaYaw * speed);
        player.setPitch(MathHelper.clamp(player.getPitch() + deltaPitch * speed, -90.0f, 90.0f));
    }

    public void handleVelocity(MinecraftClient client) {
        if (!velocityEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive() || player.isInvulnerable()) {
            return;
        }
        if (player.hurtTime == player.maxHurtTime - 1) {
            Vec3d vel = player.getVelocity();
            double hx = vel.x * (double)(100 - velocityHPercent) / 100.0;
            double vy = vel.y * (double)(100 - velocityVPercent) / 100.0;
            double hz = vel.z * (double)(100 - velocityHPercent) / 100.0;
            player.setVelocity(hx, vy, hz);
        }
    }

    public void handleNoSlow(MinecraftClient client) {
        if (!noSlowEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive()) {
            return;
        }
        if (!player.isUsingItem()) {
            return;
        }
        ItemStack stack = player.getActiveItem();
        if (stack.isEmpty()) {
            return;
        }
        Item item = stack.getItem();
        boolean isWeapon = stack.contains(DataComponentTypes.WEAPON) || item instanceof ShieldItem || item instanceof TridentItem;
        boolean isFood = stack.contains(DataComponentTypes.FOOD);
        boolean isBow = item instanceof BowItem || item instanceof CrossbowItem;
        if (isWeapon || isFood || isBow) {
            float forward = player.input.playerInput.forward() ? 1.0f : (player.input.playerInput.backward() ? -1.0f : 0.0f);
            float sideways = player.input.playerInput.right() ? 1.0f : (player.input.playerInput.left() ? -1.0f : 0.0f);
            if (forward != 0.0f || sideways != 0.0f) {
                float yawRad = player.getYaw() * ((float)Math.PI / 180);
                float speed = 0.26f;
                double mx = (sideways * MathHelper.cos(yawRad) - forward * MathHelper.sin(yawRad)) * speed;
                double mz = (sideways * MathHelper.sin(yawRad) + forward * MathHelper.cos(yawRad)) * speed;
                Vec3d vel = player.getVelocity();
                player.setVelocity(mx, vel.y, mz);
            }
        }
    }

    public void handleCriticals(MinecraftClient client) {
        if (!criticalsEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive() || player.isOnGround() || player.isUsingItem() || player.hasVehicle() || player.isGliding() || player.isTouchingWater() || player.isInLava() || player.getAbilities().flying) {
            return;
        }
        if (!this.canCriticalHit(player, false)) {
            return;
        }
        if (criticalsMode == 1) {
            if (player.fallDistance >= 0.12f) {
                return;
            }
            if (client.interactionManager != null) {
                client.interactionManager.attackEntity((PlayerEntity)player, null);
            }
        }
    }

    public void handleJesus(MinecraftClient client) {
        if (!jesusEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive()) {
            return;
        }
        if (jesusMode == 0) {
            if (player.isTouchingWater() && !player.isSwimming()) {
                player.setVelocity(player.getVelocity().x, 0.08, player.getVelocity().z);
            }
        } else if (jesusMode == 1) {
            Box bb = player.getBoundingBox();
            int minX = MathHelper.floor(bb.minX);
            int maxX = MathHelper.ceil(bb.maxX);
            int minY = MathHelper.floor(bb.minY - 0.5);
            int maxY = MathHelper.floor(bb.maxY);
            int minZ = MathHelper.floor(bb.minZ);
            int maxZ = MathHelper.ceil(bb.maxZ);
            boolean waterBelow = false;
            outer:
            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        if (client.world.getBlockState(new BlockPos(x, y, z)).isOf(Blocks.WATER)) {
                            waterBelow = true;
                            break outer;
                        }
                    }
                }
            }
            if (waterBelow) {
                player.setVelocity(player.getVelocity().x, 0.1, player.getVelocity().z);
                player.setOnGround(true);
            }
        }
    }

    public static double[] buildRodRangeTable() {
        double[] t = new double[81];
        for (int up = 0; up <= 80; ++up) {
            double rad = Math.toRadians(up);
            double vx = 1.1 * Math.cos(rad);
            double vy = 1.1 * Math.sin(rad);
            double x = 0.0;
            double y = 0.0;
            for (int i = 0; i < 300; ++i) {
                vy = (vy - 0.03) * 0.92;
                x += (vx *= 0.92);
                if ((y += vy) <= -1.62) break;
            }
            t[up] = x;
        }
        return t;
    }

    public static float rodPitchForDistance(double dist) {
        int up;
        int bestUp = 0;
        for (up = 1; up <= 80; ++up) {
            if (!(ROD_RANGE_TABLE[up] > ROD_RANGE_TABLE[bestUp])) continue;
            bestUp = up;
        }
        if (dist >= ROD_RANGE_TABLE[bestUp]) {
            return bestUp;
        }
        for (up = 0; up <= 80; ++up) {
            if (!(ROD_RANGE_TABLE[up] >= dist)) continue;
            return up;
        }
        return bestUp;
    }

    public void handleBowAim(MinecraftClient client) {
        double newT;
        Vec3d predicted;
        if (!bowAimEnabled) {
            bowAimTarget = null;
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null || !player.isAlive() || freecamEnabled) {
            bowAimTarget = null;
            return;
        }
        ItemStack held = player.getMainHandStack();
        if (held.getItem() != Items.BOW || !player.isUsingItem()) {
            bowAimTarget = null;
            return;
        }
        float pull = BowItem.getPullProgress((int)player.getItemUseTime());
        double speed = (double)pull * 3.0;
        if (speed < 0.5) {
            bowAimTarget = null;
            return;
        }
        Vec3d eyePos = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0f);
        Entity best = null;
        double bestAngleDeg = Double.MAX_VALUE;
        for (Entity entity : client.world.getEntities()) {
            double dist;
            LivingEntity living;
            if (entity == player || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive() || living.isRemoved() || entity.isSpectator() || (dist = new Vec3d(entity.getX(), entity.getY(), entity.getZ()).distanceTo(new Vec3d(player.getX(), player.getY(), player.getZ()))) > (double)bowAimRange || dist < 1.0) continue;
            Vec3d to = new Vec3d(entity.getX(), entity.getY(), entity.getZ()).add(0.0, (double)entity.getHeight() * 0.5, 0.0).subtract(eyePos).normalize();
            double dot = look.x * to.x + look.y * to.y + look.z * to.z;
            double angDeg = Math.toDegrees(Math.acos(MathHelper.clamp((double)dot, (double)-1.0, (double)1.0)));
            if (angDeg > (double)bowAimAngleRange || angDeg >= bestAngleDeg) continue;
            bestAngleDeg = angDeg;
            best = entity;
        }
        if (best == null) {
            bowAimTarget = null;
            return;
        }
        bowAimTarget = best;
        Vec3d baseTarget = new Vec3d(best.getX(), best.getY(), best.getZ()).add(0.0, (double)best.getHeight() * 0.5, 0.0);
        Vec3d targetVel = best.getVelocity();
        double t = baseTarget.subtract(eyePos).horizontalLength() / Math.max(speed, 0.1);
        Vec2f aim = null;
        for (int iter = 0; iter < 3 && (aim = this.solveBowTrajectory(eyePos, predicted = baseTarget.add(targetVel.multiply(t)), speed)) != null && !((newT = this.estimateBowFlightTime(speed, Math.toRadians(-aim.y), eyePos, predicted)) <= 0.0); ++iter) {
            t = newT;
        }
        if (aim == null) {
            bowAimTarget = null;
            return;
        }
        this.rotateSmoothly(player, aim.x, aim.y, bowAimSpeed, 1);
        if (bowAimCooldown > 0) {
            --bowAimCooldown;
        }
        if (bowAimAutoShoot && pull >= 1.0f && bowAimCooldown <= 0) {
            float yawDiff = Math.abs(this.angleDiff(player.getYaw(), aim.x));
            float pitchDiff = Math.abs(aim.y - player.getPitch());
            if (yawDiff < bowAimAngleThreshold && pitchDiff < bowAimAngleThreshold) {
                client.interactionManager.stopUsingItem((PlayerEntity)player);
                bowAimCooldown = 15;
            }
        }
    }

    public static Vec2f solveBowTrajectory(Vec3d start, Vec3d target, double speed) {
        double dx = target.x - start.x;
        double dy = target.y - start.y;
        double dz = target.z - start.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        if (distXZ < 0.05) {
            return null;
        }
        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        double best = Double.MAX_VALUE;
        double bestUp = 0.0;
        for (double up = -30.0; up <= 85.0; up += 1.0) {
            double err;
            double hit = FengBoxClient.bowSimulateHitHeight(speed, Math.toRadians(up), distXZ);
            if (Double.isNaN(hit) || !((err = Math.abs(hit - dy)) < best)) continue;
            best = err;
            bestUp = up;
        }
        if (best > 2.5) {
            return null;
        }
        double fine = best;
        double fineUp = bestUp;
        for (double up = bestUp - 2.0; up <= bestUp + 2.0; up += 0.05) {
            double err;
            double hit = FengBoxClient.bowSimulateHitHeight(speed, Math.toRadians(up), distXZ);
            if (Double.isNaN(hit) || !((err = Math.abs(hit - dy)) < fine)) continue;
            fine = err;
            fineUp = up;
        }
        return new Vec2f(yaw, (float)(-fineUp));
    }

    public static double bowSimulateHitHeight(double speed, double pitchRad, double targetX) {
        double cos = Math.cos(pitchRad);
        double sin = Math.sin(pitchRad);
        double vx = speed * cos;
        double vy = speed * sin;
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < 400; ++i) {
            vy = vy * 0.99 - 0.05;
            double nx = x + (vx *= 0.99);
            double ny = y + vy;
            if (nx >= targetX) {
                if (vx <= 1.0E-6) {
                    return Double.NaN;
                }
                double f = (targetX - x) / vx;
                return y + vy * f;
            }
            x = nx;
            y = ny;
        }
        return Double.NaN;
    }

    public static double estimateBowFlightTime(double speed, double pitchRad, Vec3d start, Vec3d target) {
        double dx = target.x - start.x;
        double dz = target.z - start.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        double cos = Math.cos(pitchRad);
        double sin = Math.sin(pitchRad);
        double vx = speed * cos;
        double vy = speed * sin;
        double x = 0.0;
        double y = 0.0;
        for (int i = 1; i <= 400; ++i) {
            vy = vy * 0.99 - 0.05;
            double nx = x + (vx *= 0.99);
            double ny = y + vy;
            if (nx >= distXZ) {
                return i;
            }
            x = nx;
            y = ny;
        }
        return 0.0;
    }

    public void handleFastUse(MinecraftClient client) {
        if (!fastUseEnabled) {
            fastUseTimer = 0;
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        if (!player.isUsingItem()) {
            fastUseTimer = 0;
            return;
        }
        ItemStack active = player.getActiveItem();
        if (active.isEmpty()) {
            fastUseTimer = 0;
            return;
        }
        Item item = active.getItem();
        if (item == Items.BOW || item == Items.CROSSBOW || item == Items.SHIELD) {
            fastUseTimer = 0;
            return;
        }
        if (++fastUseTimer >= fastUseInterval) {
            fastUseTimer = 0;
            client.interactionManager.interactItem((PlayerEntity)player, player.getActiveHand());
        }
    }

    public void handleFastBreak(MinecraftClient client) {
        if (!fastBreakEnabled) {
            fastBreakTimer = 0;
            return;
        }
        if (client.player == null || client.interactionManager == null) {
            return;
        }
        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult)) {
            fastBreakTimer = 0;
            return;
        }
        BlockHitResult bhr = (BlockHitResult)hitResult;
        if (!client.interactionManager.isBreakingBlock()) {
            fastBreakTimer = 0;
            return;
        }
        if (++fastBreakTimer >= fastBreakInterval) {
            fastBreakTimer = 0;
            client.interactionManager.updateBlockBreakingProgress(bhr.getBlockPos(), bhr.getSide());
        }
    }

    public void handleAutoBlock(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        if (!autoBlockEnabled) {
            if (player.isBlocking()) {
                client.interactionManager.stopUsingItem((PlayerEntity)player);
                player.stopUsingItem();
            }
            return;
        }
        Hand shieldHand = null;
        if (player.getOffHandStack().isOf(Items.SHIELD)) {
            shieldHand = Hand.OFF_HAND;
        } else if (player.getMainHandStack().isOf(Items.SHIELD)) {
            shieldHand = Hand.MAIN_HAND;
        }
        if (shieldHand == null) {
            return;
        }
        boolean shouldBlock = true;
        if (autoBlockOnlyTarget && killAuraTarget == null) {
            shouldBlock = false;
        }
        if (player.age - lastAuraAttackTick < autoBlockDropTicks) {
            shouldBlock = false;
        }
        if (player.isUsingItem() && player.getActiveHand() != shieldHand) {
            return;
        }
        if (shouldBlock && !player.isBlocking()) {
            client.interactionManager.interactItem((PlayerEntity)player, shieldHand);
        } else if (!shouldBlock && player.isBlocking()) {
            client.interactionManager.stopUsingItem((PlayerEntity)player);
            player.stopUsingItem();
        }
    }

    public void handleProjectileDodge(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.options == null) {
            return;
        }
        if (!projectileDodgeEnabled) {
            if (projectileDodgeTimer > 0) {
                projectileDodgeTimer = 0;
                this.releaseDodgeKey(client);
            }
            dodgeThreatEntity = null;
            dodgeThreatUntil = 0L;
            return;
        }
        if (projectileDodgeTimer > 0) {
            if (--projectileDodgeTimer == 0) {
                this.releaseDodgeKey(client);
            }
            return;
        }
        double danger = projectileDodgeDanger;
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d playerVel = player.getVelocity();
        Entity dangerProj = null;
        for (Entity e : client.world.getEntities()) {
            ProjectileEntity pe;
            if (!(e instanceof ProjectileEntity) || (pe = (ProjectileEntity)e).getOwner() == player || pe instanceof FishingBobberEntity || e.squaredDistanceTo((Entity)player) > (double)(projectileDodgeRange * projectileDodgeRange)) continue;
            Vec3d pos = new Vec3d(e.getX(), e.getY(), e.getZ());
            Vec3d vel = e.getVelocity();
            boolean gravity = e instanceof PersistentProjectileEntity;
            double minDist = Double.MAX_VALUE;
            for (int t = 0; t < 20; ++t) {
                double d;
                if (gravity) {
                    vel = vel.multiply(0.99);
                    vel = new Vec3d(vel.x, vel.y - 0.05, vel.z);
                }
                if (!((d = (pos = pos.add(vel)).distanceTo(playerPos.add(playerVel.multiply((double)(t + 1))))) < minDist)) continue;
                minDist = d;
            }
            if (!(minDist < danger)) continue;
            dangerProj = e;
            break;
        }
        if (dangerProj == null) {
            return;
        }
        dodgeThreatEntity = dangerProj;
        dodgeThreatUntil = System.currentTimeMillis() + 600L;
        double dx = dangerProj.getX() - player.getX();
        double dz = dangerProj.getZ() - player.getZ();
        float yawTo = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float rel = MathHelper.wrapDegrees((float)(yawTo - player.getYaw()));
        projectileDodgeDir = rel >= 0.0f ? 1 : -1;
        projectileDodgeTimer = projectileDodgeHoldTicks;
        if (projectileDodgeDir < 0) {
            client.options.rightKey.setPressed(false);
        } else {
            client.options.leftKey.setPressed(false);
        }
        if (projectileDodgeDir < 0) {
            client.options.leftKey.setPressed(true);
        } else {
            client.options.rightKey.setPressed(true);
        }
    }

    public static void releaseDodgeKey(MinecraftClient client) {
        if (client.options == null) {
            return;
        }
        if (projectileDodgeDir < 0) {
            client.options.leftKey.setPressed(false);
        } else if (projectileDodgeDir > 0) {
            client.options.rightKey.setPressed(false);
        }
        projectileDodgeDir = 0;
    }

    public void handleTotemGhost(MinecraftClient client) {
        boolean hasTotem;
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return;
        }
        boolean bl = hasTotem = player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING) || player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
        if (totemGhostEnabled && prevHadTotem && !hasTotem && player.isAlive()) {
            totemGhostTimer = totemGhostDuration;
            player.setGlowing(true);
        }
        prevHadTotem = hasTotem;
        if (!totemGhostEnabled) {
            if (totemGhostTimer > 0) {
                totemGhostTimer = 0;
                player.setGlowing(false);
            }
            return;
        }
        if (totemGhostTimer <= 0) {
            return;
        }
        player.setGlowing(true);
        if (--totemGhostTimer <= 0) {
            player.setGlowing(false);
            return;
        }
        if (totemGhostTimer % 4 == 0) {
            double r = 0.6;
            Vec3d p = new Vec3d(player.getX(), player.getY(), player.getZ());
            client.world.addParticleClient((ParticleEffect)ParticleTypes.SOUL, p.x + (Math.random() - 0.5) * r, p.y + Math.random() * 2.0, p.z + (Math.random() - 0.5) * r, 0.0, 0.02, 0.0);
            client.world.addParticleClient((ParticleEffect)ParticleTypes.END_ROD, p.x + (Math.random() - 0.5) * r, p.y + Math.random() * 2.0, p.z + (Math.random() - 0.5) * r, 0.0, 0.01, 0.0);
        }
    }

    public static boolean hasLineOfSight(MinecraftClient client, ClientPlayerEntity player, Vec3d eyePos, Entity target) {
        Vec3d targetVec = new Vec3d(target.getX(), target.getY(), target.getZ()).add(0.0, (double)target.getHeight() * 0.5, 0.0);
        double targetDist = eyePos.distanceTo(targetVec);
        BlockHitResult hit = client.world.raycast(new RaycastContext(eyePos, targetVec, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return true;
        }
        return eyePos.distanceTo(hit.getPos()) >= targetDist - 0.2;
    }

    public static double approach(double cur, double target, double maxDelta) {
        double d = target - cur;
        if (d > maxDelta) {
            return cur + maxDelta;
        }
        if (d < -maxDelta) {
            return cur - maxDelta;
        }
        return target;
    }

    public void handleSpeed(MinecraftClient client) {
        if (!speedEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (speedFastMode) {
            float forward = player.input.playerInput.forward() ? 1.0f : (player.input.playerInput.backward() ? -1.0f : 0.0f);
            float sideways = player.input.playerInput.right() ? 1.0f : (player.input.playerInput.left() ? -1.0f : 0.0f);
            if (forward != 0.0f || sideways != 0.0f) {
                float yawRad = player.getYaw() * ((float)Math.PI / 180);
                float cosYaw = MathHelper.cos((float)yawRad);
                float sinYaw = MathHelper.sin((float)yawRad);
                double mx = (sideways * cosYaw - forward * sinYaw) * fastMoveSpeed;
                double mz = (sideways * sinYaw + forward * cosYaw) * fastMoveSpeed;
                if (fastMoveStealth) {
                    Vec3d cur = player.getVelocity();
                    double ramp = 0.15;
                    mx = this.approach(cur.x, mx, ramp);
                    mz = this.approach(cur.z, mz, ramp);
                }
                player.setVelocity(mx, player.getVelocity().y, mz);
            }
            player.getAbilities().setWalkSpeed(0.1f);
            return;
        }
        if (!player.isOnGround()) {
            return;
        }
        if (!player.input.playerInput.forward() && !player.input.playerInput.backward() && !player.input.playerInput.left() && !player.input.playerInput.right()) {
            return;
        }
        float yawRad = player.getYaw() * ((float)Math.PI / 180);
        float cos = MathHelper.cos((float)yawRad);
        float sin = MathHelper.sin((float)yawRad);
        double velX = (player.input.getMovementInput().x * cos - player.input.getMovementInput().y * sin) * speedAmount;
        double velZ = (player.input.getMovementInput().x * sin + player.input.getMovementInput().y * cos) * speedAmount;
        if (speedStealth) {
            Vec3d cur = player.getVelocity();
            double ramp = 0.18;
            velX = this.approach(cur.x, velX, ramp);
            velZ = this.approach(cur.z, velZ, ramp);
        }
        player.setVelocity(velX, player.getVelocity().y, velZ);
    }

    public void handleStep(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        // step height handled by EntityStepHeightMixin (override getStepHeight)
    }

    public void handleSprint(MinecraftClient client) {
        if (!sprintEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || player.isSprinting()) {
            return;
        }
        if (player.getHungerManager() != null && player.getHungerManager().getFoodLevel() <= 6) {
            return;
        }
        player.setSprinting(true);
    }

    public void handleAutoWalk(MinecraftClient client) {
        if (!autoWalkEnabled) {
            return;
        }
        if (client.player == null) {
            return;
        }
        client.player.input.playerInput = new net.minecraft.util.PlayerInput(true, false, false, false, client.player.input.playerInput.jump(), client.player.input.playerInput.sneak(), client.player.input.playerInput.sprint());
    }

    public void handleGuiMove(MinecraftClient client) {
        if (!guiMoveEnabled) {
            if (guiMoveActive) {
                this.releaseGuiMoveKeys();
            }
            return;
        }
        if (client.currentScreen == null || client.player == null) {
            if (guiMoveActive) {
                this.releaseGuiMoveKeys();
            }
            return;
        }
        if (client.options == null) {
            return;
        }
        long handle = client.getWindow().getHandle();
        KeyBinding[] keys = new KeyBinding[]{client.options.forwardKey, client.options.backKey, client.options.leftKey, client.options.rightKey, client.options.jumpKey, client.options.sneakKey, client.options.sprintKey};
        boolean any = false;
        for (KeyBinding k : keys) {
            boolean pressed = this.physicalKeyHeld(k, handle);
            k.setPressed(pressed);
            if (!pressed) continue;
            any = true;
        }
        guiMoveActive = any;
    }

    public static boolean physicalKeyHeld(KeyBinding k, long handle) {
        InputUtil.Key key = InputUtil.fromTranslationKey((String)k.getBoundKeyTranslationKey());
        if (key.getCategory() == InputUtil.Type.MOUSE) {
            if (handle == -1L) {
                return false;
            }
            return GLFW.glfwGetMouseButton((long)handle, (int)key.getCode()) == 1;
        }
        if (handle == -1L) {
            return false;
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), key.getCode());
    }

    public void handleMultiJump(MinecraftClient client) {
        if (!multiJumpEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return;
        }
        if (player.getAbilities().flying) {
            multiJumpLeft = 0;
            return;
        }
        if (!client.options.jumpKey.isPressed()) {
            multiJumpLeft = 0;
            return;
        }
        if (player.isOnGround() || player.isTouchingWater() || player.isInLava() || player.isClimbing() || player.hasVehicle()) {
            multiJumpLeft = 0;
            return;
        }
        if (multiJumpLeft >= multiJumpMax) {
            return;
        }
        if (player.getVelocity().y > 0.0) {
            return;
        }
        Vec3d vel = player.getVelocity();
        float jumpV = 0.42f + player.getJumpBoostVelocityModifier();
        player.setVelocity(vel.x, (double)jumpV, vel.z);
        ++multiJumpLeft;
    }

    public void handleFullbright(MinecraftClient client) {
        if (client.options == null) {
            return;
        }
        if (fullbrightEnabled) {
            if ((Double)client.options.getGamma().getValue() <= 1.0) {
                prevGamma = (Double)client.options.getGamma().getValue();
            }
            client.options.getGamma().setValue(100.0);
        } else {
            client.options.getGamma().setValue(prevGamma);
        }
    }

    public void handleChestStealer(MinecraftClient client) {
        if (!chestStealerEnabled) {
            return;
        }
        if (client.player == null || client.interactionManager == null) {
            return;
        }
        if (chestStealerCooldown > 0) {
            --chestStealerCooldown;
            return;
        }
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen)) {
            return;
        }
        HandledScreen hs = (HandledScreen)screen;
        if (screen instanceof InventoryScreen) {
            return;
        }
        ScreenHandler handler = hs.getScreenHandler();
        int totalSlots = handler.slots.size();
        int containerSlots = totalSlots - 36;
        if (containerSlots <= 0) {
            return;
        }
        boolean found = false;
        for (int i = 0; i < containerSlots; ++i) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty() || chestStealerSmartFilter && !this.isWorthTaking(stack, client.player)) continue;
            client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)client.player);
            chestStealerCooldown = chestStealerDelay;
            found = true;
            break;
        }
        if (!found) {
            client.player.closeHandledScreen();
        }
    }

    private boolean isTrashItem(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id != null && TRASH_IDS.contains(id.getPath());
    }

    public boolean isWorthTaking(ItemStack stack, ClientPlayerEntity player) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) {
            return true;
        }
        String path = id.getPath();
        if (TRASH_IDS.contains(path)) {
            return false;
        }
        if (!this.isGear(path)) {
            return true;
        }
        int tier = 0;
        String suffix = null;
        for (Map.Entry<String, Integer> e : InventoryUtils.TIER_MAP.entrySet()) {
            if (!path.startsWith(e.getKey() + "_")) continue;
            tier = e.getValue();
            suffix = path.substring(e.getKey().length() + 1);
            break;
        }
        if (suffix == null || tier == 0) {
            return true;
        }
        if (ARMOR_TYPES.contains(suffix)) {
            return this.isBetterArmor(stack, player, suffix, tier);
        }
        if (TOOL_TYPES.contains(suffix)) {
            return this.isBetterTool(stack, player, suffix, tier);
        }
        return true;
    }

    private boolean isGear(String path) {
        for (String p : new String[]{"netherite_", "diamond_", "iron_", "golden_", "stone_", "wooden_", "leather_", "chainmail_"}) {
            if (!path.startsWith(p)) continue;
            return true;
        }
        return false;
    }

    private boolean isBetterArmor(ItemStack chestItem, ClientPlayerEntity player, String slotType, int chestTier) {
        EquipmentSlot equipSlot;
        switch (slotType) {
            case "helmet": {
                equipSlot = EquipmentSlot.HEAD;
                break;
            }
            case "chestplate": {
                equipSlot = EquipmentSlot.CHEST;
                break;
            }
            case "leggings": {
                equipSlot = EquipmentSlot.LEGS;
                break;
            }
            case "boots": {
                equipSlot = EquipmentSlot.FEET;
                break;
            }
            default: {
                return true;
            }
        }
        ItemStack equipped = player.getEquippedStack(equipSlot);
        if (equipped.isEmpty()) {
            return true;
        }
        int equippedTier = InventoryUtils.getGearTier(equipped);
        if (chestTier > equippedTier) {
            return true;
        }
        if (chestTier < equippedTier) {
            return false;
        }
        return InventoryUtils.getEnchantScore(chestItem) > InventoryUtils.getEnchantScore(equipped);
    }

    private boolean isBetterTool(ItemStack chestItem, ClientPlayerEntity player, String toolType, int chestTier) {
        String op;
        int bestTier = 0;
        int bestEnch = 0;
        for (int i = 0; i < 9; ++i) {
            String hp;
            ItemStack hs = player.getInventory().getStack(i);
            if (hs.isEmpty() || !(hp = Registries.ITEM.getId(hs.getItem()).getPath()).endsWith("_" + toolType)) continue;
            int ht = InventoryUtils.getGearTier(hs);
            int he = InventoryUtils.getEnchantScore(hs);
            if (ht <= bestTier && (ht != bestTier || he <= bestEnch)) continue;
            bestTier = ht;
            bestEnch = he;
        }
        ItemStack off = player.getOffHandStack();
        if (!off.isEmpty() && (op = Registries.ITEM.getId(off.getItem()).getPath()).endsWith("_" + toolType)) {
            int ot = InventoryUtils.getGearTier(off);
            int oe = InventoryUtils.getEnchantScore(off);
            if (ot > bestTier || ot == bestTier && oe > bestEnch) {
                bestTier = ot;
                bestEnch = oe;
            }
        }
        if (bestTier == 0) {
            return true;
        }
        if (chestTier > bestTier) {
            return true;
        }
        if (chestTier < bestTier) {
            return false;
        }
        return InventoryUtils.getEnchantScore(chestItem) > bestEnch;
    }

    public static void addTrashItem(String id) {
        if (TRASH_IDS.add(id)) {
            chestStealerTrashCustom = String.join((CharSequence)";", TRASH_IDS);
            FengBoxClient.saveConfig();
        }
    }

    public static void removeTrashItem(String id) {
        if (TRASH_IDS.remove(id)) {
            chestStealerTrashCustom = String.join((CharSequence)";", TRASH_IDS);
            FengBoxClient.saveConfig();
        }
    }

    public static void resetTrashItems() {
        TRASH_IDS.clear();
        TRASH_IDS.addAll(DEFAULT_TRASH);
        chestStealerTrashCustom = "";
        FengBoxClient.saveConfig();
    }

    public void handleHotbarOrganizer(MinecraftClient client) {
        if (!hotbarOrganizerEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen == null || !(client.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (!player.playerScreenHandler.getCursorStack().isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastOrganizeTime < 500L) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        int syncId = player.playerScreenHandler.syncId;
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 0, "sword", InventoryUtils.findBestToolSlot(inv, "sword"));
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 1, "pickaxe", InventoryUtils.findBestToolSlot(inv, "pickaxe"));
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 2, "axe", InventoryUtils.findBestToolSlot(inv, "axe"));
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 3, "food", this.findBestFoodSlot(inv));
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 4, "block", this.findMostAbundantBlockSlot(inv));
        this.clickSwapToHotbarIfWrong(client, syncId, inv, 5, "goldenapple", this.findGoldenAppleSlot(inv));
        this.tryEquipArmor(client, syncId);
        lastOrganizeTime = now;
    }

    public void clickSwapToHotbarIfWrong(MinecraftClient client, int syncId, PlayerInventory inv, int hotbarSlot, String want, int sourceSlot) {
        if (this.isTargetType(inv.getStack(hotbarSlot), want)) {
            return;
        }
        if (sourceSlot < 0 || sourceSlot == hotbarSlot) {
            return;
        }
        int a = hotbarSlot + 36;
        int b = sourceSlot < 9 ? sourceSlot + 36 : sourceSlot;
        FengBoxClient.swapSlots(client, syncId, a, b);
    }

    private boolean isTargetType(ItemStack stack, String want) {
        if (stack.isEmpty()) {
            return false;
        }
        String path = Registries.ITEM.getId(stack.getItem()).getPath();
        switch (want) {
            case "sword": {
                return path.endsWith("_sword");
            }
            case "pickaxe": {
                return path.endsWith("_pickaxe");
            }
            case "axe": {
                return path.endsWith("_axe");
            }
            case "food": {
                return stack.get(DataComponentTypes.FOOD) != null;
            }
            case "block": {
                return stack.getItem() instanceof BlockItem;
            }
            case "goldenapple": {
                return stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
            }
        }
        return false;
    }

    public int findBestFoodSlot(PlayerInventory inv) {
        int bestSlot = -1;
        int bestHunger = 0;
        for (int i = 0; i < 36; ++i) {
            int h;
            FoodComponent food;
            Item item;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || (food = stack.get(DataComponentTypes.FOOD)) == null || (h = food.nutrition()) <= bestHunger) continue;
            bestHunger = h;
            bestSlot = i;
        }
        return bestSlot;
    }

    public int findMostAbundantBlockSlot(PlayerInventory inv) {
        int bestSlot = -1;
        int bestCount = 0;
        for (int i = 0; i < 36; ++i) {
            int cnt;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem) || (cnt = stack.getCount()) <= bestCount) continue;
            bestCount = cnt;
            bestSlot = i;
        }
        return bestSlot;
    }

    public int findGoldenAppleSlot(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            Item item;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || (item = stack.getItem()) != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE) continue;
            return i;
        }
        return -1;
    }

    public static void swapSlots(MinecraftClient client, int syncId, int a, int b) {
        if (a == b) {
            return;
        }
        client.interactionManager.clickSlot(syncId, a, 0, SlotActionType.PICKUP, (PlayerEntity)client.player);
        client.interactionManager.clickSlot(syncId, b, 0, SlotActionType.PICKUP, (PlayerEntity)client.player);
        client.interactionManager.clickSlot(syncId, a, 0, SlotActionType.PICKUP, (PlayerEntity)client.player);
    }

    public void tryEquipArmor(MinecraftClient client, int syncId) {
        ClientPlayerEntity player = client.player;
        PlayerInventory inv = player.getInventory();
        int[] wornIdx = new int[]{39, 38, 37, 36};
        int[] screenSlot = new int[]{5, 6, 7, 8};
        String[] suffix = new String[]{"helmet", "chestplate", "leggings", "boots"};
        for (int k = 0; k < 4; ++k) {
            int best = -1;
            int bestTier = 0;
            int bestEnch = 0;
            int bestDur = 0;
            for (int i = 0; i < 36; ++i) {
                String path;
                ItemStack stack;
                if (i == inv.getSelectedSlot() || (stack = inv.getStack(i)).isEmpty() || !(path = Registries.ITEM.getId(stack.getItem()).getPath()).endsWith("_" + suffix[k]) || FengBoxClient.durabilityPercent(stack) < 10.0f) continue;
                int t = InventoryUtils.getGearTier(stack);
                int e = InventoryUtils.getEnchantScore(stack);
                int d = (int)FengBoxClient.durabilityPercent(stack);
                if (t <= bestTier && (t != bestTier || e <= bestEnch) && (t != bestTier || e != bestEnch || d <= bestDur)) continue;
                bestTier = t;
                bestEnch = e;
                bestDur = d;
                best = i;
            }
            if (best < 0) continue;
            ItemStack worn = inv.getStack(wornIdx[k]);
            if (!worn.isEmpty()) {
                int wt = InventoryUtils.getGearTier(worn);
                int we = InventoryUtils.getEnchantScore(worn);
                int wd = (int)FengBoxClient.durabilityPercent(worn);
                if (wt > bestTier || wt == bestTier && (we > bestEnch || we == bestEnch && wd >= bestDur)) continue;
            }
            int b = best < 9 ? best + 36 : best;
            FengBoxClient.swapSlots(client, syncId, screenSlot[k], b);
        }
    }

    public static List<ItemStack> invManagerGetAllItems(PlayerInventory inv) {
        int i;
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(40);
        for (i = 0; i < 36; ++i) {
            list.add(inv.getStack(i));
        }
        for (i = 0; i < 4; ++i) {
            // TODO: inv.armor removed. list.add(inv.getStack(36 + i)); // approximate armor slot
        }
        return list;
    }

    public static int invManagerScreenSlot(int idx) {
        if (idx < 9) {
            return idx + 36;
        }
        if (idx < 36) {
            return idx;
        }
        if (idx < 40) {
            return 8 - (idx - 36);
        }
        return 45;
    }

    public static String invManagerDisplayName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        try {
            Text t = stack.getName();
            return t == null ? null : t.getString();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean invManagerHasServerMark(ItemStack stack) {
        String name = FengBoxClient.invManagerDisplayName(stack);
        if (name == null) {
            return false;
        }
        for (String kw : new String[]{"\u957f\u6309\u70b9\u51fb", "\u70b9\u51fb\u4f7f\u7528", "\u79bb\u5f00\u6e38\u620f", "\u9009\u62e9\u4e00\u4e2a\u961f\u4f0d", "\u518d\u6765\u4e00\u5c40", "Click to use", "Click", "Teleport"}) {
            if (!name.contains(kw)) continue;
            return true;
        }
        return false;
    }

    public static boolean invManagerHasServerItem(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !FengBoxClient.invManagerHasServerMark(s)) continue;
            return true;
        }
        return false;
    }

    public static boolean invManagerIsUsable(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        if (FengBoxClient.invManagerIsSkull(stack)) {
            return false;
        }
        return !FengBoxClient.invManagerHasServerMark(stack);
    }

    public static boolean invManagerIsUsableItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        if (FengBoxClient.invManagerHasServerMark(stack)) {
            return true;
        }
        if (stack.hasEnchantments()) {
            return true;
        }
        return FengBoxClient.invManagerIsUsable(stack);
    }

    public static boolean invManagerIsWeaponItem(ItemStack stack) {
        Item i = stack.getItem();
        return i instanceof AxeItem; // TODO: SwordItem removed
    }

    public static boolean invManagerIsSkull(ItemStack stack) {
        Item i = stack.getItem();
        return i == Items.PLAYER_HEAD || i == Items.WITHER_SKELETON_SKULL || i == Items.ZOMBIE_HEAD || i == Items.SKELETON_SKULL || i == Items.CREEPER_HEAD || i == Items.DRAGON_HEAD || i == Items.PIGLIN_HEAD;
    }

    public static boolean invManagerIsEndCrystal(ItemStack stack) {
        return stack.getItem() == Items.END_CRYSTAL;
    }

    public static boolean invManagerIsKBSlimeBall(ItemStack stack) {
        return stack.getItem() == Items.SLIME_BALL;
    }

    public static boolean invManagerIsKBStick(ItemStack stack) {
        return stack.getItem() == Items.STICK;
    }

    public static boolean invManagerIsEnchantedGoldenApple(ItemStack stack) {
        return stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }

    public static boolean invManagerIsLegitAxe(ItemStack stack) {
        if (!(stack.getItem() instanceof AxeItem)) {
            return false;
        }
        int sharp = FengBoxClient.invManagerEnchLevel(stack, Enchantments.SHARPNESS);
        return sharp >= 8 && sharp < 50;
    }

    public static boolean invManagerIsOtherCheat(ItemStack stack) {
        String name = FengBoxClient.invManagerDisplayName(stack);
        if (name != null && name.contains("\u4e00\u5200")) {
            return true;
        }
        return stack.getItem() == Items.GOLDEN_AXE && FengBoxClient.invManagerEnchLevel(stack, Enchantments.SHARPNESS) > 100;
    }

    public static boolean invManagerIsFunctionalBlock(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof BlockItem) {
            Block b = ((BlockItem)stack.getItem()).getBlock();
            if (b == Blocks.ENDER_CHEST) {
                return true;
            }
            if (b instanceof ShulkerBoxBlock) {
                return true;
            }
            if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL) {
                return true;
            }
            if (b == Blocks.CRAFTING_TABLE || b == Blocks.FURNACE || b == Blocks.SMOKER || b == Blocks.BLAST_FURNACE) {
                return true;
            }
            if (b == Blocks.ANVIL || b == Blocks.ENCHANTING_TABLE || b == Blocks.BEACON) {
                return true;
            }
            if (b == Blocks.HOPPER || b == Blocks.DISPENSER || b == Blocks.DROPPER || b == Blocks.OBSERVER) {
                return true;
            }
            if (b == Blocks.PISTON || b == Blocks.STICKY_PISTON) {
                return true;
            }
            if (b == Blocks.BREWING_STAND) {
                return true;
            }
        }
        return stack.getItem() == Items.ENDER_PEARL || stack.getItem() == Items.ENDER_EYE;
    }

    public static boolean invManagerIsPlaceable(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            return false;
        }
        Block b = ((BlockItem)stack.getItem()).getBlock();
        if (b == Blocks.AIR) {
            return false;
        }
        if (b == Blocks.WATER || b == Blocks.LAVA) {
            return false;
        }
        return b != Blocks.BEDROCK && b != Blocks.BARRIER && b != Blocks.COMMAND_BLOCK && b != Blocks.CHAIN_COMMAND_BLOCK && b != Blocks.REPEATING_COMMAND_BLOCK && b != Blocks.STRUCTURE_BLOCK && b != Blocks.STRUCTURE_VOID && b != Blocks.JIGSAW;
    }

    public static boolean invManagerIsInputActive(MinecraftClient client) {
        if (client.options == null) {
            return false;
        }
        return client.options.forwardKey.isPressed() || client.options.backKey.isPressed() || client.options.leftKey.isPressed() || client.options.rightKey.isPressed() || client.options.jumpKey.isPressed() || client.options.sneakKey.isPressed();
    }

    public static int enchLevel(ItemStack stack, RegistryKey<Enchantment> key) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return 0;
        }
        return EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key), stack);
    }

    public static int invManagerEnchLevel(ItemStack stack, RegistryKey<Enchantment> ench) {
        return FengBoxClient.enchLevel(stack, ench);
    }

    public static float invManagerGetArmorScore(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0f;
        }
        // TODO: ArmorItem removed in 1.21.2+
        String path = Registries.ITEM.getId(stack.getItem()).getPath();
        float score = path.startsWith("netherite_") ? 600.0f : (path.startsWith("diamond_") ? 500.0f : (path.startsWith("iron_") ? 400.0f : (path.startsWith("golden_") ? 300.0f : (path.startsWith("chainmail_") ? 200.0f : (path.startsWith("leather_") ? 100.0f : 0.0f)))));
        return score + (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.PROTECTION);
    }

    public static int invManagerGetBestArmor(PlayerInventory inv, EquipmentSlot es) {
        return -1; // TODO: ArmorItem removed in 1.21.2+, armor equip logic disabled
    }

    public static float invManagerGetAttackDamage(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0f;
        }
        double dmg = 0.0;
        try {
            AttributeModifiersComponent mods = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (mods != null) {
                for (AttributeModifiersComponent.Entry e : mods.modifiers()) {
                    if (e.slot() != AttributeModifierSlot.MAINHAND || !e.attribute().matches(EntityAttributes.ATTACK_DAMAGE)) continue;
                    dmg += e.modifier().value();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return (float)dmg;
    }

    public static float invManagerGetSwordDamage(ItemStack stack) {
        return 0.0f; // TODO: SwordItem removed in 1.21.2+
    }

    public static float invManagerGetAxeDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof AxeItem)) {
            return 0.0f;
        }
        float dmg = FengBoxClient.invManagerGetAttackDamage(stack);
        dmg += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.SHARPNESS) * 1.25f;
        return dmg += (float)Math.max(FengBoxClient.invManagerEnchLevel(stack, Enchantments.SMITE), FengBoxClient.invManagerEnchLevel(stack, Enchantments.BANE_OF_ARTHROPODS)) * 2.5f;
    }

    public static float invManagerGetDigSpeed(ItemStack stack) {
        BlockState test;
        // TODO: SwordItem, PickaxeItem, ShovelItem removed in 1.21.2+
        if (FengBoxClient.invManagerIsLegitAxe(stack)) {
            return 0.0f;
        }
        if (stack.getItem() instanceof AxeItem) {
            test = Blocks.OAK_LOG.getDefaultState();
        } else {
            return 0.0f;
        }
        return stack.getMiningSpeedMultiplier(test) + (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.EFFICIENCY) * 0.0075f;
    }

    public static float invManagerGetBowScore(ItemStack stack) {
        if (!(stack.getItem() instanceof BowItem)) {
            return 0.0f;
        }
        float score = 10.0f;
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.PUNCH);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.FLAME);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.INFINITY);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.POWER) / 10.0f;
        if (stack.getMaxDamage() > 0) {
            score += (float)(stack.getMaxDamage() - stack.getDamage()) / (float)stack.getMaxDamage();
        }
        return score;
    }

    public static float invManagerGetBowScoreAlt(ItemStack stack) {
        if (!(stack.getItem() instanceof BowItem)) {
            return 0.0f;
        }
        float score = 10.0f;
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.PUNCH) / 10.0f;
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.FLAME);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.INFINITY);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.POWER);
        if (stack.getMaxDamage() > 0) {
            score += (float)(stack.getMaxDamage() - stack.getDamage()) / (float)stack.getMaxDamage();
        }
        return score;
    }

    public static float invManagerGetCrossbowScore(ItemStack stack) {
        if (!(stack.getItem() instanceof CrossbowItem)) {
            return 0.0f;
        }
        float score = 10.0f;
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.QUICK_CHARGE);
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.MULTISHOT) * 2.0f;
        score += (float)FengBoxClient.invManagerEnchLevel(stack, Enchantments.PIERCING);
        if (stack.getMaxDamage() > 0) {
            score += (float)(stack.getMaxDamage() - stack.getDamage()) / (float)stack.getMaxDamage();
        }
        return score;
    }

    public static boolean invManagerIsGoodBow(ItemStack stack) {
        return FengBoxClient.invManagerGetBowScore(stack) > 10.0f && FengBoxClient.invManagerIsUsable(stack);
    }

    public static boolean invManagerIsGoodBowAlt(ItemStack stack) {
        return FengBoxClient.invManagerGetBowScoreAlt(stack) > 10.0f && FengBoxClient.invManagerIsUsable(stack);
    }

    public static int invManagerGetBestSword(PlayerInventory inv) {
        return -1; // TODO: SwordItem removed in 1.21.2+
    }

    public static int invManagerGetBestPickaxe(PlayerInventory inv) {
        return -1; // TODO: PickaxeItem removed in 1.21.2+
    }

    public static int invManagerGetBestAxe(PlayerInventory inv) {
        int best = -1;
        float bestScore = -1.0f;
        for (int i = 0; i < 36; ++i) {
            float sc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof AxeItem) || FengBoxClient.invManagerIsLegitAxe(s) || !FengBoxClient.invManagerIsUsable(s) || !((sc = FengBoxClient.invManagerGetDigSpeed(s)) > bestScore)) continue;
            bestScore = sc;
            best = i;
        }
        return best;
    }

    public static int invManagerGetBestSharpAxe(PlayerInventory inv) {
        int best = -1;
        float bestScore = -1.0f;
        for (int i = 0; i < 36; ++i) {
            float sc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof AxeItem) || !FengBoxClient.invManagerIsLegitAxe(s) || FengBoxClient.invManagerIsOtherCheat(s) || !FengBoxClient.invManagerIsUsable(s) || !((sc = FengBoxClient.invManagerGetAxeDamage(s)) > bestScore)) continue;
            bestScore = sc;
            best = i;
        }
        return best;
    }

    public static int invManagerGetBestShovel(PlayerInventory inv) {
        return -1; // TODO: ShovelItem removed in 1.21.2+
    }

    public static int invManagerGetBestCrossbow(PlayerInventory inv) {
        int best = -1;
        float bestScore = -1.0f;
        for (int i = 0; i < 36; ++i) {
            float sc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof CrossbowItem) || !FengBoxClient.invManagerIsUsable(s) || !((sc = FengBoxClient.invManagerGetCrossbowScore(s)) > bestScore)) continue;
            bestScore = sc;
            best = i;
        }
        return best;
    }

    public static int invManagerGetBestBow(PlayerInventory inv) {
        int best = -1;
        float bestScore = -1.0f;
        for (int i = 0; i < 36; ++i) {
            float sc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof BowItem) || !FengBoxClient.invManagerIsUsable(s) || !((sc = FengBoxClient.invManagerGetBowScore(s)) > bestScore)) continue;
            bestScore = sc;
            best = i;
        }
        return best;
    }

    public static int invManagerGetBestBowAlt(PlayerInventory inv) {
        int best = -1;
        float bestScore = -1.0f;
        for (int i = 0; i < 36; ++i) {
            float sc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof BowItem) || !FengBoxClient.invManagerIsUsable(s) || !((sc = FengBoxClient.invManagerGetBowScoreAlt(s)) > bestScore)) continue;
            bestScore = sc;
            best = i;
        }
        return best;
    }

    public static int invManagerGetMostBlock(PlayerInventory inv) {
        int best = -1;
        int bestCount = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof BlockItem) || !FengBoxClient.invManagerIsPlaceable(s) || !FengBoxClient.invManagerIsUsable(s) || invManagerFunctionalBlocks && FengBoxClient.invManagerIsFunctionalBlock(s) || s.getCount() <= bestCount) continue;
            bestCount = s.getCount();
            best = i;
        }
        return best;
    }

    public static int invManagerGetWorstBlock(PlayerInventory inv) {
        int worst = -1;
        int worstCount = Integer.MAX_VALUE;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof BlockItem) || !FengBoxClient.invManagerIsPlaceable(s) || !FengBoxClient.invManagerIsUsable(s) || invManagerFunctionalBlocks && FengBoxClient.invManagerIsFunctionalBlock(s) || s.getCount() >= worstCount) continue;
            worstCount = s.getCount();
            worst = i;
        }
        return worst;
    }

    public static int invManagerGetBestFoodStack(PlayerInventory inv) {
        int best = -1;
        int bestHunger = -1;
        for (int i = 0; i < 36; ++i) {
            FoodComponent fc;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.get(DataComponentTypes.FOOD) == null || !FengBoxClient.invManagerIsUsable(s) || s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE || (fc = s.get(DataComponentTypes.FOOD)) == null || fc.nutrition() <= bestHunger) continue;
            bestHunger = fc.nutrition();
            best = i;
        }
        return best;
    }

    public static int invManagerGetBestProjectile(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.getItem() != Items.EGG && s.getItem() != Items.SNOWBALL || !FengBoxClient.invManagerIsUsable(s)) continue;
            return i;
        }
        return -1;
    }

    public static int invManagerGetWorstProjectile(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.getItem() != Items.EGG && s.getItem() != Items.SNOWBALL) continue;
            return i;
        }
        return -1;
    }

    public static int invManagerGetArrowStack(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof ArrowItem) || !FengBoxClient.invManagerIsUsable(s)) continue;
            return i;
        }
        return -1;
    }

    public static int invManagerGetFishingRodStack(PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof FishingRodItem)) continue;
            return i;
        }
        return -1;
    }

    public static int invManagerFindItemInRange(PlayerInventory inv, Item item, int from, int to) {
        for (int i = from; i <= to; ++i) {
            if (i < 0 || i >= 36 || inv.getStack(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static boolean invManagerHasItem(PlayerInventory inv, Item item) {
        return FengBoxClient.invManagerFindItemInRange(inv, item, 0, 35) >= 0;
    }

    public static int invManagerCountItem(PlayerInventory inv, Item item) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.getItem() != item) continue;
            count += s.getCount();
        }
        return count;
    }

    public static int invManagerCountBlocks(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof BlockItem) || !FengBoxClient.invManagerIsPlaceable(s) || !FengBoxClient.invManagerIsUsable(s) || invManagerFunctionalBlocks && FengBoxClient.invManagerIsFunctionalBlock(s)) continue;
            count += s.getCount();
        }
        return count;
    }

    public static int invManagerCountFood(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.get(DataComponentTypes.FOOD) == null || s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE || !FengBoxClient.invManagerIsUsable(s)) continue;
            count += s.getCount();
        }
        return count;
    }

    public static int invManagerCountFishingRods(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            if (inv.getStack(i).isEmpty() || !(inv.getStack(i).getItem() instanceof FishingRodItem)) continue;
            ++count;
        }
        return count;
    }

    public static int invManagerCountProjectiles(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || s.getItem() != Items.EGG && s.getItem() != Items.SNOWBALL) continue;
            count += s.getCount();
        }
        return count;
    }

    public static int invManagerCountArrows(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof ArrowItem) || !FengBoxClient.invManagerIsUsable(s)) continue;
            count += s.getCount();
        }
        return count;
    }

    public static boolean invManagerIsUsefulItem(MinecraftClient client, PlayerInventory inv, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (FengBoxClient.invManagerIsWeaponItem(stack)) {
            return true;
        }
        if (FengBoxClient.invManagerHasServerMark(stack)) {
            return true;
        }
        if (FengBoxClient.invManagerIsEndCrystal(stack)) {
            return true;
        }
        if (invManagerFunctionalBlocks && FengBoxClient.invManagerIsFunctionalBlock(stack)) {
            return true;
        }
        // TODO: ArmorItem removed in 1.21.2+, auto-equip disabled
        // if (item instanceof ArmorItem) { ... }
        /*
        if (item instanceof ArmorItem) {
            EquipmentSlot slot = ((ArmorItem)item).getSlotType();
            ItemStack worn = inv.player.getEquippedStack(slot);
            return !(FengBoxClient.invManagerGetArmorScore(stack) < FengBoxClient.invManagerGetArmorScore(worn));
        }
        */
        // TODO: PickaxeItem removed in 1.21.2+
        // if (item instanceof PickaxeItem) { ... }
        /*
        if (item instanceof PickaxeItem) {
            int best = FengBoxClient.invManagerGetBestPickaxe(inv);
            return best >= 0 && FengBoxClient.invManagerGetDigSpeed(stack) >= FengBoxClient.invManagerGetDigSpeed(inv.getStack(best));
        }
        */
        if (item instanceof AxeItem) {
            if (FengBoxClient.invManagerIsLegitAxe(stack)) {
                int best = FengBoxClient.invManagerGetBestSharpAxe(inv);
                return best >= 0 && FengBoxClient.invManagerGetAxeDamage(stack) >= FengBoxClient.invManagerGetAxeDamage(inv.getStack(best));
            }
            int best = FengBoxClient.invManagerGetBestAxe(inv);
            return best >= 0 && FengBoxClient.invManagerGetDigSpeed(stack) >= FengBoxClient.invManagerGetDigSpeed(inv.getStack(best));
        }
        // TODO: ShovelItem removed in 1.21.2+
        /*
        if (item instanceof ShovelItem) {
            int best = FengBoxClient.invManagerGetBestShovel(inv);
            return best >= 0 && FengBoxClient.invManagerGetDigSpeed(stack) >= FengBoxClient.invManagerGetDigSpeed(inv.getStack(best));
        }
        */
        if (item instanceof CrossbowItem) {
            int best = FengBoxClient.invManagerGetBestCrossbow(inv);
            return best >= 0 && FengBoxClient.invManagerGetCrossbowScore(stack) >= FengBoxClient.invManagerGetCrossbowScore(inv.getStack(best));
        }
        if (item instanceof BowItem) {
            if (FengBoxClient.invManagerIsGoodBow(stack)) {
                return true;
            }
            return FengBoxClient.invManagerIsGoodBowAlt(stack);
        }
        if (item == Items.WATER_BUCKET || item == Items.LAVA_BUCKET) {
            return FengBoxClient.invManagerCountItem(inv, item) <= invManagerMaxBuckets;
        }
        if (item == Items.FISHING_ROD) {
            return FengBoxClient.invManagerCountFishingRods(inv) <= 1;
        }
        if (item instanceof BlockItem) {
            if (FengBoxClient.invManagerIsPlaceable(stack) && FengBoxClient.invManagerIsUsable(stack) && (!invManagerFunctionalBlocks || !FengBoxClient.invManagerIsFunctionalBlock(stack))) {
                return FengBoxClient.invManagerCountBlocks(inv) <= invManagerMaxBlocks * 64;
            }
            return true;
        }
        if (item.getComponents().contains(DataComponentTypes.FOOD) && item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE) {
            return FengBoxClient.invManagerCountFood(inv) <= invManagerMaxFood * 64;
        }
        return FengBoxClient.invManagerIsUsableItem(stack);
    }

    public void handleInventoryManager(MinecraftClient client) {
        if (!invManagerEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null || client.world == null) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        if (this.invManagerHasServerItem(inv)) {
            return;
        }
        if (!(client.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (!player.playerScreenHandler.getCursorStack().isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - invManagerLastAction < (long)invManagerDelay) {
            return;
        }
        boolean wasSprinting = player.isSprinting();
        if (invManagerSilentSprint && wasSprinting) {
            this.invManagerStopSilentSprint(client);
        }
        this.invManagerPerformAction(client);
        invManagerLastAction = now;
        if (invManagerSilentSprint && wasSprinting) {
            this.invManagerReleaseSilentSprint(client);
        }
    }

    public static void invManagerPerformAction(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        int syncId = player.playerScreenHandler.syncId;
        if (invManagerAutoArmor && FengBoxClient.invManagerHandleAutoArmor(client, syncId)) {
            return;
        }
        if (invManagerAutoOffhand && invManagerOffhandMode > 0 && FengBoxClient.invManagerHandleOffhand(client, syncId)) {
            return;
        }
        if (invManagerSortMode > 0 && FengBoxClient.invManagerHandleHotbarSort(client, syncId, inv)) {
            return;
        }
        if (invManagerCleanMode == 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - invManagerLastClean < (long)invManagerCleanDelay) {
            return;
        }
        if (FengBoxClient.invManagerHandleOverflow(client, syncId, inv)) {
            invManagerLastClean = now;
            return;
        }
        if (FengBoxClient.invManagerHandleSpecificOverflow(client, syncId, inv)) {
            invManagerLastClean = now;
            return;
        }
        if (invManagerCleanMode >= 2 && FengBoxClient.invManagerHandleShuffle(client, syncId, inv)) {
            invManagerLastClean = now;
            return;
        }
        invManagerLastClean = now;
    }

    public static boolean invManagerHandleAutoArmor(MinecraftClient client, int syncId) {
        EquipmentSlot[] slots;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        PlayerInventory inv = player.getInventory();
        for (EquipmentSlot es : slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack worn = inv.player.getEquippedStack(es); // TODO: getArmorStack removed
            int best = FengBoxClient.invManagerGetBestArmor(inv, es);
            if (best < 0) continue;
            float wornScore = FengBoxClient.invManagerGetArmorScore(worn);
            float bestScore = FengBoxClient.invManagerGetArmorScore(inv.getStack(best));
            if (!(bestScore > wornScore)) continue;
            client.interactionManager.clickSlot(syncId, FengBoxClient.invManagerScreenSlot(best), 0, SlotActionType.QUICK_MOVE, (PlayerEntity)client.player);
            return true;
        }
        for (int i = 0; i < 36; ++i) {
            EquipmentSlot es;
            ItemStack worn;
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            // TODO: ArmorItem removed in 1.21.2+, armor equip logic needs rewrite
            return true;
        }
        return false;
    }

    public static boolean invManagerHandleOffhand(MinecraftClient client, int syncId) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        PlayerInventory inv = player.getInventory();
        ItemStack off = inv.getStack(40);
        switch (invManagerOffhandMode) {
            case 1: {
                if (off.getItem() == Items.TOTEM_OF_UNDYING) {
                    return false;
                }
                return FengBoxClient.invManagerMoveToOffhand(client, syncId, inv, FengBoxClient.invManagerFindItemInRange(inv, Items.TOTEM_OF_UNDYING, 0, 35));
            }
            case 2: {
                if (off.getItem() == Items.GOLDEN_APPLE || off.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                    return false;
                }
                int slot = FengBoxClient.invManagerFindItemInRange(inv, Items.GOLDEN_APPLE, 0, 35);
                if (slot < 0) {
                    slot = FengBoxClient.invManagerFindItemInRange(inv, Items.ENCHANTED_GOLDEN_APPLE, 0, 35);
                }
                return FengBoxClient.invManagerMoveToOffhand(client, syncId, inv, slot);
            }
            case 3: {
                if (off.getItem() == Items.ENDER_PEARL) {
                    return false;
                }
                return FengBoxClient.invManagerMoveToOffhand(client, syncId, inv, FengBoxClient.invManagerFindItemInRange(inv, Items.ENDER_PEARL, 0, 35));
            }
            case 4: {
                if (off.getItem() instanceof BlockItem) {
                    return false;
                }
                return FengBoxClient.invManagerMoveToOffhand(client, syncId, inv, FengBoxClient.invManagerGetMostBlock(inv));
            }
        }
        return false;
    }

    public static boolean invManagerMoveToOffhand(MinecraftClient client, int syncId, PlayerInventory inv, int invIndex) {
        if (invIndex < 0 || invIndex >= 36) {
            return false;
        }
        ItemStack off = inv.getStack(40);
        if (off.getItem() == inv.getStack(invIndex).getItem() && !off.isEmpty()) {
            return false;
        }
        FengBoxClient.swapSlots(client, syncId, FengBoxClient.invManagerScreenSlot(invIndex), 45);
        return true;
    }

    public static boolean invManagerEnsureBestInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot, int bestIndex, ScoreFunc score) {
        if (bestIndex < 0 || bestIndex == targetHotbarSlot) {
            return false;
        }
        if (score.score(inv.getStack(targetHotbarSlot)) >= score.score(inv.getStack(bestIndex))) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, bestIndex, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerEnsureBowInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot) {
        float curScore = 0.0f;
        int bestBow = FengBoxClient.invManagerGetBestBow(inv);
        int bestCross = FengBoxClient.invManagerGetBestCrossbow(inv);
        int best = -1;
        float bestScore = 0.0f;
        if (bestBow >= 0) {
            best = bestBow;
            bestScore = FengBoxClient.invManagerGetBowScore(inv.getStack(bestBow));
        }
        if (bestCross >= 0 && FengBoxClient.invManagerGetCrossbowScore(inv.getStack(bestCross)) > bestScore) {
            best = bestCross;
            bestScore = FengBoxClient.invManagerGetCrossbowScore(inv.getStack(bestCross));
        }
        if (best < 0 || best == targetHotbarSlot) {
            return false;
        }
        ItemStack current = inv.getStack(targetHotbarSlot);
        Item cur = current.getItem();
        float f = cur instanceof BowItem ? FengBoxClient.invManagerGetBowScore(current) : (curScore = cur instanceof CrossbowItem ? FengBoxClient.invManagerGetCrossbowScore(current) : 0.0f);
        if (curScore >= bestScore) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, best, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerEnsureBlockInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot) {
        int best = FengBoxClient.invManagerGetMostBlock(inv);
        if (best < 0 || best == targetHotbarSlot) {
            return false;
        }
        ItemStack current = inv.getStack(targetHotbarSlot);
        if (current.getItem() instanceof BlockItem && current.getCount() >= inv.getStack(best).getCount()) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, best, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerEnsureGappleInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot) {
        int best = FengBoxClient.invManagerFindItemInRange(inv, Items.GOLDEN_APPLE, 0, 35);
        if (best < 0) {
            best = FengBoxClient.invManagerFindItemInRange(inv, Items.ENCHANTED_GOLDEN_APPLE, 0, 35);
        }
        if (best < 0 || best == targetHotbarSlot) {
            return false;
        }
        Item cur = inv.getStack(targetHotbarSlot).getItem();
        if (cur == Items.GOLDEN_APPLE || cur == Items.ENCHANTED_GOLDEN_APPLE) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, best, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerEnsureRodInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot) {
        int best = FengBoxClient.invManagerGetFishingRodStack(inv);
        if (best < 0 || best == targetHotbarSlot) {
            return false;
        }
        if (inv.getStack(targetHotbarSlot).getItem() instanceof FishingRodItem) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, best, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerEnsureShieldInSlot(MinecraftClient client, int syncId, PlayerInventory inv, int targetHotbarSlot) {
        int best = FengBoxClient.invManagerFindItemInRange(inv, Items.SHIELD, 0, 35);
        if (best < 0 || best == targetHotbarSlot) {
            return false;
        }
        if (inv.getStack(targetHotbarSlot).getItem() == Items.SHIELD) {
            return false;
        }
        FengBoxClient.invManagerSwapToSlot(client, syncId, inv, best, targetHotbarSlot);
        return true;
    }

    public static boolean invManagerHandleHotbarSort(MinecraftClient client, int syncId, PlayerInventory inv) {
        if (invManagerAutoWeapon && FengBoxClient.invManagerEnsureBestInSlot(client, syncId, inv, invManagerSwordSlot, FengBoxClient.invManagerGetBestSword(inv), FengBoxClient::invManagerGetSwordDamage)) {
            return true;
        }
        if (invManagerAutoTools) {
            if (FengBoxClient.invManagerEnsureBestInSlot(client, syncId, inv, invManagerPickaxeSlot, FengBoxClient.invManagerGetBestPickaxe(inv), FengBoxClient::invManagerGetDigSpeed)) {
                return true;
            }
            if (FengBoxClient.invManagerEnsureBestInSlot(client, syncId, inv, invManagerAxeSlot, FengBoxClient.invManagerGetBestAxe(inv), FengBoxClient::invManagerGetDigSpeed)) {
                return true;
            }
            if (FengBoxClient.invManagerEnsureBestInSlot(client, syncId, inv, invManagerShovelSlot, FengBoxClient.invManagerGetBestShovel(inv), FengBoxClient::invManagerGetDigSpeed)) {
                return true;
            }
        }
        if (invManagerAutoBow && FengBoxClient.invManagerEnsureBowInSlot(client, syncId, inv, invManagerBowSlot)) {
            return true;
        }
        if (invManagerSortMode >= 2) {
            if (FengBoxClient.invManagerEnsureBlockInSlot(client, syncId, inv, invManagerBlockSlot)) {
                return true;
            }
            if (FengBoxClient.invManagerEnsureGappleInSlot(client, syncId, inv, invManagerGappleSlot)) {
                return true;
            }
            if (FengBoxClient.invManagerEnsureRodInSlot(client, syncId, inv, invManagerRodSlot)) {
                return true;
            }
            if (FengBoxClient.invManagerEnsureShieldInSlot(client, syncId, inv, invManagerShieldSlot)) {
                return true;
            }
        }
        return false;
    }

    public static void invManagerSwapToSlot(MinecraftClient client, int syncId, PlayerInventory inv, int invIndex, int hotbarSlot) {
        if (invIndex == hotbarSlot || invIndex < 0) {
            return;
        }
        FengBoxClient.swapSlots(client, syncId, FengBoxClient.invManagerScreenSlot(invIndex), hotbarSlot + 36);
    }

    public static boolean invManagerHandleOverflow(MinecraftClient client, int syncId, PlayerInventory inv) {
        ItemStack s;
        int i;
        if (FengBoxClient.invManagerCountBlocks(inv) > invManagerMaxBlocks * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.isEmpty() || !(s.getItem() instanceof BlockItem) || !FengBoxClient.invManagerIsPlaceable(s) || !FengBoxClient.invManagerIsUsable(s) || invManagerFunctionalBlocks && FengBoxClient.invManagerIsFunctionalBlock(s) || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountFood(inv) > invManagerMaxFood * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.isEmpty() || s.get(DataComponentTypes.FOOD) == null || s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE || !FengBoxClient.invManagerIsUsable(s) || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountFishingRods(inv) > 1) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.isEmpty() || !(s.getItem() instanceof FishingRodItem) || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountProjectiles(inv) > invManagerMaxProjectiles * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.isEmpty() || s.getItem() != Items.EGG && s.getItem() != Items.SNOWBALL || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean invManagerHandleSpecificOverflow(MinecraftClient client, int syncId, PlayerInventory inv) {
        ItemStack s;
        int i;
        if (FengBoxClient.invManagerCountItem(inv, Items.ENDER_PEARL) > invManagerMaxPearls * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.getItem() != Items.ENDER_PEARL || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountItem(inv, Items.WATER_BUCKET) + FengBoxClient.invManagerCountItem(inv, Items.LAVA_BUCKET) > invManagerMaxBuckets * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.getItem() != Items.WATER_BUCKET && s.getItem() != Items.LAVA_BUCKET || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountItem(inv, Items.SLIME_BALL) > invManagerMaxSlimeBalls * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.getItem() != Items.SLIME_BALL || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountItem(inv, Items.END_CRYSTAL) > invManagerMaxCrystals * 64) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.getItem() != Items.END_CRYSTAL || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        if (FengBoxClient.invManagerCountArrows(inv) > invManagerMaxArrows) {
            for (i = 0; i < 36; ++i) {
                s = inv.getStack(i);
                if (s.isEmpty() || !(s.getItem() instanceof ArrowItem) || !FengBoxClient.invManagerIsUsable(s) || !FengBoxClient.invManagerThrowStack(client, syncId, i)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean invManagerThrowStack(MinecraftClient client, int syncId, int invIndex) {
        if (invIndex < 0 || invIndex >= 36) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - invManagerLastThrow < (long)invManagerThrowDelay) {
            return false;
        }
        client.interactionManager.clickSlot(syncId, FengBoxClient.invManagerScreenSlot(invIndex), 1, SlotActionType.THROW, (PlayerEntity)client.player);
        invManagerLastThrow = now;
        return true;
    }

    public static boolean invManagerHandleShuffle(MinecraftClient client, int syncId, PlayerInventory inv) {
        List<ItemStack> all = FengBoxClient.invManagerGetAllItems(inv);
        for (int idx = 0; idx < 40; ++idx) {
            ItemStack s = all.get(idx);
            if (s.isEmpty() || idx < 36 && FengBoxClient.invManagerIsProtectedSlot(idx) || FengBoxClient.invManagerIsUsefulItem(client, inv, s)) continue;
            client.interactionManager.clickSlot(syncId, FengBoxClient.invManagerScreenSlot(idx), 1, SlotActionType.THROW, (PlayerEntity)client.player);
            return true;
        }
        return false;
    }

    public static boolean invManagerIsProtectedSlot(int idx) {
        return idx == invManagerSwordSlot || idx == invManagerPickaxeSlot || idx == invManagerAxeSlot || idx == invManagerShovelSlot || idx == invManagerBlockSlot || idx == invManagerGappleSlot || idx == invManagerBowSlot || idx == invManagerRodSlot || idx == invManagerShieldSlot;
    }

    public static void invManagerStopSilentSprint(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        invManagerSprintWasSprinting = player.isSprinting();
        invManagerSprintKeyHeld = client.options.sprintKey.isPressed();
        client.options.sprintKey.setPressed(false);
        NetworkHelper.sendStopSprinting(player);
        player.setSprinting(false);
    }

    public static void invManagerReleaseSilentSprint(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (invManagerSprintKeyHeld) {
            client.options.sprintKey.setPressed(true);
        }
        if (invManagerSprintWasSprinting && FengBoxClient.invManagerCanResumeSprint(player)) {
            NetworkHelper.sendStartSprinting(player);
            player.setSprinting(true);
        }
        invManagerSprintWasSprinting = false;
        invManagerSprintKeyHeld = false;
    }

    public static boolean invManagerCanResumeSprint(ClientPlayerEntity player) {
        return !(!player.input.playerInput.forward() && !player.input.playerInput.backward() && !player.input.playerInput.left() && !player.input.playerInput.right() || player.isSubmergedInWater() || player.isInLava() || player.isSneaking() || player.hasVehicle() || player.getHungerManager() != null && player.getHungerManager().getFoodLevel() <= 6);
    }

    public static long clickerComputePhaseDelay(int selectedCps) {
        if (!clickerBurstActive) {
            clickerLastDelayMs = 1000L / (long)selectedCps;
            if (clickerRandom.nextInt(4) == 1) {
                clickerBurstActive = true;
                clickerBurstLength = 1 + clickerRandom.nextInt(5);
            } else if (clickerRandom.nextInt(10) == 1 && clickerRandom.nextInt(10) == 1) {
                clickerBurstActive = true;
                clickerBurstLength = 5 + clickerRandom.nextInt(10);
            }
        }
        if (clickerBurstActive && ++clickerBurstCount >= clickerBurstLength) {
            clickerBurstCount = 0;
            clickerBurstActive = false;
        }
        if (clickerRandom.nextInt(48) % (clickerFastPhase ? 6 : 10) == 0 && !clickerBurstActive) {
            clickerLastDelayMs += 40L + (long)clickerRandom.nextInt(45);
        }
        if (clickerFastPhase) {
            if (++clickerFastPhaseClicks >= clickerFastPhaseLength) {
                clickerSlowPhaseLength = 75 + clickerRandom.nextInt(125);
                clickerFastPhase = false;
                clickerFastPhaseClicks = 0;
            }
            return clickerLastDelayMs + (clickerRandom.nextInt(5) == 3 ? 50L : 25L);
        }
        if (++clickerSlowPhaseClicks >= clickerSlowPhaseLength) {
            clickerFastPhase = true;
            clickerFastPhaseLength = 7 + clickerRandom.nextInt(8);
            clickerSlowPhaseClicks = 0;
        }
        return clickerLastDelayMs;
    }

    public static long clickerComputeDelayMs() {
        int minCps = Math.max(1, clickerMinCps);
        int maxCps = Math.max(minCps, clickerMaxCps);
        int cpsRangeSize = maxCps - minCps;
        int selectedCps = cpsRangeSize > 0 ? minCps + clickerRandom.nextInt(cpsRangeSize) + 1 : minCps;
        int mode = clickerRandomMode;
        if (mode == 0) {
            return 1000L / (long)selectedCps;
        }
        if (mode == 1) {
            return FengBoxClient.clickerComputePhaseDelay(selectedCps);
        }
        clickerTiming.configureCpsRange(minCps, maxCps);
        return clickerTiming.getNextDelayMillis();
    }

    public static boolean clickerHeldItemAllowed(ClientPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            return false;
        }
        Item it = stack.getItem();
        // TODO: SwordItem, PickaxeItem, ShovelItem, HoeItem removed in 1.21.2+
        return it instanceof AxeItem || it instanceof BowItem || it instanceof CrossbowItem || it instanceof TridentItem || it instanceof FishingRodItem || it instanceof ShieldItem || it instanceof BlockItem || it.getComponents().contains(DataComponentTypes.FOOD);
    }

    public void clickerLeftClick(MinecraftClient client, ClientPlayerEntity player) {
        if (player.isUsingItem() || player.isRiding()) {
            return;
        }
        HitResult hit = client.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            Entity target = client.targetedEntity;
            if (target != null) {
                client.interactionManager.attackEntity((PlayerEntity)player, target);
            } else {
                player.swingHand(Hand.MAIN_HAND);
            }
        } else if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult)hit;
            if (!client.world.getBlockState(blockHit.getBlockPos()).isAir()) {
                client.interactionManager.attackBlock(blockHit.getBlockPos(), blockHit.getSide());
                if (player.isSprinting()) {
                    player.setSprinting(false);
                }
            }
            player.swingHand(Hand.MAIN_HAND);
        } else {
            if (player.isSprinting()) {
                player.setSprinting(false);
            }
            player.swingHand(Hand.MAIN_HAND);
        }
    }

    public void clickerRightClick(MinecraftClient client, ClientPlayerEntity player) {
        ActionResult result;
        BlockHitResult blockHit;
        if (player.isRiding() || player.isUsingItem()) {
            return;
        }
        Hand hand = Hand.MAIN_HAND;
        HitResult hit = client.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK && !client.world.getBlockState((blockHit = (BlockHitResult)hit).getBlockPos()).isAir() && (result = client.interactionManager.interactBlock(player, hand, blockHit)).isAccepted()) {
            player.swingHand(hand);
            return;
        }
        client.interactionManager.interactItem((PlayerEntity)player, hand);
    }

    public void handleAutoClicker(MinecraftClient client) {
        if (!clickerEnabled) {
            clickerAccumMs = 0.0f;
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (!player.isAlive() || freecamEnabled) {
            return;
        }
        if (client.currentScreen != null && !clickerInGui) {
            clickerAccumMs = 0.0f;
            return;
        }
        if (clickerOnlyWeapons && !this.clickerHeldItemAllowed(player)) {
            clickerAccumMs = 0.0f;
            return;
        }
        boolean leftActive = clickerLeft;
        boolean rightActive = clickerRight;
        if (clickerHoldOnly) {
            leftActive = leftActive && client.options.attackKey.isPressed();
            boolean bl = rightActive = rightActive && client.options.useKey.isPressed();
            if (!leftActive && !rightActive) {
                clickerAccumMs = 0.0f;
                return;
            }
        }
        if (!leftActive && !rightActive) {
            clickerAccumMs = 0.0f;
            return;
        }
        long delayMs = this.clickerComputeDelayMs();
        if ((clickerAccumMs += 50.0f) < (float)delayMs) {
            return;
        }
        clickerAccumMs -= (float)delayMs;
        if (leftActive) {
            this.clickerLeftClick(client, player);
        }
        if (rightActive) {
            this.clickerRightClick(client, player);
        }
    }

    public void handleReplenish(MinecraftClient client) {
        long now;
        if (!replenishEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen)) {
            return;
        }
        HandledScreen hs = (HandledScreen)screen;
        ScreenHandler handler = hs.getScreenHandler();
        boolean isInvScreen = screen instanceof InventoryScreen;
        if (!isInvScreen) {
            if (replenishInsideOf == 0) {
                return;
            }
            if (handler.slots.size() <= 36) {
                return;
            }
        }
        if ((now = System.currentTimeMillis()) - lastReplenishTime < (long)replenishDelay) {
            return;
        }
        if (!handler.getCursorStack().isEmpty()) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        for (int h = 0; h < 10; ++h) {
            Item targetItem;
            ItemStack hotbarStack;
            int invSlot = h < 9 ? h : 40;
            ItemStack itemStack = hotbarStack = h < 9 ? inv.getStack(h) : player.getOffHandStack();
            if (hotbarStack.isEmpty()) {
                Item tracked = replenishTracked[h];
                if (tracked == null || tracked == Items.AIR) continue;
                targetItem = tracked;
            } else {
                FengBoxClient.replenishTracked[h] = targetItem = hotbarStack.getItem();
                if (hotbarStack.getCount() > replenishThreshold || hotbarStack.getCount() >= hotbarStack.getMaxCount()) continue;
            }
            int best = -1;
            int bestCount = 0;
            for (int i = 9; i < 36; ++i) {
                ItemStack s = inv.getStack(i);
                if (s.isEmpty() || s.getItem() != targetItem || !s.isStackable() || s.getCount() <= bestCount) continue;
                bestCount = s.getCount();
                best = i;
            }
            if (best < 0) continue;
            int src = this.screenSlotFor(handler, inv, best);
            int dst = this.screenSlotFor(handler, inv, invSlot);
            if (src < 0 || dst < 0) continue;
            client.interactionManager.clickSlot(handler.syncId, src, 0, SlotActionType.PICKUP, (PlayerEntity)player);
            client.interactionManager.clickSlot(handler.syncId, dst, 0, SlotActionType.PICKUP, (PlayerEntity)player);
            client.interactionManager.clickSlot(handler.syncId, src, 0, SlotActionType.PICKUP, (PlayerEntity)player);
            lastReplenishTime = now;
            return;
        }
    }

    public void handleArmorSave(MinecraftClient client) {
        if (!armorSaveEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null || player.isDead()) {
            return;
        }
        if (armorSaveCooldown > 0) {
            --armorSaveCooldown;
            return;
        }
        PlayerInventory inv = player.getInventory();
        int[] wornIdx = new int[]{39, 38, 37, 36};
        int[] screenSlot = new int[]{5, 6, 7, 8};
        String[] suffix = new String[]{"helmet", "chestplate", "leggings", "boots"};
        int syncId = player.playerScreenHandler.syncId;
        for (int k = 0; k < 4; ++k) {
            ItemStack worn = inv.getStack(wornIdx[k]);
            if (worn.isEmpty() || this.durabilityPercent(worn) >= (float)armorSaveThreshold) continue;
            int best = -1;
            int bestTier = -1;
            int bestEnch = -1;
            int bestDur = -1;
            for (int i = 9; i < 36; ++i) {
                String path;
                ItemStack stack;
                if (i == inv.getSelectedSlot() || (stack = inv.getStack(i)).isEmpty() || !(path = Registries.ITEM.getId(stack.getItem()).getPath()).endsWith("_" + suffix[k]) || this.durabilityPercent(stack) < (float)armorSaveThreshold) continue;
                int t = InventoryUtils.getGearTier(stack);
                int e = InventoryUtils.getEnchantScore(stack);
                int d = (int)this.durabilityPercent(stack);
                if (t <= bestTier && (t != bestTier || e <= bestEnch) && (t != bestTier || e != bestEnch || d <= bestDur)) continue;
                bestTier = t;
                bestEnch = e;
                bestDur = d;
                best = i;
            }
            if (best < 0) continue;
            int b = best < 9 ? best + 36 : best;
            this.swapSlots(client, syncId, screenSlot[k], b);
            armorSaveCooldown = armorSaveDelay;
            return;
        }
    }

    public static float durabilityPercent(ItemStack stack) {
        if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
            return 100.0f;
        }
        return (float)(stack.getMaxDamage() - stack.getDamage()) * 100.0f / (float)stack.getMaxDamage();
    }

    public static int screenSlotFor(ScreenHandler handler, PlayerInventory inv, int invSlot) {
        for (int i = 0; i < handler.slots.size(); ++i) {
            Slot s = (Slot)handler.slots.get(i);
            if (s.inventory != inv || s.getIndex() != invSlot) continue;
            return i;
        }
        return -1;
    }

    public void handleAutoTool(MinecraftClient client) {
        if (!autoToolEnabled) {
            return;
        }
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (!client.options.attackKey.isPressed()) {
            return;
        }
        if (client.crosshairTarget == null || client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult hit = (BlockHitResult)client.crosshairTarget;
        BlockState state = client.world.getBlockState(hit.getBlockPos());
        int bestSlot = -1;
        float bestSpeed = 0.0f;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = client.player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (!(speed > bestSpeed)) continue;
            bestSpeed = speed;
            bestSlot = i;
        }
        if (bestSlot >= 0 && bestSlot != client.player.getInventory().getSelectedSlot()) {
            client.player.getInventory().setSelectedSlot(bestSlot);
        }
    }

    public void handleAutoRespawn(MinecraftClient client) {
        if (!autoRespawnEnabled) {
            return;
        }
        if (client.player == null || client.currentScreen == null) {
            return;
        }
        if (client.currentScreen.getClass().getName().contains("DeathScreen")) {
            client.setScreen(null);
        }
    }

    public void handleAntiAfk(MinecraftClient client) {
        if (!antiAfkEnabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastAfkTime < 15000L) {
            return;
        }
        lastAfkTime = now;
        if (!player.isOnGround()) {
            return;
        }
        player.jump();
    }

    public void handleAutoEat(MinecraftClient client) {
        if (!autoEatEnabled) {
            if (isAutoEating) {
                isAutoEating = false;
                client.options.useKey.setPressed(false);
            }
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null || client.currentScreen != null) {
            if (isAutoEating) {
                isAutoEating = false;
                if (client.options != null) {
                    client.options.useKey.setPressed(false);
                }
            }
            return;
        }
        if (autoEatCombatPause && this.nearbyHostile(client)) {
            if (isAutoEating) {
                isAutoEating = false;
                client.options.useKey.setPressed(false);
            }
            return;
        }
        if (player.isUsingItem()) {
            if (isAutoEating) {
                client.options.useKey.setPressed(true);
            }
            return;
        }
        if (autoEatCooldown > 0) {
            --autoEatCooldown;
            return;
        }
        int slot = -1;
        if (player.getHealth() < autoEatHealHealth && player.getHealth() < player.getMaxHealth()) {
            slot = this.findHealItemSlot(player.getInventory());
        }
        if (slot < 0 && player.getHungerManager() != null && player.getHungerManager().getFoodLevel() <= autoEatHungerThreshold) {
            slot = this.findHighSaturationSlot(player.getInventory());
        }
        if (slot < 0) {
            if (isAutoEating) {
                isAutoEating = false;
                client.options.useKey.setPressed(false);
            }
            return;
        }
        if (slot >= 9) {
            int free = -1;
            for (int i = 0; i < 9; ++i) {
                if (!player.getInventory().getStack(i).isEmpty()) continue;
                free = i;
                break;
            }
            if (free < 0) {
                return;
            }
            this.swapSlots(client, player.playerScreenHandler.syncId, free + 36, slot);
            autoEatCooldown = 12;
            return;
        }
        player.getInventory().setSelectedSlot(slot);
        client.interactionManager.interactItem((PlayerEntity)player, Hand.MAIN_HAND);
        isAutoEating = true;
        client.options.useKey.setPressed(true);
        autoEatCooldown = 20;
    }

    public boolean nearbyHostile(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return false;
        }
        double r = autoEatCombatRange;
        double r2 = r * r;
        for (PlayerEntity p : client.world.getPlayers()) {
            if (p == player || !p.isAlive() || !(p.squaredDistanceTo((Entity)player) <= r2)) continue;
            return true;
        }
        for (Entity e : client.world.getEntities()) {
            LivingEntity le;
            if (e == player || !e.isAlive() || e.squaredDistanceTo((Entity)player) > r2 || !(e instanceof LivingEntity) || !((le = (LivingEntity)e) instanceof Monster)) continue;
            return true;
        }
        return false;
    }

    public int findHighSaturationSlot(PlayerInventory inv) {
        int best = -1;
        double bestScore = -1.0;
        for (int i = 0; i < 36; ++i) {
            double score;
            Item item;
            FoodComponent food;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || (food = stack.get(DataComponentTypes.FOOD)) == null || !((score = (double)food.nutrition() + (double)food.saturation() * 2.0) > bestScore)) continue;
            bestScore = score;
            best = i;
        }
        return best;
    }

    public int findHealItemSlot(PlayerInventory inv) {
        int best = -1;
        int bestScore = -1;
        for (int i = 0; i < 36; ++i) {
            int score;
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || (score = this.healScore(stack)) <= bestScore) continue;
            bestScore = score;
            best = i;
        }
        return best;
    }

    private int healScore(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.ENCHANTED_GOLDEN_APPLE) {
            return 100;
        }
        if (item == Items.GOLDEN_APPLE) {
            return 90;
        }
        if (item == Items.POTION) {
            PotionContentsComponent potComp = stack.get(DataComponentTypes.POTION_CONTENTS);
            RegistryEntry<Potion> p = potComp == null ? null : potComp.potion().orElse(null);
            if (p == Potions.STRONG_HEALING) {
                return 80;
            }
            if (p == Potions.HEALING) {
                return 70;
            }
        }
        if (item == Items.GOLDEN_CARROT) {
            return 50;
        }
        return -1;
    }




    public void handleDummyInteract(MinecraftClient client) {
        boolean attackPressed;
        ItemStack held;
        if (testDummy == null || client.player == null || client.interactionManager == null) {
            return;
        }
        if (client.targetedEntity == testDummy && client.options.useKey.wasPressed() && !(held = client.player.getMainHandStack()).isEmpty()) {
            Item item = held.getItem();
            // TODO: ArmorItem removed in 1.21.2+, test dummy armor equip disabled
            /*
            if (item instanceof ArmorItem) {
                int armorIndex = -1;
                // ArmorItem armor = (ArmorItem)item;
                switch (armor.getSlotType()) {
                    case HEAD: armorIndex = 3; break;
                    case CHEST: armorIndex = 2; break;
                    case LEGS: armorIndex = 1; break;
                    case FEET: armorIndex = 0; break;
                    default: armorIndex = -1;
                }
                if (armorIndex >= 0) {
                    FengBoxClient.testDummy.equipStack(net.minecraft.entity.EquipmentSlot.fromTypeIndex(net.minecraft.entity.EquipmentType.ARMOR, armorIndex), held.copy());
                }
            } else {
            */
            FengBoxClient.testDummy.getInventory().getMainStacks().set(FengBoxClient.testDummy.getInventory().getSelectedSlot(), held.copy());
            client.player.swingHand(Hand.MAIN_HAND);
        }
        if ((attackPressed = client.options.attackKey.isPressed()) && !dummyAttackWasPressed && client.targetedEntity == testDummy) {
            float dmg = 4.0f;
            testDummy.setHealth(testDummy.getHealth() - dmg);
            FengBoxClient.testDummy.hurtTime = 10;
            if (testDummy.getHealth() <= 0.0f) {
                testDummy.setHealth(0.0f);
                NetworkHelper.removeDummySkinEntry(client, FengBoxClient.testDummy);
                testDummy.discard();
                testDummy = null;
            }
        }
        dummyAttackWasPressed = attackPressed;
        if (testDummy != null && testDummy.isOnGround() && FengBoxClient.testDummy.fallDistance > 3.0f) {
            float fallDmg = (float)(FengBoxClient.testDummy.fallDistance - 3.0f) * 0.5f;
            testDummy.setHealth(testDummy.getHealth() - fallDmg);
            FengBoxClient.testDummy.fallDistance = 0.0f;
            if (testDummy.getHealth() <= 0.0f) {
                testDummy.setHealth(0.0f);
                NetworkHelper.removeDummySkinEntry(client, FengBoxClient.testDummy);
                testDummy.discard();
                testDummy = null;
            }
        }
    }

    public void updateAnimations(MinecraftClient client) {
        boolean[] active = this.getFeatureStates(client);
        for (int i = 0; i < 50; ++i) {
            float target = active[i] ? 1.0f : 0.0f;
            float current = featureAlphas[i];
            FengBoxClient.featureAlphas[i] = Math.abs(current - target) > 0.001f ? current + (target - current) * 0.25f : target;
        }
        boolean anyActive = false;
        for (boolean a : active) {
            if (!a) continue;
            anyActive = true;
            break;
        }
        float slideTarget = anyActive ? 0.0f : 400.0f;
        float slideDiff = slideTarget - slideOffset;
        slideVel += slideDiff * 0.12f;
        slideOffset += (slideVel *= 0.82f);
        if (Math.abs(slideDiff) < 0.5f && Math.abs(slideVel) < 0.1f) {
            slideOffset = slideTarget;
            slideVel = 0.0f;
        }
        float piTarget = playerInfoEnabled ? 0.0f : -300.0f;
        float piDiff = piTarget - playerInfoSlideOffset;
        piSlideVel += piDiff * 0.15f;
        playerInfoSlideOffset += (piSlideVel *= 0.8f);
        if (Math.abs(piDiff) < 0.5f && Math.abs(piSlideVel) < 0.1f) {
            playerInfoSlideOffset = piTarget;
            piSlideVel = 0.0f;
        }
    }

    private boolean[] getFeatureStates(MinecraftClient client) {
        boolean flying = this.isMultiplayer(client) ? flightEnabled : client.player != null && client.player.getAbilities().allowFlying;
        return new boolean[]{autoCritEnabled, flying, freecamEnabled, clearEffectsEnabled, antiFallEnabled, nightVisionEnabled, noHurtShakeEnabled, playerInfoEnabled, autoCrystalEnabled, autoTotemEnabled, storageEspEnabled, playerTrackerEnabled, autoBridgeEnabled, spinEnabled, themeMode != 0, hitboxEspEnabled, killAuraEnabled, speedEnabled, stepEnabled, sprintEnabled, autoWalkEnabled, fullbrightEnabled, tracersEnabled, nameTagsEnabled, itemEspEnabled, chestStealerEnabled, autoToolEnabled, autoRespawnEnabled, antiAfkEnabled, autoEatEnabled, hotbarOrganizerEnabled, customOutlineEnabled, reachEnabled, multiJumpEnabled, autoAnchorEnabled, guiMoveEnabled, entityOutlineEnabled, replenishEnabled, armorSaveEnabled, boxEspEnabled, autoRodEnabled, bowAimEnabled, fastUseEnabled, fastBreakEnabled, autoBlockEnabled, projectileDodgeEnabled, totemGhostEnabled, itemPhysicsEnabled, autoWebEnabled, violentMode};
    }

    public static int lerpColor(int from, int to, float t) {
        if (t <= 0.0f) {
            return from;
        }
        if (t >= 1.0f) {
            return to;
        }
        int a1 = from >> 24 & 0xFF;
        int r1 = from >> 16 & 0xFF;
        int g1 = from >> 8 & 0xFF;
        int b1 = from & 0xFF;
        int a2 = to >> 24 & 0xFF;
        int r2 = to >> 16 & 0xFF;
        int g2 = to >> 8 & 0xFF;
        int b2 = to & 0xFF;
        return (int)((float)a1 + (float)(a2 - a1) * t) << 24 | (int)((float)r1 + (float)(r2 - r1) * t) << 16 | (int)((float)g1 + (float)(g2 - g1) * t) << 8 | (int)((float)b1 + (float)(b2 - b1) * t);
    }

    public void handleFlight(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (!flightEnabled) {
            return;
        }
        this.handleVelocityFlight(client);
    }

    public void handleVelocityFlight(MinecraftClient client) {
        Vec3d vel;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        double forward = client.options.forwardKey.isPressed() ? 1.0 : 0.0;
        double back = client.options.backKey.isPressed() ? 1.0 : 0.0;
        double left = client.options.leftKey.isPressed() ? 1.0 : 0.0;
        double right = client.options.rightKey.isPressed() ? 1.0 : 0.0;
        double up = client.options.jumpKey.isPressed() ? 1.0 : 0.0;
        double down = client.options.sneakKey.isPressed() ? 1.0 : 0.0;
        float yawRad = player.getYaw() * ((float)Math.PI / 180);
        double strafe = left - right;
        double forwardBack = forward - back;
        double mx = strafe * Math.cos(yawRad) - forwardBack * Math.sin(yawRad);
        double mz = strafe * Math.sin(yawRad) + forwardBack * Math.cos(yawRad);
        double my = up - down;
        if (mx == 0.0 && mz == 0.0 && my == 0.0) {
            vel = Vec3d.ZERO;
        } else {
            vel = new Vec3d(mx, my, mz).normalize().multiply((double)flightSpeed);
            if (my == 0.0) {
                vel = new Vec3d(vel.x, 0.0, vel.z);
            }
        }
        if (flightStealth) {
            Vec3d cur = player.getVelocity();
            double ramp = 0.3;
            vel = new Vec3d(this.approach(cur.x, vel.x, ramp), this.approach(cur.y, vel.y, ramp), this.approach(cur.z, vel.z, ramp));
        }
        player.setVelocity(vel);
        // player.velocityModified = true; // TODO: field removed in 1.21.2+
    }

    public void handleElytraFlight(MinecraftClient client) {
        double d;
        boolean wantsBoost;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem() != Items.ELYTRA) {
            this.tryEquipElytra(client);
            chest = player.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.getItem() != Items.ELYTRA) {
                return;
            }
        }
        if (!player.isGliding() && player.isOnGround()) {
            player.jump();
        }
        if (!player.isGliding()) {
            return;
        }
        boolean bl = wantsBoost = client.options.forwardKey.isPressed() || client.options.jumpKey.isPressed();
        if (!wantsBoost) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - elytraLastFireworkTime < (long)elytraFireworkCooldownMs) {
            return;
        }
        if (elytraFireworkSyncAura && killAuraEnabled && killAuraTarget != null && (d = player.getEyePos().distanceTo(new Vec3d(killAuraTarget.getX(), killAuraTarget.getY(), killAuraTarget.getZ()))) <= (double)killAuraRange) {
            return;
        }
        this.autoUseFirework(client);
        elytraLastFireworkTime = now;
    }

    public void autoUseFirework(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            if (player.getInventory().getStack(i).getItem() != Items.FIREWORK_ROCKET) continue;
            int prevSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(i);
            client.interactionManager.interactItem((PlayerEntity)player, Hand.MAIN_HAND);
            player.getInventory().setSelectedSlot(prevSlot);
            return;
        }
    }

    public void tryEquipElytra(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }
        for (int i = 0; i < 36; ++i) {
            if (player.getInventory().getStack(i).getItem() != Items.ELYTRA) continue;
            int containerSlot = i < 9 ? i + 36 : i;
            int syncId = player.playerScreenHandler.syncId;
            client.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)player);
            return;
        }
    }

    public static void flipField(BooleanSupplier getter, Consumer<Boolean> setter, String langKey) {
        boolean on = !getter.getAsBoolean();
        setter.accept(on);
        FengBoxClient.sendCmdMsg(langKey, on);
    }

    public static CmdFeature[] buildCommandFeatures() {
        return new CmdFeature[]{new CmdFeature("auto_crit", () -> FengBoxClient.flipField(() -> autoCritEnabled, v -> {
            autoCritEnabled = v;
        }, "msg.feng_box.auto_crit"), () -> autoCritEnabled), new CmdFeature("flight", () -> FengBoxClient.toggleFlight(MinecraftClient.getInstance()), () -> flightEnabled), new CmdFeature("freecam", () -> FengBoxClient.toggleFreecam(MinecraftClient.getInstance()), () -> freecamEnabled), new CmdFeature("clear_effects", () -> FengBoxClient.flipField(() -> clearEffectsEnabled, v -> {
            clearEffectsEnabled = v;
        }, "msg.feng_box.clear_effects"), () -> clearEffectsEnabled), new CmdFeature("anti_fall", () -> FengBoxClient.flipField(() -> antiFallEnabled, v -> {
            antiFallEnabled = v;
        }, "msg.feng_box.anti_fall"), () -> antiFallEnabled), new CmdFeature("night_vision", () -> FengBoxClient.flipField(() -> nightVisionEnabled, v -> {
            nightVisionEnabled = v;
        }, "msg.feng_box.night_vision"), () -> nightVisionEnabled), new CmdFeature("no_hurt_shake", () -> FengBoxClient.flipField(() -> noHurtShakeEnabled, v -> {
            noHurtShakeEnabled = v;
        }, "msg.feng_box.no_hurt_shake"), () -> noHurtShakeEnabled), new CmdFeature("player_info", () -> FengBoxClient.flipField(() -> playerInfoEnabled, v -> {
            playerInfoEnabled = v;
        }, "msg.feng_box.player_info"), () -> playerInfoEnabled), new CmdFeature("auto_crystal", () -> {
            boolean bl = autoCrystalEnabled = !autoCrystalEnabled;
            if (!autoCrystalEnabled) {
                crystalStep = 0;
                crystalCooldown = 0;
                crystalPlacePos = null;
                crystalRetries = 0;
            }
            FengBoxClient.sendCmdMsg("msg.feng_box.auto_crystal", autoCrystalEnabled);
        }, () -> autoCrystalEnabled), new CmdFeature("auto_totem", () -> FengBoxClient.flipField(() -> autoTotemEnabled, v -> {
            autoTotemEnabled = v;
        }, "msg.feng_box.auto_totem"), () -> autoTotemEnabled), new CmdFeature("storage_esp", () -> FengBoxClient.flipField(() -> storageEspEnabled, v -> {
            storageEspEnabled = v;
        }, "msg.feng_box.storage_esp"), () -> storageEspEnabled), new CmdFeature("player_tracker", () -> FengBoxClient.flipField(() -> playerTrackerEnabled, v -> {
            playerTrackerEnabled = v;
        }, "msg.feng_box.player_tracker"), () -> playerTrackerEnabled), new CmdFeature("auto_bridge", () -> FengBoxClient.flipField(() -> autoBridgeEnabled, v -> {
            autoBridgeEnabled = v;
        }, "msg.feng_box.auto_bridge"), () -> autoBridgeEnabled), new CmdFeature("spin", () -> FengBoxClient.flipField(() -> spinEnabled, v -> {
            spinEnabled = v;
        }, "msg.feng_box.spin"), () -> spinEnabled), new CmdFeature("white_theme", () -> {
            themeMode = themeMode == 0 ? 1 : 0;
            FengBoxClient.sendCmdMsg("msg.feng_box.white_theme", themeMode != 0);
        }, () -> themeMode != 0), new CmdFeature("hitbox_esp", () -> FengBoxClient.flipField(() -> hitboxEspEnabled, v -> {
            hitboxEspEnabled = v;
        }, "msg.feng_box.hitbox_esp"), () -> hitboxEspEnabled), new CmdFeature("kill_aura", () -> FengBoxClient.flipField(() -> killAuraEnabled, v -> {
            killAuraEnabled = v;
        }, "msg.feng_box.kill_aura"), () -> killAuraEnabled), new CmdFeature("auto_anchor", () -> {
            boolean bl = autoAnchorEnabled = !autoAnchorEnabled;
            if (!autoAnchorEnabled) {
                anchorStep = 0;
                anchorCooldown = 0;
                anchorPos = null;
                anchorRetries = 0;
            }
            FengBoxClient.sendCmdMsg("msg.feng_box.auto_anchor", autoAnchorEnabled);
        }, () -> autoAnchorEnabled), new CmdFeature("reach", () -> FengBoxClient.flipField(() -> reachEnabled, v -> {
            reachEnabled = v;
        }, "msg.feng_box.reach"), () -> reachEnabled), new CmdFeature("speed", () -> FengBoxClient.flipField(() -> speedEnabled, v -> {
            speedEnabled = v;
        }, "msg.feng_box.speed"), () -> speedEnabled), new CmdFeature("step", () -> FengBoxClient.flipField(() -> stepEnabled, v -> {
            stepEnabled = v;
        }, "msg.feng_box.step"), () -> stepEnabled), new CmdFeature("sprint", () -> FengBoxClient.flipField(() -> sprintEnabled, v -> {
            sprintEnabled = v;
        }, "msg.feng_box.sprint"), () -> sprintEnabled), new CmdFeature("multi_jump", () -> FengBoxClient.flipField(() -> multiJumpEnabled, v -> {
            multiJumpEnabled = v;
        }, "msg.feng_box.multi_jump"), () -> multiJumpEnabled), new CmdFeature("gui_move", () -> {
            boolean bl = guiMoveEnabled = !guiMoveEnabled;
            if (!guiMoveEnabled) {
                FengBoxClient.releaseGuiMoveKeys();
            }
            FengBoxClient.sendCmdMsg("msg.feng_box.gui_move", guiMoveEnabled);
        }, () -> guiMoveEnabled), new CmdFeature("entity_outline", () -> FengBoxClient.flipField(() -> entityOutlineEnabled, v -> {
            entityOutlineEnabled = v;
        }, "msg.feng_box.entity_outline"), () -> entityOutlineEnabled), new CmdFeature("box_esp", () -> FengBoxClient.flipField(() -> boxEspEnabled, v -> {
            boxEspEnabled = v;
        }, "msg.feng_box.box_esp"), () -> boxEspEnabled), new CmdFeature("auto_walk", () -> FengBoxClient.flipField(() -> autoWalkEnabled, v -> {
            autoWalkEnabled = v;
        }, "msg.feng_box.auto_walk"), () -> autoWalkEnabled), new CmdFeature("fullbright", () -> FengBoxClient.flipField(() -> fullbrightEnabled, v -> {
            fullbrightEnabled = v;
        }, "msg.feng_box.fullbright"), () -> fullbrightEnabled), new CmdFeature("tracers", () -> FengBoxClient.flipField(() -> tracersEnabled, v -> {
            tracersEnabled = v;
        }, "msg.feng_box.tracers"), () -> tracersEnabled), new CmdFeature("name_tags", () -> FengBoxClient.flipField(() -> nameTagsEnabled, v -> {
            nameTagsEnabled = v;
        }, "msg.feng_box.name_tags"), () -> nameTagsEnabled), new CmdFeature("item_esp", () -> FengBoxClient.flipField(() -> itemEspEnabled, v -> {
            itemEspEnabled = v;
        }, "msg.feng_box.item_esp"), () -> itemEspEnabled), new CmdFeature("chest_stealer", () -> FengBoxClient.flipField(() -> chestStealerEnabled, v -> {
            chestStealerEnabled = v;
        }, "msg.feng_box.chest_stealer"), () -> chestStealerEnabled), new CmdFeature("auto_tool", () -> FengBoxClient.flipField(() -> autoToolEnabled, v -> {
            autoToolEnabled = v;
        }, "msg.feng_box.auto_tool"), () -> autoToolEnabled), new CmdFeature("auto_respawn", () -> FengBoxClient.flipField(() -> autoRespawnEnabled, v -> {
            autoRespawnEnabled = v;
        }, "msg.feng_box.auto_respawn"), () -> autoRespawnEnabled), new CmdFeature("anti_afk", () -> FengBoxClient.flipField(() -> antiAfkEnabled, v -> {
            antiAfkEnabled = v;
        }, "msg.feng_box.anti_afk"), () -> antiAfkEnabled), new CmdFeature("auto_eat", () -> FengBoxClient.flipField(() -> autoEatEnabled, v -> {
            autoEatEnabled = v;
        }, "msg.feng_box.auto_eat"), () -> autoEatEnabled), new CmdFeature("hotbar_organizer", () -> FengBoxClient.flipField(() -> hotbarOrganizerEnabled, v -> {
            hotbarOrganizerEnabled = v;
        }, "msg.feng_box.hotbar_organizer"), () -> hotbarOrganizerEnabled), new CmdFeature("replenish", () -> FengBoxClient.flipField(() -> replenishEnabled, v -> {
            replenishEnabled = v;
        }, "msg.feng_box.replenish"), () -> replenishEnabled), new CmdFeature("armor_save", () -> FengBoxClient.flipField(() -> armorSaveEnabled, v -> {
            armorSaveEnabled = v;
        }, "msg.feng_box.armor_save"), () -> armorSaveEnabled), new CmdFeature("inv_manager", () -> FengBoxClient.flipField(() -> invManagerEnabled, v -> {
            invManagerEnabled = v;
        }, "msg.feng_box.inv_manager"), () -> invManagerEnabled), new CmdFeature("clicker", () -> FengBoxClient.flipField(() -> clickerEnabled, v -> {
            clickerEnabled = v;
        }, "msg.feng_box.clicker"), () -> clickerEnabled), new CmdFeature("custom_outline", () -> FengBoxClient.flipField(() -> customOutlineEnabled, v -> {
            customOutlineEnabled = v;
        }, "msg.feng_box.custom_outline"), () -> customOutlineEnabled), new CmdFeature("auto_rod", () -> FengBoxClient.flipField(() -> autoRodEnabled, v -> {
            autoRodEnabled = v;
        }, "msg.feng_box.auto_rod"), () -> autoRodEnabled), new CmdFeature("auto_web", () -> FengBoxClient.flipField(() -> autoWebEnabled, v -> {
            autoWebEnabled = v;
        }, "msg.feng_box.auto_web"), () -> autoWebEnabled), new CmdFeature("bow_aim", () -> FengBoxClient.flipField(() -> bowAimEnabled, v -> {
            bowAimEnabled = v;
        }, "msg.feng_box.bow_aim"), () -> bowAimEnabled), new CmdFeature("fast_use", () -> FengBoxClient.flipField(() -> fastUseEnabled, v -> {
            fastUseEnabled = v;
        }, "msg.feng_box.fast_use"), () -> fastUseEnabled), new CmdFeature("fast_break", () -> FengBoxClient.flipField(() -> fastBreakEnabled, v -> {
            fastBreakEnabled = v;
        }, "msg.feng_box.fast_break"), () -> fastBreakEnabled), new CmdFeature("auto_block", () -> FengBoxClient.flipField(() -> autoBlockEnabled, v -> {
            autoBlockEnabled = v;
        }, "msg.feng_box.auto_block"), () -> autoBlockEnabled), new CmdFeature("projectile_dodge", () -> FengBoxClient.flipField(() -> projectileDodgeEnabled, v -> {
            projectileDodgeEnabled = v;
        }, "msg.feng_box.projectile_dodge"), () -> projectileDodgeEnabled), new CmdFeature("totem_ghost", () -> FengBoxClient.flipField(() -> totemGhostEnabled, v -> {
            totemGhostEnabled = v;
        }, "msg.feng_box.totem_ghost"), () -> totemGhostEnabled), new CmdFeature("item_physics", () -> FengBoxClient.flipField(() -> itemPhysicsEnabled, v -> {
            itemPhysicsEnabled = v;
        }, "msg.feng_box.item_physics"), () -> itemPhysicsEnabled), new CmdFeature("mace_crit", () -> FengBoxClient.flipField(() -> maceCritEnabled, v -> {
            maceCritEnabled = v;
        }, "msg.feng_box.mace_crit"), () -> maceCritEnabled), new CmdFeature("trident_impulse", () -> FengBoxClient.flipField(() -> tridentImpulseEnabled, v -> { tridentImpulseEnabled = v; }, "msg.feng_box.trident_impulse"), () -> tridentImpulseEnabled), new CmdFeature("menu_bg", () -> {
            titleBgEnabled = !titleBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.menu_bg", titleBgEnabled);
        }, () -> titleBgEnabled), new CmdFeature("setting_bg", () -> {
            settingBgEnabled = !settingBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.setting_bg", settingBgEnabled);
        }, () -> settingBgEnabled), new CmdFeature("mp_bg", () -> {
            mpBgEnabled = !mpBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.mp_bg", mpBgEnabled);
        }, () -> mpBgEnabled), new CmdFeature("game_bg", () -> {
            gameBgEnabled = !gameBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.game_bg", gameBgEnabled);
        }, () -> gameBgEnabled), new CmdFeature("loading_bg", () -> {
            loadingBgEnabled = !loadingBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.loading_bg", loadingBgEnabled);
        }, () -> loadingBgEnabled), new CmdFeature("custom_bg", () -> {
            customBgEnabled = !customBgEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.custom_bg", customBgEnabled);
        }, () -> customBgEnabled), new CmdFeature("cape", () -> {
            capeEnabled = !capeEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.cape", capeEnabled);
        }, () -> capeEnabled), new CmdFeature("elytra_skin", () -> {
            elytraSkinEnabled = !elytraSkinEnabled;
            FengBoxClient.saveConfig();
            FengBoxClient.sendCmdMsg("msg.feng_box.elytra_skin", elytraSkinEnabled);
        }, () -> elytraSkinEnabled)};
    }

    public static void sendCmdMsg(String langKey, boolean on) {
        FengBoxClient.sendRawCmd((Text)Text.translatable((String)(langKey + "." + (on ? "on" : "off"))));
    }

    public static void sendRawCmd(Text t) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(t, false);
        } else if (client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(t);
        }
    }

    public static boolean handleCommand(String message) {
        String sub;
        String m = message.trim();
        if (!(m.equals(".fb") || m.startsWith(".fb ") || m.startsWith(".fb\t"))) {
            return false;
        }
        String[] parts = m.split("\\s+");
        if (parts.length == 1) {
            FengBoxClient.sendCmdHelp();
            return true;
        }
        switch (sub = parts[1].toLowerCase()) {
            case "help": 
            case "?": {
                FengBoxClient.sendCmdHelp();
                break;
            }
            case "list": {
                FengBoxClient.sendCmdList();
                break;
            }
            case "gui": {
                // GUI removed
                break;
            }
            case "spawn": 
            case "dummy": 
            case "test_dummy": {
                FengBoxClient.toggleTestDummy(MinecraftClient.getInstance());
                break;
            }
            case "toggle": {
                FengBoxClient.cmdSet(parts.length > 2 ? parts[2] : null, null);
                break;
            }
            case "on": {
                FengBoxClient.cmdSet(parts.length > 2 ? parts[2] : null, true);
                break;
            }
            case "off": {
                FengBoxClient.cmdSet(parts.length > 2 ? parts[2] : null, false);
                break;
            }
            default: {
                FengBoxClient.cmdSet(sub, null);
            }
        }
        return true;
    }

    public static void cmdSet(String name, Boolean state) {
        if (name == null || name.isEmpty()) {
            FengBoxClient.sendRawCmd((Text)Text.translatable((String)"cmd.feng_box.usage"));
            return;
        }
        String target = name.toLowerCase();
        CmdFeature found = null;
        for (CmdFeature f : CMD_FEATURES) {
            if (f.name.equals(target)) {
                found = f;
                break;
            }
            if (found != null || !f.name.startsWith(target)) continue;
            found = f;
        }
        if (found == null) {
            FengBoxClient.sendRawCmd((Text)Text.translatable((String)"cmd.feng_box.unknown", (Object[])new Object[]{name}));
            return;
        }
        boolean on = found.isOn.get();
        if (state != null && on == state) {
            FengBoxClient.sendRawCmd((Text)Text.translatable((String)"cmd.feng_box.already", (Object[])new Object[]{Text.translatable((String)("gui.feng_box." + found.name)), on ? "\u00a7a\u2714" : "\u00a78\u2718"}));
            return;
        }
        found.toggle.run();
    }

    public static void sendCmdHelp() {
        FengBoxClient.sendRawCmd((Text)Text.translatable((String)"cmd.feng_box.help"));
    }

    public static void sendCmdList() {
        StringBuilder sb = new StringBuilder();
        for (CmdFeature f : CMD_FEATURES) {
            sb.append(Text.translatable((String)("gui.feng_box." + f.name)).getString()).append(f.isOn.get() != false ? " \u00a7a\u2714" : " \u00a78\u2718").append("\n");
        }
        FengBoxClient.sendRawCmd((Text)Text.literal((String)sb.toString()));
    }

    public static boolean isSettingsScreen(Screen s) {
        if (s instanceof CreditsScreen) {
            return true;
        }
        Package p = s.getClass().getPackage();
        return p != null && "net.minecraft.client.gui.screen.option".equals(p.getName());
    }

    public static boolean isMultiplayerScreen(Screen s) {
        Package p = s.getClass().getPackage();
        if (p != null && "net.minecraft.client.gui.screen.multiplayer".equals(p.getName())) {
            return true;
        }
        String cn = s.getClass().getSimpleName();
        return cn.equals("DirectConnectScreen") || cn.equals("AddServerScreen");
    }

    public static boolean isLoadingScreen(Screen s) {
        String cn = s.getClass().getSimpleName();
        return cn.equals("DownloadingTerrainScreen") || cn.equals("LevelLoadingScreen") || cn.equals("ConnectScreen") || cn.equals("ProgressScreen") || cn.equals("TaskScreen") || cn.equals("OptimizeWorldScreen");
    }

    public static boolean shouldRenderBg(Screen screen) {
        if (!customBgEnabled) {
            return false;
        }
        if (FengBoxClient.isLoadingScreen(screen)) {
            return loadingBgEnabled;
        }
        if (MinecraftClient.getInstance().world != null) {
            return gameBgEnabled;
        }
        if (FengBoxClient.isSettingsScreen(screen)) {
            return settingBgEnabled;
        }
        if (FengBoxClient.isMultiplayerScreen(screen)) {
            return mpBgEnabled;
        }
        return titleBgEnabled;
    }

    public static boolean shouldSkipListBackground() {
        if (!customBgEnabled) {
            return false;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        Screen s = mc.currentScreen;
        if (s == null) {
            return false;
        }
        if (mc.world != null) {
            return gameBgEnabled;
        }
        if (s instanceof SelectWorldScreen) {
            return titleBgEnabled;
        }
        if (FengBoxClient.isSettingsScreen(s)) {
            return settingBgEnabled;
        }
        if (FengBoxClient.isMultiplayerScreen(s)) {
            return mpBgEnabled;
        }
        return false;
    }

    public static boolean shouldSkipBgEffects() {
        return bgAlpha > 0.005f;
    }

    public static boolean renderScreenBackground(DrawContext context, Screen screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        if (FengBoxClient.isLoadingScreen(screen)) {
            if (!FengBoxClient.shouldRenderBg(screen)) {
                lastStaticBg = null;
                crossBg = null;
                return false;
            }
            LOADING_BG.ensure();
            LOADING_BG.render(context, w, h, 1.0f);
            return true;
        }
        float target = FengBoxClient.shouldRenderBg(screen) ? 1.0f : 0.0f;
        bgAlpha += (target - bgAlpha) * 0.14f;
        if (target == 0.0f && bgAlpha < 0.005f) {
            bgAlpha = 0.0f;
        } else if (target == 1.0f && bgAlpha > 0.995f) {
            bgAlpha = 1.0f;
        }
        if (bgAlpha <= 0.005f) {
            lastStaticBg = null;
            crossBg = null;
            return false;
        }
        BgTex bg = MinecraftClient.getInstance().world != null ? GAME_BG : (FengBoxClient.isSettingsScreen(screen) ? SETTING_BG : (FengBoxClient.isMultiplayerScreen(screen) ? MP_BG : MENU_BG));
        bg.ensure();
        if (lastStaticBg != null && lastStaticBg != bg && crossBg == null) {
            crossBg = lastStaticBg;
            crossAlpha = 1.0f;
        }
        lastStaticBg = bg;
        RenderUtils.drawImageCover(context, bg.id, bg.texW, bg.texH, w, h, bgAlpha);
        if (crossBg != null) {
            crossBg.ensure();
            crossAlpha -= 0.09f;
            if (crossAlpha > 0.0f) {
                RenderUtils.drawImageCover(context, FengBoxClient.crossBg.id, FengBoxClient.crossBg.texW, FengBoxClient.crossBg.texH, w, h, crossAlpha * bgAlpha);
            } else {
                crossBg = null;
            }
        }
        if (titleBgDim) {
            int alpha = (int)(MathHelper.clamp((float)titleBgDimAmount, (float)0.0f, (float)1.0f) * 255.0f * bgAlpha);
            context.fill(0, 0, w, h, alpha << 24);
        }
        return true;
    }

    public static void toggleFlight(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (!FengBoxClient.isMultiplayer(client)) {
            boolean canFly;
            PlayerAbilities abilities = player.getAbilities();
            abilities.allowFlying = canFly = !abilities.allowFlying;
            abilities.flying = canFly;
            IntegratedServer server = client.getServer();
            if (server != null) {
                server.execute(() -> FengBoxClient.lambda$toggleFlight$6((MinecraftServer)server, player, canFly));
            }
        } else {
            flightEnabled = !flightEnabled;
        }
        boolean flightOn = FengBoxClient.isMultiplayer(client) ? flightEnabled : player.getAbilities().allowFlying;
        player.sendMessage((Text)Text.translatable((String)("msg.feng_box.flight." + (flightOn ? "on" : "off"))), true);
    }



    private boolean isBoxEspTarget(Entity entity) {
        switch (boxEspTargets) {
            case 1: {
                return entity instanceof Monster;
            }
            case 2: {
                return entity instanceof AnimalEntity;
            }
            case 3: {
                return entity instanceof PlayerEntity || entity instanceof Monster || entity instanceof AnimalEntity;
            }
        }
        return entity instanceof PlayerEntity;
    }


    public static int getContainerColor(BlockEntity be) {
        if (be instanceof ChestBlockEntity) {
            Block block = be.getCachedState().getBlock();
            if (block == Blocks.TRAPPED_CHEST) {
                return -43776;
            }
            return -22016;
        }
        if (be instanceof EnderChestBlockEntity) {
            return -5635841;
        }
        if (be instanceof BarrelBlockEntity) {
            return -7638187;
        }
        if (be instanceof ShulkerBoxBlockEntity) {
            DyeColor dye = ((ShulkerBoxBlockEntity)be).getColor();
            if (dye != null) {
                return 0xFF000000 | dye.getEntityColor();
            }
            return -3355444;
        }
        return 0;
    }

    /**
     * Render colored wireframe boxes around nearby storage containers (chests, ender chests,
     * barrels, shulker boxes). Called from the WorldRenderEvents.END_MAIN callback each frame.
     */
    public static void drawStorageEsp(MatrixStack matrices, float tickDelta) {
        if (!storageEspEnabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        Vec3d camPos = mc.player.getCameraPosVec(tickDelta);
        double rangeSq = (double) storageEspRange * storageEspRange;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        // Collect visible storage block-entities within range
        List<BlockPos> targets = new ArrayList<>();
        int r = (int) Math.ceil(storageEspRange);
        BlockPos playerPos = mc.player.getBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    if (pos.getSquaredDistance(playerPos) > rangeSq) continue;
                    BlockEntity be = mc.world.getBlockEntity(pos);
                    if (be instanceof ChestBlockEntity || be instanceof EnderChestBlockEntity
                            || be instanceof BarrelBlockEntity || be instanceof ShulkerBoxBlockEntity) {
                        targets.add(pos);
                    }
                }
            }
        }
        if (targets.isEmpty()) return;

        // Draw translucent filled boxes
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (BlockPos pos : targets) {
            BlockEntity be = mc.world.getBlockEntity(pos);
            int color = getContainerColor(be);
            if (color == 0) continue;
            float a = 0.15f;
            float rCol = ((color >> 16) & 0xFF) / 255.0f;
            float gCol = ((color >> 8) & 0xFF) / 255.0f;
            float bCol = (color & 0xFF) / 255.0f;
            float x0 = pos.getX() - (float) camPos.x;
            float y0 = pos.getY() - (float) camPos.y;
            float z0 = pos.getZ() - (float) camPos.z;
            float x1 = x0 + 1.0f, y1 = y0 + 1.0f, z1 = z0 + 1.0f;
            // Front
            buffer.vertex(matrix, x0, y0, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y0, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y1, z1).color(rCol, gCol, bCol, a);
            // Back
            buffer.vertex(matrix, x1, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y1, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z0).color(rCol, gCol, bCol, a);
            // Right
            buffer.vertex(matrix, x1, y0, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z1).color(rCol, gCol, bCol, a);
            // Left
            buffer.vertex(matrix, x0, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y0, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y1, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y1, z0).color(rCol, gCol, bCol, a);
            // Top
            buffer.vertex(matrix, x0, y1, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y1, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y1, z0).color(rCol, gCol, bCol, a);
            // Bottom
            buffer.vertex(matrix, x0, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y0, z0).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x1, y0, z1).color(rCol, gCol, bCol, a);
            buffer.vertex(matrix, x0, y0, z1).color(rCol, gCol, bCol, a);
        }
        BuiltBuffer builtFill = buffer.endNullable();
        if (builtFill != null) RenderCompat.drawPositionColorEsp(builtFill);

        // Draw wireframe edges
        buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
        for (BlockPos pos : targets) {
            BlockEntity be = mc.world.getBlockEntity(pos);
            int color = getContainerColor(be);
            if (color == 0) continue;
            float a = 0.9f;
            float rCol = ((color >> 16) & 0xFF) / 255.0f;
            float gCol = ((color >> 8) & 0xFF) / 255.0f;
            float bCol = (color & 0xFF) / 255.0f;
            float x0 = pos.getX() - (float) camPos.x;
            float y0 = pos.getY() - (float) camPos.y;
            float z0 = pos.getZ() - (float) camPos.z;
            float x1 = x0 + 1.0f, y1 = y0 + 1.0f, z1 = z0 + 1.0f;
            // 12 edges, each 2 vertices
            float lw = 2.0f;
            // bottom edges
            addEdge(buffer, matrix, x0, y0, z0, x1, y0, z0, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y0, z0, x1, y0, z1, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y0, z1, x0, y0, z1, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x0, y0, z1, x0, y0, z0, rCol, gCol, bCol, a, lw);
            // top edges
            addEdge(buffer, matrix, x0, y1, z0, x1, y1, z0, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y1, z0, x1, y1, z1, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y1, z1, x0, y1, z1, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x0, y1, z1, x0, y1, z0, rCol, gCol, bCol, a, lw);
            // vertical edges
            addEdge(buffer, matrix, x0, y0, z0, x0, y1, z0, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y0, z0, x1, y1, z0, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x1, y0, z1, x1, y1, z1, rCol, gCol, bCol, a, lw);
            addEdge(buffer, matrix, x0, y0, z1, x0, y1, z1, rCol, gCol, bCol, a, lw);
        }
        BuiltBuffer builtLines = buffer.endNullable();
        if (builtLines != null) RenderCompat.drawPositionColorEsp(builtLines);
    }

    // === Thin wrappers for ESP renderers (called from the WorldRenderEvents.END_MAIN callback) ===
    public static void drawBoxEsp(MatrixStack matrices, float tickDelta) {
        EspRenderer.drawBoxEsp(matrices, tickDelta);
    }
    public static void drawHitboxEsp(MatrixStack matrices, float tickDelta) {
        EspRenderer.drawHitboxEsp(matrices, tickDelta);
    }
    public static void drawTracers(MatrixStack matrices, float tickDelta) {
        EspRenderer.drawTracers(matrices, tickDelta);
    }
    public static void drawItemEsp(MatrixStack matrices, float tickDelta) {
        EspRenderer.drawItemEsp(matrices, tickDelta);
    }
    public static void drawEntityOutlines(MatrixStack matrices, float tickDelta) {
        EspRenderer.drawEntityOutlines(matrices, tickDelta);
    }

    private static void addEdge(BufferBuilder b, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float bl, float a, float lw) {
        float dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float nx = len > 0.001f ? dx / len : 1.0f;
        float ny = len > 0.001f ? dy / len : 0.0f;
        float nz = len > 0.001f ? dz / len : 0.0f;
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(nx, ny, nz).lineWidth(lw);
    }

    public static Identifier captureTextureId() {
        long stamp;
        TextureManager mgr = MinecraftClient.getInstance().getTextureManager();
        MinecraftClient client = MinecraftClient.getInstance();
        File custom = new File(client.runDirectory, "config/feng_box/capture.png");
        long l = stamp = custom.isFile() ? custom.lastModified() : 0L;
        if (captureTexId != null && captureTexMgr == mgr && captureTexStamp == stamp) {
            return captureTexId;
        }
        captureTexId = null;
        captureTexMgr = mgr;
        captureTexStamp = stamp;
        if (captureTexBacked != null) {
            captureTexBacked.close();
            captureTexBacked = null;
        }
        try {
            Optional opt;
            NativeImage img = null;
            if (custom.isFile()) {
                try (FileInputStream in = new FileInputStream(custom);){
                    img = NativeImage.read((InputStream)in);
                }
            }
            if (img == null && (opt = client.getResourceManager().getResource(Identifier.of("feng_box", "textures/capture.png"))).isPresent()) {
                try (InputStream in = ((Resource)opt.get()).getInputStream();){
                    img = NativeImage.read((InputStream)in);
                }
            }
            if (img == null) {
                img = FengBoxClient.genCaptureTexture();
            }
            captureTexBacked = new NativeImageBackedTexture(() -> "capture_frame", img);
            mgr.registerTexture(Identifier.of("feng_box", "capture_frame"), captureTexBacked); captureTexId = Identifier.of("feng_box", "capture_frame");
        }
        catch (Throwable ignored) {
            captureTexId = null;
        }
        return captureTexId;
    }

    public static NativeImage genCaptureTexture() {
        NativeImage img = new NativeImage(NativeImage.Format.RGBA, 256, 256, false);
        FengBoxClient.fillCaptureRect(img, 8, 8, 240, 6, 24);
        FengBoxClient.fillCaptureRect(img, 8, 242, 240, 6, 24);
        FengBoxClient.fillCaptureRect(img, 8, 8, 6, 240, 24);
        FengBoxClient.fillCaptureRect(img, 242, 8, 6, 240, 24);
        FengBoxClient.fillCaptureRect(img, 88, 19, 80, 5, 150);
        FengBoxClient.fillCaptureRect(img, 88, 232, 80, 5, 150);
        FengBoxClient.fillCaptureRect(img, 19, 88, 5, 80, 150);
        FengBoxClient.fillCaptureRect(img, 232, 88, 5, 80, 150);
        FengBoxClient.fillCaptureRect(img, 126, 64, 4, 12, 70);
        FengBoxClient.fillCaptureRect(img, 126, 180, 4, 12, 70);
        FengBoxClient.fillCaptureRect(img, 64, 126, 12, 4, 70);
        FengBoxClient.fillCaptureRect(img, 180, 126, 12, 4, 70);
        FengBoxClient.fillCaptureRect(img, 14, 14, 11, 74, 255);
        FengBoxClient.fillCaptureRect(img, 14, 14, 74, 11, 255);
        FengBoxClient.fillCaptureRect(img, 231, 14, 11, 74, 255);
        FengBoxClient.fillCaptureRect(img, 168, 14, 74, 11, 255);
        FengBoxClient.fillCaptureRect(img, 14, 168, 11, 74, 255);
        FengBoxClient.fillCaptureRect(img, 14, 231, 74, 11, 255);
        FengBoxClient.fillCaptureRect(img, 231, 168, 11, 74, 255);
        FengBoxClient.fillCaptureRect(img, 168, 231, 74, 11, 255);
        return img;
    }

    public static void fillCaptureRect(NativeImage img, int x, int y, int w, int h, int alpha) {
        int abgr = alpha << 24 | 0xFFFFFF;
        for (int j = y; j < y + h; ++j) {
            for (int i = x; i < x + w; ++i) {
                img.setColor(i, j, abgr);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */


    public static void combatPreviewBox(MatrixStack matrices, Matrix4f matrix, Vec3d camPos, BlockPos pos, float r, float g, float b, float pulse) {
        float x0 = (float)((double)pos.getX() - camPos.x);
        float y0 = (float)((double)pos.getY() - camPos.y);
        float z0 = (float)((double)pos.getZ() - camPos.z);
        Box box = new Box(x0, y0, z0, x0 + 1.0f, y0 + 1.0f, z0 + 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            RenderUtils.boxFill(buffer, matrix, x0, y0, z0, x0 + 1.0f, y0 + 1.0f, z0 + 1.0f, r, g, b, 0.12f + 0.1f * pulse);
            BuiltBuffer safeBuilt12 = buffer.endNullable();
            if (safeBuilt12 != null) {
                RenderCompat.drawPositionColorEsp(safeBuilt12);
            }
        buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
            RenderUtils.boxEdges(buffer, matrix, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ, r, g, b, 0.55f + 0.35f * pulse);
            BuiltBuffer safeBuilt13 = buffer.endNullable();
            if (safeBuilt13 != null) {
                RenderCompat.drawPositionColorEsp(safeBuilt13);
            }
    }

    public static void lambda$toggleFlight$6(MinecraftServer server, ClientPlayerEntity player, boolean canFly) {
        ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(player.getUuid());
        if (serverPlayer != null) {
            serverPlayer.getAbilities().allowFlying = canFly;
            serverPlayer.getAbilities().flying = canFly;
            NetworkHelper.sendAbilitiesS2C(serverPlayer);
        }
    }

    public static void lambda$exitFreecam$4(MinecraftServer server, ClientPlayerEntity player, Vec3d pos) {
        ServerPlayerEntity sp = server.getPlayerManager().getPlayer(player.getUuid());
        if (sp != null) {
            sp.noClip = false;
            sp.getAbilities().allowFlying = freecamPrevFlight;
            sp.getAbilities().flying = false;
            sp.setPos(pos.x, pos.y, pos.z);
            NetworkHelper.sendAbilitiesS2C(sp);
        }
    }

    public static void lambda$enterFreecam$3(MinecraftServer server, ClientPlayerEntity player, Vec3d pos) {
        ServerPlayerEntity sp = server.getPlayerManager().getPlayer(player.getUuid());
        if (sp != null) {
            sp.noClip = true;
            sp.setPos(pos.x, pos.y, pos.z);
        }
    }

    static {
        hudEnabled = true;
        nightVisionEnabled = false;
        noHurtShakeEnabled = false;
        playerInfoEnabled = false;
        autoCrystalEnabled = false;
        storageEspEnabled = false;
        playerTrackerEnabled = false;
        hitboxEspEnabled = false;
        autoTotemEnabled = false;
        autoBridgeEnabled = false;
        spinEnabled = false;
        vanillaUiEnabled = false;
        spinYaw = 0.0f;
        themeMode = 0;
        // themeMode 0=me.perish dark, 1=me.perish white, 2=bloodred
        T_BG = new int[]{0xEC1E1E1E, 0xE8F5F8FF, 0xFF1A0A0A};
        T_BORDER = new int[]{0x1AFFFFFF, 0x55DCDCDC, 0xFFFF3333};
        T_ACCENT = new int[]{0xFF0078D4, 0xFF0078D4, 0xFFFF0000};
        T_FROST = new int[]{0x66111111, 0xAAE8E8E8, 0xBB330000};
        T_ITEM_BG = new int[]{0xEC1E1E1E, 0xE0E8E8E8, 0xFF1A0A0A};
        T_ITEM_HOV = new int[]{0xC8323232, 0xD0D0D0D0, 0xFF330000};
        T_TEXT_MAIN = new int[]{-1, 0xFF000000, -1};
        T_TEXT_DIM = new int[]{0xB3DCDCDC, 0xB3888888, 0xFFFF6666};
        T_SW_ON = new int[]{0xFF0078D4, 0xFF0078D4, 0xFFFF0000};
        T_SW_OFF = new int[]{0x44FFFFFF, 0x44888888, 0x44FF4444};
        killAuraEnabled = false;
        speedEnabled = false;
        speedFastMode = false;
        stepEnabled = false;
        sprintEnabled = false;
        autoWalkEnabled = false;
        fullbrightEnabled = false;
        tracersEnabled = false;
        nameTagsEnabled = false;
        itemEspEnabled = false;
        chestStealerEnabled = false;
        chestStealerCooldown = 0;
        chestStealerDelay = 3;
        chestStealerSmartFilter = true;
        chestStealerTrashCustom = "";
        hotbarOrganizerEnabled = false;
        lastOrganizeTime = 0L;
        replenishEnabled = false;
        replenishThreshold = 5;
        replenishDelay = 40;
        replenishInsideOf = 0;
        lastReplenishTime = 0L;
        replenishTracked = new Item[10];
        armorSaveEnabled = false;
        armorSaveThreshold = 30;
        armorSaveDelay = 40;
        armorSaveCooldown = 0;
        autoToolEnabled = false;
        autoRespawnEnabled = false;
        antiAfkEnabled = false;
        autoEatEnabled = false;
        multiJumpEnabled = false;
        multiJumpMax = 3;
        multiJumpLeft = 0;
        autoEatHealHealth = 10.0f;
        autoEatHungerThreshold = 10;
        autoEatCombatPause = true;
        autoEatCombatRange = 8.0f;
        themeTransition = 0.0f;
        CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("feng_box.json");
        hudX = -1;
        hudY = 4;
        playerInfoSlideOffset = -300.0f;
        piSlideVel = 0.0f;
        featureAlphas = new float[50];
        slideOffset = 400.0f;
        slideVel = 0.0f;
        FEATURE_TEXTS = new Text[50];
        FEATURE_WIDTHS = new int[50];
        featureTextsInit = false;
        lastHpInt = -1;
        cachedHpText = Text.literal((String)"0");
        hitFlash = 0.0f;
        barDisplayHealth = -1.0f;
        barDisplayVel = 0.0f;
        lastMainHandItem = 0;
        cachedMainHandName = Text.literal((String)"");
        lastOffHandItem = 0;
        cachedOffHandName = Text.literal((String)"");
        flightEnabled = false;
        autoAimSpeed = 30.0f;
        autoCrystalRange = 6.0f;
        flightSpeed = 0.7f;
        fastMoveSpeed = 0.42f;
        flightStealth = false;
        fastMoveStealth = false;
        speedStealth = false;
        storageEspRange = 32.0f;
        playerTrackerRange = 64.0f;
        autoAimRotMode = 1;
        autoCrystalRotMode = 0;
        autoBridgeRotMode = 0;
        invManagerEnabled = false;
        invManagerDelay = 80;
        invManagerCleanDelay = 600;
        invManagerThrowDelay = 150;
        invManagerOffhandMode = 2;
        invManagerCleanMode = 1;
        invManagerSortMode = 2;
        invManagerAutoArmor = true;
        invManagerAutoWeapon = true;
        invManagerAutoTools = true;
        invManagerAutoBow = true;
        invManagerAutoOffhand = true;
        invManagerSilentSprint = true;
        invManagerFunctionalBlocks = true;
        invManagerSwordSlot = 0;
        invManagerPickaxeSlot = 1;
        invManagerAxeSlot = 2;
        invManagerShovelSlot = 3;
        invManagerBlockSlot = 4;
        invManagerGappleSlot = 5;
        invManagerBowSlot = 6;
        invManagerRodSlot = 7;
        invManagerShieldSlot = 8;
        invManagerMaxBlocks = 6;
        invManagerMaxFood = 3;
        invManagerMaxProjectiles = 2;
        invManagerMaxPearls = 1;
        invManagerMaxBuckets = 1;
        invManagerMaxSlimeBalls = 1;
        invManagerMaxCrystals = 1;
        invManagerMaxArrows = 256;
        invManagerLastAction = 0L;
        invManagerLastClean = 0L;
        invManagerLastThrow = 0L;
        invManagerSprintWasSprinting = false;
        invManagerSprintKeyHeld = false;
        customOutlineEnabled = false;
        customOutlineAnimation = true;
        customOutlineAnimationSpeed = 6.0f;
        customOutlineMoveSpeed = 8.0f;
        customOutlineRed = 1.0f;
        customOutlineGreen = 0.25f;
        customOutlineBlue = 0.25f;
        customOutlineAlpha = 0.9f;
        customOutlineFill = true;
        customOutlineFillRed = 1.0f;
        customOutlineFillGreen = 0.25f;
        customOutlineFillBlue = 0.25f;
        customOutlineFillAlpha = 0.22f;
        customOutlineWidth = 2.0f;
        customOutlineThroughWalls = true;
        outlineVisibility = 0.0f;
        outlineRenderX = 0.0f;
        outlineRenderY = 0.0f;
        outlineRenderZ = 0.0f;
        outlineTarget = null;
        lastOutlineTime = 0L;
        killAuraRange = 4.5f;
        killAuraCps = 12.0f;
        killAuraAuto = true;
        killAuraWeaponSwitch = true;
        killAuraShieldBreak = true;
        killAuraOnlyWeapons = false;
        killAuraSwitchDelay = 4;
        killAuraSwitchCooldown = 0;
        killAuraPrevSlot = -1;
        killAuraWallRange = 3.0f;
        killAuraCheckLineOfSight = true;
        killAuraScanExtra = 2.0f;
        killAuraIgnoreOpenInv = false;
        killAuraClickJitter = 0.15f;
        killAuraEspMode = 2;
        captureFrameEnabled = true;
        captureAnimScale = 0.0f;
        captureAnimTarget = null;
        killAuraAttacks = 0.0f;
        clickerEnabled = false;
        clickerLeft = true;
        clickerRight = false;
        clickerMinCps = 8;
        clickerMaxCps = 12;
        clickerRandomMode = 2;
        clickerHoldOnly = false;
        clickerOnlyWeapons = false;
        clickerInGui = false;
        clickerAccumMs = 0.0f;
        clickerLastDelayMs = 0L;
        clickerBurstCount = 0;
        clickerBurstLength = 0;
        clickerBurstActive = false;
        clickerFastPhase = true;
        clickerFastPhaseClicks = 0;
        clickerSlowPhaseClicks = 0;
        clickerSlowPhaseLength = 0;
        clickerFastPhaseLength = 0;
        clickerRandom = new SplittableRandom();
        clickerTiming = new AutoClickerTimingState();
        currentKillAuraScanRange = 0.0f;
        reachEnabled = false;
        reachRange = 5.0f;
        reachEntityRange = 4.5f;
        speedAmount = 0.28f;
        autoEatCooldown = 0;
        autoRodEnabled = false;
        autoRodRange = 20.0f;
        autoRodTriggerRange = 4.5f;
        autoRodSpeed = 35.0f;
        autoRodCooldown = 0;
        autoRodTarget = null;
        autoWebEnabled = false;
        autoWebRange = 4.0f;
        autoWebDelay = 4;
        autoWebRender = true;
        autoWebGroundWeb = false;
        autoWebLava = true;
        autoWebLavaWaitWater = true;
        autoWebDebug = false;
        autoWebSpeed = 30.0f;
        webPhase = 0;
        webWaitTicks = 0;
        webRetrieveTicks = 0;
        webRetrieveAttempts = 0;
        webSourceWaitCount = 0;
        webLavaReapplyCount = 0;
        webLavaPlaced = false;
        webRetriedLavaPlace = false;
        webPlaceSent = false;
        webBreakTicks = 0;
        webBreakStarted = false;
        webBreakCooldown = 0;
        webTarget = null;
        webPlacePos = null;
        webSupportPos = null;
        webFace = Direction.UP;
        webAimPoint = null;
        webPlaceType = 0;
        webFootPos = null;
        webLavaPos = null;
        webSecondWebPending = false;
        webGroundBreakTarget = null;
        webLastSupportPos = null;
        webLastFace = null;
        webLastAimPoint = null;
        bowAimEnabled = false;
        bowAimAutoShoot = true;
        bowAimRange = 50.0f;
        bowAimAngleRange = 30.0f;
        bowAimAngleThreshold = 6.0f;
        bowAimSpeed = 30.0f;
        bowAimCooldown = 0;
        bowAimTarget = null;
        fastUseEnabled = false;
        fastUseInterval = 3;
        fastUseTimer = 0;
        fastBreakEnabled = false;
        fastBreakInterval = 2;
        fastBreakTimer = 0;
        autoBlockEnabled = false;
        autoBlockOnlyTarget = true;
        autoBlockDropTicks = 6;
        projectileDodgeEnabled = false;
        projectileDodgeRange = 12.0f;
        projectileDodgeDanger = 2.0f;
        projectileDodgeHoldTicks = 4;
        projectileDodgeTimer = 0;
        projectileDodgeDir = 0;
        dodgeThreatEntity = null;
        dodgeThreatUntil = 0L;
        totemGhostEnabled = false;
        totemGhostDuration = 60;
        prevHadTotem = false;
        totemGhostTimer = 0;
        itemPhysicsEnabled = false;
        itemPhysicsSpeed = 1.0f;
        rotHumanize = true;
        elytraFireworkCooldownMs = 750;
        elytraFireworkSyncAura = false;
        elytraLastFireworkTime = 0L;
        freecamFrozenPos = null;
        freecamPrevFlight = false;
        freecamPrevFlying = false;
        cameraMarker = null;
        freecamSyncTimer = 0;
        bridgeYaw = 0.0f;
        bridgePlaceTicks = 0;
        bridgeUserPitch = 0.0f;
        bridgePreviewPos = null;
        autoBridgeTower = false;
        autoBridgeDoNotUseBelow = 0;
        killAuraTarget = null;
        lastKillAuraAttackTime = 0L;
        lastAuraAttackTick = -1;
        prevGamma = 1.0;
        lastAfkTime = 0L;
        isAutoEating = false;
        titleBgEnabled = true;
        settingBgEnabled = true;
        mpBgEnabled = true;
        gameBgEnabled = true;
        titleBgDim = true;
        titleBgDimAmount = 0.35f;
        loadingBgEnabled = true;
        customBgEnabled = true;
        bgAlpha = 1.0f;
        crossBg = null;
        crossAlpha = 0.0f;
        lastStaticBg = null;
        MENU_BG = new BgTex(Identifier.of("feng_box", "textures/gui/title_bg.png"));
        SETTING_BG = new BgTex(Identifier.of("feng_box", "textures/gui/setting.png"));
        MP_BG = new BgTex(Identifier.of("feng_box", "textures/gui/game2.png"));
        GAME_BG = new BgTex(Identifier.of("feng_box", "textures/gui/game_bg.png"));
        LOADING_BG = new AnimatedGif(Identifier.of("feng_box", "textures/gui/loading.gif"));
        featureKeyBindings = new HashMap<String, KeyBinding>();
        guiKeyCode = 75;
        guiDown = false;
        dummyAttackWasPressed = false;
        guiMoveEnabled = false;
        guiMoveActive = false;
        entityOutlineEnabled = false;
        entityOutlineColor = -16711936;
        entityOutlineWidth = 1.0f;
        entityOutlineTargets = 0;
        entityOutlineMaxDistance = 128;
        boxEspEnabled = false;
        boxEspMaxDistance = 128;
        boxEspTargets = 3;
        boxEspColorMode = 1;
        boxEspColor = -16711936;
        boxEspFill = true;
        boxEspFillAlpha = 0.2f;
        boxEspOutline = true;
        boxEspHealthBar = true;
        boxEspExpand = 0.05f;
        boxEspScreens = new ArrayList<float[]>();
        capeEnabled = true;
        elytraSkinEnabled = true;
        autoAnchorEnabled = false;
        autoAnchorRange = 5.0f;
        autoAnchorMinHealth = 8.0f;
        autoAnchorSuicide = false;
        autoAnchorRotMode = 0;
        autoAnchorPlaceDelay = 4;
        autoAnchorBreakDelay = 3;
        autoCrystalWallsRange = 6.0f;
        autoCrystalSmartDelay = true;
        autoCrystalIgnoreNakeds = false;
        autoCrystalSupport = true;
        autoCrystalFastBreak = true;
        ROD_RANGE_TABLE = FengBoxClient.buildRodRangeTable();
        DEFAULT_TRASH = Set.of("dirt", "cobblestone", "gravel", "sand", "red_sand", "netherrack", "stone", "granite", "diorite", "andesite", "tuff", "deepslate", "rotten_flesh", "bone", "string", "feather", "flint", "poppy", "dandelion", "dead_bush", "fern", "grass", "kelp", "seagrass", "stick");
        TRASH_IDS = new HashSet<String>(DEFAULT_TRASH);
        ARMOR_TYPES = Set.of("helmet", "chestplate", "leggings", "boots");
        TOOL_TYPES = Set.of("sword", "pickaxe", "axe", "shovel", "hoe");
        CMD_FEATURES = FengBoxClient.buildCommandFeatures();
        autoTotemCooldown = 0;
        testDummy = null;
        crystalCooldown = 0;
        crystalStep = 0;
        crystalPlacePos = null;
        crystalRetries = 0;
        anchorCooldown = 0;
        anchorStep = 0;
        anchorPos = null;
        anchorRetries = 0;
        FEATURE_KEYS = new String[]{"msg.feng_box.auto_crit.on", "msg.feng_box.flight.on", "msg.feng_box.freecam.on", "msg.feng_box.clear_effects.on", "msg.feng_box.anti_fall.on", "msg.feng_box.night_vision.on", "msg.feng_box.no_hurt_shake.on", "msg.feng_box.player_info.on", "msg.feng_box.auto_crystal.on", "msg.feng_box.auto_totem.on", "msg.feng_box.storage_esp.on", "msg.feng_box.player_tracker.on", "msg.feng_box.auto_bridge.on", "msg.feng_box.spin.on", "msg.feng_box.white_theme.on", "msg.feng_box.hitbox_esp.on", "msg.feng_box.kill_aura.on", "msg.feng_box.speed.on", "msg.feng_box.step.on", "msg.feng_box.sprint.on", "msg.feng_box.auto_walk.on", "msg.feng_box.fullbright.on", "msg.feng_box.tracers.on", "msg.feng_box.name_tags.on", "msg.feng_box.item_esp.on", "msg.feng_box.chest_stealer.on", "msg.feng_box.auto_tool.on", "msg.feng_box.auto_respawn.on", "msg.feng_box.anti_afk.on", "msg.feng_box.auto_eat.on", "msg.feng_box.hotbar_organizer.on", "msg.feng_box.custom_outline.on", "msg.feng_box.reach.on", "msg.feng_box.multi_jump.on", "msg.feng_box.auto_anchor.on", "msg.feng_box.gui_move.on", "msg.feng_box.entity_outline.on", "msg.feng_box.replenish.on", "msg.feng_box.armor_save.on", "msg.feng_box.box_esp.on", "msg.feng_box.auto_rod.on", "msg.feng_box.bow_aim.on", "msg.feng_box.fast_use.on", "msg.feng_box.fast_break.on", "msg.feng_box.auto_block.on", "msg.feng_box.projectile_dodge.on", "msg.feng_box.totem_ghost.on", "msg.feng_box.item_physics.on", "msg.feng_box.auto_web.on", "msg.feng_box.violent.on"};
        FEATURE_ORDER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 32, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49};
        FEATURE_COLORS = new int[]{-48060, -12285697, -12285697, -12264124, -12264124, -4504321, -4504321, -4504321, -48060, -12264124, -4504321, -4504321, -12285697, -12285697, -4504321, -4504321, -48060, -12285697, -12285697, -12285697, -12285697, -4504321, -4504321, -4504321, -4504321, -22016, -22016, -22016, -22016, -12264124, -22016, -4504321, -48060, -12285697, -48060, -12285697, -4504321, -22016, -12264124, -4504321, -48060, -48060, -22016, -22016, -48060, -12264124, -12264124, -4504321, -48060, -39390};
    }

    public static interface ScoreFunc {
        public float score(ItemStack var1);
    }

    public static class CmdFeature {
        final String name;
        final Runnable toggle;
        final Supplier<Boolean> isOn;

        CmdFeature(String name, Runnable toggle, Supplier<Boolean> isOn) {
            this.name = name;
            this.toggle = toggle;
            this.isOn = isOn;
        }
    }

    public static class BgTex {
        final Identifier id;
        int texW;
        int texH;
        boolean loaded;

        BgTex(Identifier id) {
            this.id = id;
        }

        void ensure() {
            block9: {
                if (this.loaded) {
                    return;
                }
                this.loaded = true;
                try {
                    Optional res = MinecraftClient.getInstance().getResourceManager().getResource(this.id);
                    if (!res.isPresent()) break block9;
                    try (InputStream in = ((Resource)res.get()).getInputStream();){
                        NativeImage img = NativeImage.read((InputStream)in);
                        this.texW = img.getWidth();
                        this.texH = img.getHeight();
                        img.close();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    public static final class AnimatedGif {
        public static final int MAX_FRAMES = 64;
        final Identifier id;
        int width;
        int height;
        Identifier[] texIds;
        int[] delaysMs;
        int current;
        long totalMs;
        long lastAdvance;
        boolean loaded;
        TextureManager tmRef;

        AnimatedGif(Identifier id) {
            this.id = id;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        void ensure() {
            MinecraftClient mc = MinecraftClient.getInstance();
            TextureManager mgr = mc.getTextureManager();
            if (this.loaded && this.tmRef == mgr) {
                return;
            }
            this.loaded = true;
            this.tmRef = mgr;
            this.texIds = null;
            try {
                Optional res = MinecraftClient.getInstance().getResourceManager().getResource(this.id);
                if (res.isEmpty()) {
                    return;
                }
                ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
                ArrayList<Integer> delays = new ArrayList<Integer>();
                try (InputStream raw = ((Resource)res.get()).getInputStream();
                     ImageInputStream iis = ImageIO.createImageInputStream(raw);){
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                    if (!readers.hasNext()) {
                        return;
                    }
                    ImageReader reader = readers.next();
                    reader.setInput(iis);
                    int count = Math.min(reader.getNumImages(true), 64);
                    if (count <= 0) {
                        reader.dispose();
                        return;
                    }
                    for (int i = 0; i < count; ++i) {
                        frames.add(reader.read(i));
                        delays.add(AnimatedGif.readDelay(reader, i));
                    }
                    reader.dispose();
                }
                this.width = ((BufferedImage)frames.get(0)).getWidth();
                this.height = ((BufferedImage)frames.get(0)).getHeight();
                int n = frames.size();
                this.texIds = new Identifier[n];
                this.delaysMs = new int[n];
                int i = 0;
                while (true) {
                    if (i >= n) {
                        this.lastAdvance = System.currentTimeMillis();
                        return;
                    }
                    BufferedImage bi = (BufferedImage)frames.get(i);
                    if (bi.getWidth() != this.width || bi.getHeight() != this.height) {
                        BufferedImage scaled = new BufferedImage(this.width, this.height, 2);
                        scaled.getGraphics().drawImage(bi, 0, 0, this.width, this.height, null);
                        bi = scaled;
                    }
                    NativeImage ni = new NativeImage(NativeImage.Format.RGBA, this.width, this.height, false);
                    for (int y = 0; y < this.height; ++y) {
                        for (int x = 0; x < this.width; ++x) {
                            int argb = bi.getRGB(x, y);
                            int abgr = argb & 0xFF00FF00 | (argb & 0xFF0000) >> 16 | (argb & 0xFF) << 16;
                            ni.setColor(x, y, abgr);
                        }
                    }
                    { String _texName = "fb_loading_bg_" + i; Identifier _texId = Identifier.of("feng_box", _texName); mgr.registerTexture(_texId, new NativeImageBackedTexture(() -> _texName, ni)); this.texIds[i] = _texId; }
                    this.delaysMs[i] = Math.max(30, (Integer)delays.get(i));
                    this.totalMs += (long)this.delaysMs[i];
                    ++i;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        public static int readDelay(ImageReader reader, int frame) {
            try {
                IIOMetadata meta = reader.getImageMetadata(frame);
                String fmt = meta.getNativeMetadataFormatName();
                Node root = meta.getAsTree(fmt);
                NodeList list = root.getChildNodes();
                for (int i = 0; i < list.getLength(); ++i) {
                    Node d;
                    Node n = list.item(i);
                    if (!"GraphicControlExtension".equals(n.getNodeName()) || (d = n.getAttributes().getNamedItem("delayTime")) == null) continue;
                    return Math.max(0, Integer.parseInt(d.getNodeValue())) * 10;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return 100;
        }

        void render(DrawContext ctx, int w, int h, float alpha) {
            if (this.texIds == null || this.texIds.length == 0) {
                return;
            }
            long now = System.currentTimeMillis();
            if (this.totalMs > 0L && now - this.lastAdvance >= this.totalMs) {
                this.lastAdvance = now;
            }
            while (now - this.lastAdvance > (long)this.delaysMs[this.current]) {
                this.lastAdvance += (long)this.delaysMs[this.current];
                this.current = (this.current + 1) % this.delaysMs.length;
            }
            RenderUtils.drawImageCover(ctx, this.texIds[this.current], this.width, this.height, w, h, alpha);
        }
    }

    // === Missing handle methods (stubs for v55) ===
    public void handlePacketFly(MinecraftClient client) {
        if (!packetFlyEnabled || client.player == null) return;
        // Packet fly implementation stub
    }

    public void handleWebAuraTick(MinecraftClient client) {
        if (!webAuraEnabled || client.player == null) return;
        // Web aura implementation stub
    }

    public void handleStrafe(MinecraftClient client) {
        if (!strafeEnabled || client.player == null) return;
        // Strafe implementation stub
    }

    public void handleAntiHunger(MinecraftClient client) {
        if (!antiHungerEnabled || client.player == null) return;
        // Anti hunger implementation stub
    }

    public void handleNoFall(MinecraftClient client) {
        if (!noFallEnabled || client.player == null) return;
        // No fall implementation stub
    }

    public void handleAutoPot(MinecraftClient client) {
        if (!autoPotEnabled || client.player == null) return;
        // Auto pot implementation stub
    }

    public void handleXinBurrow(MinecraftClient client) {
        if (!xinBurrowEnabled || client.player == null) return;
        // Xin burrow implementation stub
    }

    public void handleAntiCev(MinecraftClient client) {
        if (!antiCevEnabled || client.player == null) return;
        // Anti cev implementation stub
    }
}

