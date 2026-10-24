package com.fengbox;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class NetworkHelper {

    private static final java.lang.reflect.Method SEND_PACKET_METHOD;
    static {
        java.lang.reflect.Method m = null;
        try {
            m = Class.forName("net.minecraft.class_634").getMethod("method_52787", Class.forName("net.minecraft.class_2596"));
        } catch (Exception e) {
            try {
                m = Class.forName("net.minecraft.client.network.ClientPlayNetworkHandler").getMethod("sendPacket", Class.forName("net.minecraft.network.packet.Packet"));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        SEND_PACKET_METHOD = m;
    }

    private static void sendPacketReflect(Object networkHandler, Object packet) {
        if (SEND_PACKET_METHOD != null && networkHandler != null && packet != null) {
            try {
                SEND_PACKET_METHOD.invoke(networkHandler, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Helper to get network handler reflectively from a ClientPlayerEntity
    private static Object getNetworkHandler(ClientPlayerEntity player) {
        if (player == null) return null;
        try {
            Field f = ClientPlayerEntity.class.getDeclaredField("networkHandler");
            f.setAccessible(true);
            return f.get(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Freecam sync packet
    public static void sendFreecamSync(ClientPlayerEntity player, double x, double y, double z) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true, false));
    }

    // Look/rotation packets (used by multiple features)
    public static void sendLookAndOnGround(ClientPlayerEntity player, float yaw, float pitch, boolean onGround) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, false));
    }

    // Anti-fall packet
    public static void sendOnGroundOnly(ClientPlayerEntity player, boolean onGround) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new PlayerMoveC2SPacket.OnGroundOnly(onGround, false));
    }

    // Packet-critical hit mode 2
    public static void sendCritPackets(ClientPlayerEntity player, double x, double y, double z) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, true, false));
        sendPacketReflect(nh, new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false, false));
        sendPacketReflect(nh, new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, false));
    }

    // Sprint packets
    public static void sendStopSprinting(ClientPlayerEntity player) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new ClientCommandC2SPacket((Entity)player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
    }

    public static void sendStartSprinting(ClientPlayerEntity player) {
        Object nh = getNetworkHandler(player);
        sendPacketReflect(nh, new ClientCommandC2SPacket((Entity)player, ClientCommandC2SPacket.Mode.START_SPRINTING));
    }

    // Flight / abilities sync packets (server-side only)
    public static void sendAbilitiesS2C(ServerPlayerEntity serverPlayer) {
        sendPacketReflect(serverPlayer.networkHandler, new PlayerAbilitiesS2CPacket(serverPlayer.getAbilities()));
    }

    // Skin texture lookup via reflection (so FengBoxClient never directly references ClientPlayNetworkHandler type)
    public static Identifier getPlayerSkinTexture(MinecraftClient client, UUID uuid) {
        if (client.getNetworkHandler() == null) return null;
        try {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(uuid);
            if (entry == null && client.player != null) {
                entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            }
            if (entry == null) return null;
            return entry.getSkinTextures().body().texturePath();
        } catch (Exception e) {
            return null;
        }
    }

    // Dummy skin entry management (moved from FengBoxClient)
    @SuppressWarnings("unchecked")
    public static void addDummySkinEntry(MinecraftClient client, PlayerEntity dummy, com.mojang.authlib.GameProfile profile) {
        if (client.getNetworkHandler() == null || client.player == null) return;
        try {
            UUID localUuid = client.player.getUuid();
            PlayerListEntry localEntry = client.getNetworkHandler().getPlayerListEntry(localUuid);
            if (localEntry == null) return;
            Identifier skinTex = localEntry.getSkinTextures().body().texturePath();
            if (skinTex == null) return;

            Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
            PlayerListEntry dummyEntry = (PlayerListEntry) unsafe.allocateInstance(PlayerListEntry.class);

            Field profileField = PlayerListEntry.class.getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(dummyEntry, profile);

            Field skinField = PlayerListEntry.class.getDeclaredField("skinTexture");
            skinField.setAccessible(true);
            skinField.set(dummyEntry, skinTex);

            Field entriesField = Class.forName("net.minecraft.client.network.ClientPlayNetworkHandler").getDeclaredField("playerListEntries");
            entriesField.setAccessible(true);
            Map<UUID, PlayerListEntry> entries = (Map<UUID, PlayerListEntry>) entriesField.get(client.getNetworkHandler());
            entries.put(profile.id(), dummyEntry);
        } catch (Exception e) {
            // silently ignore
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeDummySkinEntry(MinecraftClient client, Entity testDummy) {
        if (client.getNetworkHandler() == null || testDummy == null) return;
        try {
            Field entriesField = Class.forName("net.minecraft.client.network.ClientPlayNetworkHandler").getDeclaredField("playerListEntries");
            entriesField.setAccessible(true);
            Map<UUID, PlayerListEntry> entries = (Map<UUID, PlayerListEntry>) entriesField.get(client.getNetworkHandler());
            entries.remove(testDummy.getUuid());
        } catch (Exception e) {
            // silently ignore
        }
    }

    // Disconnect callback registration (called once from FengBoxClient entrypoint)
    public static void registerDisconnectCallback() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (FengBoxClient.freecamEnabled) {
                FengBoxClient.freecamEnabled = false;
                if (FengBoxClient.cameraMarker != null) {
                    FengBoxClient.cameraMarker.discard();
                    FengBoxClient.cameraMarker = null;
                }
                FengBoxClient.freecamFrozenPos = null;
                if (client.player != null) {
                    client.setCameraEntity((Entity)client.player);
                    client.player.noClip = false;
                }
            }
            FengBoxClient.flightEnabled = false;
            FengBoxClient.nightVisionEnabled = false;
            FengBoxClient.autoEatCooldown = 0;
            FengBoxClient.isAutoEating = false;
            FengBoxClient.crystalStep = 0;
            FengBoxClient.crystalCooldown = 0;
            FengBoxClient.crystalPlacePos = null;
            FengBoxClient.crystalRetries = 0;
            FengBoxClient.webReset();
            if (FengBoxClient.testDummy != null) {
                FengBoxClient.testDummy.discard();
                FengBoxClient.testDummy = null;
            }
            FengBoxClient.bowAimTarget = null;
            FengBoxClient.bowAimCooldown = 0;
            if (FengBoxClient.projectileDodgeTimer > 0 && client.options != null) {
                if (FengBoxClient.projectileDodgeDir < 0) {
                    client.options.leftKey.setPressed(false);
                } else if (FengBoxClient.projectileDodgeDir > 0) {
                    client.options.rightKey.setPressed(false);
                }
                FengBoxClient.projectileDodgeTimer = 0;
                FengBoxClient.projectileDodgeDir = 0;
            }
            FengBoxClient.prevHadTotem = false;
            FengBoxClient.totemGhostTimer = 0;
            if (client.player != null) {
                client.player.setGlowing(false);
            }
        });
    }
}
