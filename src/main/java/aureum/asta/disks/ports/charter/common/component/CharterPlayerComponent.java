package aureum.asta.disks.ports.charter.common.component;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.ports.charter.common.item.GauntletItem;
import aureum.asta.disks.ports.charter.common.item.GoldweaveItemEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CharterPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
   private final PlayerEntity obj;
   public UUID ownerUUID;
   public boolean mute = false;
   public boolean divinityFlying = false;
   public boolean tantalus = false;
   private boolean epitaphBanning = false;
   private int banningTicks = 0;
   private int flyingTicks = 0;
   @Nullable
   public String newName = null;
   public EyeState eyes = EyeState.NORMAL;
   private final List<GoldweaveItemEntity> goldweaveList;
   public GauntletMode mode = GauntletMode.IDLE;

   public CharterPlayerComponent(PlayerEntity player) {
      this.obj = player;
      this.goldweaveList = new ArrayList<>();
   }

   public void setEpitaphBanning(boolean newBanning)
   {
      this.epitaphBanning = newBanning;
      this.sync();
   }

   public void readFromNbt(NbtCompound tag) {
      this.goldweaveList.clear();
      if (tag.contains("debtOwnerUUID")) {
         this.ownerUUID = tag.getUuid("debtOwnerUUID");
      }

      if (tag.contains("newName")) {
         this.newName = tag.getString("newName");
      }

      this.mode = GauntletMode.valueOf(tag.getString("gauntletMode"));
      this.mute = tag.getBoolean("mute");
      this.divinityFlying = tag.getBoolean("divinityFlying");
      this.tantalus = tag.getBoolean("tantalus");
   }

   public int getListSize() {
      return this.goldweaveList.size();
   }

   public List<GoldweaveItemEntity> getGoldweaveList() {
      return this.goldweaveList;
   }

   public void removeFromList(GoldweaveItemEntity item) {
      this.goldweaveList.remove(item);
   }

   public void writeToNbt(NbtCompound tag) {
      tag.putBoolean("mute", this.mute);
      tag.putBoolean("divinityFlying", this.divinityFlying);
      tag.putBoolean("tantalus", this.tantalus);

      if (this.ownerUUID != null) {
         tag.putUuid("debtOwnerUUID", this.ownerUUID);
      }

      if (this.newName != null) {
         tag.putString("newName", this.newName);
      }

      tag.putString("gauntletMode", this.mode.toString());
   }

   public void sync() {
      CharterComponents.PLAYER_COMPONENT.sync(this.obj);
   }

   public void serverTick() {
      if (this.eyes == EyeState.BLINDED) {
         this.obj.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0, true, false));
      }

      if (!(this.obj.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GauntletItem) && this.mode != GauntletMode.COLLECT) {
         this.mode = GauntletMode.COLLECT;
         this.sync();
      }

      if (this.obj.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GauntletItem
         && this.obj.getStackInHand(Hand.OFF_HAND).getItem() instanceof GauntletItem
         && this.obj.age % 10 == 0
         && this.mode == GauntletMode.IDLE) {
         for (ItemStack stack : this.obj.getInventory().main) {
            if (stack.getItem() instanceof SwordItem sword) {
               float baseDamage = sword.getAttackDamage();
               float multiplier = 1.2F;
               double damage = (double)(1.0F + baseDamage * multiplier);
               GoldweaveItemEntity entity = new GoldweaveItemEntity(this.obj.world);
               entity.setPos(this.obj.getPos().x, this.obj.getPos().y + (double)(this.obj.getHeight() / 2.0F), this.obj.getPos().z);
               entity.setData((float)damage, this.obj.getUuid(), this.obj.getInventory().main.indexOf(stack), stack.copy());
               entity.getDataTracker().set(GoldweaveItemEntity.STACK, stack.copy());
               this.obj.world.spawnEntity(entity);
               entity.index = this.goldweaveList.size();
               this.goldweaveList.add(entity);
               stack.decrement(1);
               break;
            }
         }
      }

      if(this.divinityFlying)
      {
         this.obj.noClip = true;
         this.flyingTicks++;
         if(this.obj.getY() > 600)
         {
            this.obj.requestTeleport(7777777, 600, 7777777);
            this.divinityFlying = false;
            this.flyingTicks = -1;
         }

         /*if(flyingTicks > 600 || this.obj.isFallFlying()) {
            this.divinityFlying = false;
            this.flyingTicks = -1;
         }*/
         this.sync();
      }
      else if(this.flyingTicks == -1)
      {
         this.obj.noClip = false;
         this.flyingTicks = 0;
         this.sync();
      }

      if(this.tantalus && this.obj.getWorld().getWorldBorder().contains(this.obj.getBlockPos()) && !this.divinityFlying) this.tantalus = false;

      if(this.epitaphBanning)
      {
         this.banningTicks++;
         if(this.banningTicks >= 120)
         {
            this.setEpitaphBanning(false);
            this.banningTicks = 0;
         }

         this.sync();
      }
   }

   public boolean getEpitaphBanning()
   {
      return this.epitaphBanning;
   }

   public boolean renderBeam()
   {
      //AureumAstaDisks.LOGGER.info("Banning Ticks: {}", this.banningTicks);
      return this.banningTicks > 15;
   }

   public void clientTick() {
      if (this.epitaphBanning) {
         this.banningTicks++;
         if (this.banningTicks >= 120) {
            this.setEpitaphBanning(false);
            this.banningTicks = 0;
         }
         else if(this.banningTicks == 15)
         {
            for(int i = 0; i < 5; i++)
            {
               ParticleBuilders.create(LodestoneParticles.STAR_PARTICLE)
                       .setLifetime(80)
                       .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                       .setColor(Charter.darkOrange, Charter.yellow)
                       .setColorEasing(Easing.EXPO_OUT)
                       .setScaleEasing(Easing.EXPO_IN)
                       .setAlphaEasing(Easing.EXPO_IN)
                       .setSpinOffset((float) this.obj.world.random.nextInt(2))
                       .setAlpha(0.5f, 0.0f)
                       .setScale(Math.min(this.obj.world.random.nextFloat() + 0.5f, 1.5f))
                       .spawn(this.obj.world, this.obj.getX(), this.obj.getY() + 2.4f, this.obj.getZ());
            }
         }
         else if(this.banningTicks > 15 && this.banningTicks < 65 && this.banningTicks % 2 == 0 /*&& this.banningTicks < 95*/)
         {
             this.spawnSmokeParticles(this.obj.world, this.obj.getPos().add(0.0f, 2.4f, 0.0f), 1.5f, 0.05f, 10);
         }

         this.sync();
      }
   }

   public void spawnSmokeParticles(World world, Vec3d origin, double radius, double speed, int count) {
      double goldenAngle = Math.PI * (3 - Math.sqrt(5));

      for (int i = 0; i < count; i++) {
         double y = 1 - (i / (double)(count - 1)) * 2;
         double radiusXZ = Math.sqrt(1 - y * y);
         double theta = goldenAngle * i;

         double x = Math.cos(theta) * radiusXZ;
         double z = Math.sin(theta) * radiusXZ;

         Vec3d direction = new Vec3d(x, y, z).normalize();
         Vec3d spawnPos = origin.add(direction.multiply(radius)).add((world.random.nextFloat() - 0.5f)*0.4f, (world.random.nextFloat() - 0.5f)*0.4f, (world.random.nextFloat() - 0.5f)*0.4f);
         Vec3d motion = direction.multiply(-speed);

         ParticleBuilders.create(CharterParticles.SQUARE)
                 .setLifetime(30)
                 .setScale(0.1f)
                 .setMotion(motion)
                 .setSpin(world.getRandom().nextBoolean() ? 0.3F : -0.3F)
                 .setColor(Charter.yellow)
                 .setAlpha(0.5f, 0.4f)
                 .spawn(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
      }
   }
}
