package aureum.asta.disks.ports.elysium.cheirosiphon;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.machine.gravitator.GravitatorBlockEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CheirosiphonItem extends Item implements HeatingItem {
   private static final int MAX_USE_TICKS = 100;

   public CheirosiphonItem(Settings properties) {
      super(properties);
   }

   /*public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
      if (this.isIn(group)) {
         stacks.add(new ItemStack(this));
         stacks.add((ItemStack)Util.make(new ItemStack(this), stack -> EnchantmentHelper.set(Map.of(Elysium.JET_ENCHANTMENT, 1), stack)));
         stacks.add((ItemStack)Util.make(new ItemStack(this), stack -> EnchantmentHelper.set(Map.of(Elysium.GHASTLY_ENCHANTMENT, 1), stack)));
      }
   }*/

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      if ((user.isCreative() || user.getInventory().contains(Items.FIRE_CHARGE.getDefaultStack())) && !user.isTouchingWater() && !this.isOverheated(user)) {
         ItemStack stack = user.getStackInHand(hand);
         user.setCurrentHand(hand);
         this.startHeating(user);
         Inventories.remove(user.getInventory(), s -> s.isOf(Items.FIRE_CHARGE), 1, false);
         return TypedActionResult.success(stack, world.isClient());
      } else {
         return TypedActionResult.fail(user.getStackInHand(hand));
      }
   }

   public boolean isEnchantable(ItemStack stack) {
      return stack.getCount() == 1;
   }

   public int getMaxUseTime(ItemStack stack) {
      return 999999990;
   }

   public UseAction getUseAction(ItemStack stack) {
      return UseAction.NONE;
   }

   public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
      if (remainingUseTicks % 2 == 0) {
         user.playSoundIfNotSilent(ElysiumSounds.CHEIROSIPHON_LOOP);
      }

      if (user instanceof PlayerEntity p && this.isOverheated(p)) {
         p.playSoundIfNotSilent(ElysiumSounds.CHEIROSIPHON_DEACTIVATE);
         p.clearActiveItem();
         p.damage(
            p.getActiveHand() == Hand.MAIN_HAND ? world.getDamageSources().create(ElysiumDamageSources.CHEIROSIPHON_OVERHEAT_BYPASSES) : world.getDamageSources().create(ElysiumDamageSources.CHEIROSIPHON_OVERHEAT),
            1.0F
         );
         p.playSoundIfNotSilent(SoundEvents.ENTITY_GENERIC_BURN);
         p.setOnFireFor(6);
         return;
      }

      if (remainingUseTicks % 120 == 0 && user instanceof PlayerEntity p) {
         if (!p.isCreative() && !p.getInventory().contains(Items.FIRE_CHARGE.getDefaultStack()) || user.isTouchingWater()) {
            p.playSoundIfNotSilent(ElysiumSounds.CHEIROSIPHON_DEACTIVATE);
            p.clearActiveItem();
            return;
         }

         Inventories.remove(p.getInventory(), s -> s.isOf(Items.FIRE_CHARGE), 1, false);
      }

      if (!world.isClient()) {
         CheirosiphonFlame flame = new CheirosiphonFlame(Elysium.CHEIROSIPHON_FLAME, user, world);
         float divergence = ((CheirosiphonFlameDivergenceCallback)CheirosiphonFlameDivergenceCallback.EVENT.invoker()).modifyDivergence(user, stack, 30.0F);
         float speed = ((CheirosiphonFlameSpeedCallback)CheirosiphonFlameSpeedCallback.EVENT.invoker()).modifySpeed(user, stack, 1.0F);
         flame.setVelocity(user, divergence, speed);
         ((CheirosiphonFlameSpawningCallback)CheirosiphonFlameSpawningCallback.EVENT.invoker()).acceptFlame(user, stack, flame);
         world.spawnEntity(flame);
      }
   }

   public void airBlast(World level, PlayerEntity user) {
      level.getOtherEntities(user, user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(4.0))).forEach(e -> {
         e.extinguish();
         e.damage(user.world.getDamageSources().create(ElysiumDamageSources.CHEIROSIPHON_BLAST), 1.0F);
         GravitatorBlockEntity.pushEntity(user.getRotationVec(1.0F).multiply(3.0, 1.5, 3.0), e);
      });
      user.getItemCooldownManager().set(this, 20);
      ClientboundAirblastFxPacket.sendToTracking(user);
   }

   @Override
   public int getMaxHeat() {
      return 100;
   }

   @Override
   public int getHeat(int lastTickHeat, PlayerEntity player, @Nullable Hand hand, boolean isBeingUsed) {
      if (isBeingUsed) {
         return lastTickHeat + (hand == Hand.MAIN_HAND ? 1 : 2);
      } else {
         return hand == Hand.MAIN_HAND ? lastTickHeat - (int)(player.world.getTime() % 2L) : lastTickHeat - (player.world.getTime() % 3L == 0L ? 0 : 1);
      }
   }

   public static final class ClientboundAirblastFxPacket {
      public static final Identifier PACKET_ID = Elysium.id("cheirosiphon_airblast_fx");

      public static void sendToTracking(Entity entity, Vec3d direction, Vec3d spawnPosition) {

         if(entity instanceof ServerPlayerEntity mainPlayer) {
            ServerPlayNetworking.send(mainPlayer, PACKET_ID, create(direction, spawnPosition));
         }

         for (ServerPlayerEntity player : PlayerLookup.tracking(entity))
         {
            ServerPlayNetworking.send(player, PACKET_ID, create(direction, spawnPosition));
         }

         //ServerPlayNetworking.send((Collection)Util.make(new ArrayList(PlayerLookup.tracking(entity)), c -> {if (entity instanceof ServerPlayerEntity s) {c.add(s);}}), PACKET_ID, create(direction, spawnPosition));
      }

      public static void sendToTracking(Entity entity) {
         Vec3d direction = entity.getRotationVec(1.0F).multiply(0.45);
         Vec3d spawnPosition = entity.getCameraPosVec(1.0F).add(entity.getRotationVec(1.0F));
         sendToTracking(entity, direction, spawnPosition);
      }

      public static PacketByteBuf create(Vec3d direction, Vec3d spawnPosition) {
         return new PacketByteBuf(
            PacketByteBufs.create()
               .writeDouble(direction.getX())
               .writeDouble(direction.getY())
               .writeDouble(direction.getZ())
               .writeDouble(spawnPosition.getX())
               .writeDouble(spawnPosition.getY())
               .writeDouble(spawnPosition.getZ())
         );
      }
   }

   public static final class ServerboundAirblastPacket {
      public static final Identifier PACKET_ID = Elysium.id("cheirosiphon_airblast");

      public static void init() {
         ServerPlayNetworking.registerGlobalReceiver(
            PACKET_ID,
            (server, player, handler, buf, responseSender) -> server.execute(
                  () -> {
                     if (player.getMainHandStack().isOf(Elysium.CHEIROSIPHON)
                        && !player.getItemCooldownManager().isCoolingDown(Elysium.CHEIROSIPHON)
                        && !Elysium.CHEIROSIPHON.isOverheated(player)
                        && !((CheirosiphonAirblastCallback)CheirosiphonAirblastCallback.EVENT.invoker()).handleAirblast(player, player.getMainHandStack())) {
                        Elysium.CHEIROSIPHON.airBlast(player.world, player);
                     }
                  }
               )
         );
      }
   }
}
