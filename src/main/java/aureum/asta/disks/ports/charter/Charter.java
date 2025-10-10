package aureum.asta.disks.ports.charter;

import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.ports.charter.common.block.CharterStoneBlock;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.component.DiamondOfProtection;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import aureum.asta.disks.ports.charter.common.entity.EpitaphShockwaveEntity;
import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.ports.charter.common.recipe.CharterRecipes;
import aureum.asta.disks.ports.charter.common.util.GauntletPacket;
import java.awt.Color;
import java.util.ArrayList;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.After;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Charter implements ModInitializer {
   public static final String MODID = "charter";
   public static final Color yellow = new Color(248, 185, 3);
   public static final Color darkerYellow = new Color(255, 160, 20);
   public static final Color darkOrange = new Color(215, 80, 40);

   public static final Vec3f fnuuyF = new Vec3f(0.97f, 0.73f, 0.01f);
   public static final Vec3f darkerFnuuyF = new Vec3f(1.0f, 0.63f, 0.08f);
   public static final Vec3f darkestFnuuyF = new Vec3f(0.84f, 0.31f, 0.16f);

   public static final Identifier EPITAPH_HIT_PACKET = new Identifier("charter", "epitaph_hit");

   public static final ArrayList<UUID> bannedUuids = new ArrayList<>();

   public void onInitialize() {
      CharterBlocks.init();
      CharterItems.init();
      CharterEntities.init();
      CharterParticles.init();
      CharterRecipes.init();

      bannedUuids.add(UUID.fromString("e0f927bf-91fe-3015-97d8-41698d0cd92d"));
      bannedUuids.add(UUID.fromString("fb1ceaa4-e849-4169-93c2-e917f2d4b1ce"));

      ServerTickEvents.START_WORLD_TICK.register(world -> {
         ((CharterWorldComponent) world.getComponent(CharterComponents.CHARTER)).serverTick();
      });

      ServerPlayNetworking.registerGlobalReceiver(EPITAPH_HIT_PACKET, Charter::epitaphHit);

      ServerPlayNetworking.registerGlobalReceiver(GauntletPacket.ID, GauntletPacket::handle);
      PlayerBlockBreakEvents.AFTER.register((After)(world, player, pos, state, blockEntity) -> {
         if (!world.isClient) {
            for (DiamondOfProtection dia : ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER)).diamonds) {
               boolean bl = dia.isPosInside(pos) && !dia.isOwner(player);
               if (bl && !(world.getBlockState(pos).getBlock() instanceof CharterStoneBlock)) {
                  ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER)).addBlockChange(new QueuedBlockChange(200, pos, state));
                  break;
               }
            }
         }
      });
   }

   private static void epitaphHit(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
      int entityId = buf.readInt();

      server.execute(() -> {
         Entity target = player.getWorld().getEntityById(entityId);
         if (target instanceof EpitaphChainsEntity chains) {
            player.getComponent(CharterComponents.PLAYER_COMPONENT).setEpitaphBanning(true);

            EpitaphShockwaveEntity shockwave = new EpitaphShockwaveEntity(player.getWorld());
            shockwave.requestTeleport(chains.getX(), chains.getY(), chains.getZ());
            shockwave.setBannedPlayer((PlayerEntity) chains.getFirstPassenger());
            player.getWorld().spawnEntity(shockwave);

            System.out.println(player.getName().getString() + " hit " + chains.getName().getString());
         }
      });
   }

   public static Identifier id(String name) {
      return new Identifier("charter", name);
   }
}
