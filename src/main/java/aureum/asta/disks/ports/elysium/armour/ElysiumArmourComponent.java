package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.particles.ArcParticleOption;
import aureum.asta.disks.util.EnchantingClientUtil;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ElysiumArmourComponent implements Component, AutoSyncedComponent, ClientTickingComponent {
   private static final int MAX_CHARGE = 10;
   private final LivingEntity entity;
   private int charge;
   public static final ComponentKey<ElysiumArmourComponent> KEY = ComponentRegistry.getOrCreate(Elysium.id("elysium_armour"), ElysiumArmourComponent.class);

   public ElysiumArmourComponent(LivingEntity entity) {
      this.entity = entity;
   }

   public void addCharge(float damageAmount) {
      if (this.entity.getRandom().nextFloat() * 5.0F < damageAmount) {
         this.charge++;
         KEY.sync(this.entity);
      }
   }

   public int getCharge() {
      return this.charge;
   }

   public int getMaxCharge() {
      return 10;
   }

   public void setChargeNoSync(int charge) {
      this.charge = charge;
   }

   public void decrementCharge() {
      this.charge--;
      KEY.sync(this.entity);
   }

   public void dischargeHurt(Entity entity) {
      if (!((ElysiumArmourHurtDischargeCallback)ElysiumArmourHurtDischargeCallback.EVENT.invoker()).handleHurtDischarge(this, this.entity, entity)) {
         this.dischargeHurtDefault(entity);
      }

      KEY.sync(this.entity);
   }

   public void dischargeHurtDefault(Entity entity) {
      float damage = (float)this.charge * 0.75F;
      float nonPiercingDamage = damage * 0.65F;
      float piercingDamage = damage - nonPiercingDamage;
      entity.damage(this.entity.world.getDamageSources().create(ElysiumDamageSources.ELYSIUM_ARMOUR), nonPiercingDamage);
      entity.damage(this.entity.world.getDamageSources().create(ElysiumDamageSources.ELYSIUM_ARMOUR_BYPASS), piercingDamage);
      this.sendZap(entity.getPos().add(0.0, (double)(entity.getHeight() / 2.0F), 0.0), this.entity.world);
      this.charge = 0;
   }

   public void dischargeVuln(LivingEntity entity) {
      if (!((ElysiumArmourVulnDischargeCallback)ElysiumArmourVulnDischargeCallback.EVENT.invoker()).handleVulnDischarge(this, this.entity, entity)) {
         this.dischargeVulnDefault(entity);
      }

      KEY.sync(this.entity);
   }

   public void dischargeVulnDefault(LivingEntity entity) {
      int length = Math.min(this.charge, 5) * 20;
      int amplifier = this.vulnerabilityLevel() - 1;
      entity.addStatusEffect(new StatusEffectInstance(ElysiumArmour.ELYSIUM_VULNERABILITY, length, amplifier));
      this.sendZap(entity.getPos().add(0.0, (double)(entity.getHeight() / 2.0F), 0.0), this.entity.world);
      this.charge = 0;
   }

   public void dischargeRandomly() {
      Vec3d target = this.entity
         .getPos()
         .add(this.entity.getRandom().nextDouble() * 5.0, this.entity.getRandom().nextDouble() * 5.0, this.entity.getRandom().nextDouble() * 5.0);
      this.entity
         .world
         .getOtherEntities(this.entity, new Box(target.subtract(-0.5, -0.5, -0.5), target.add(0.5, 0.5, 0.5)))
         .stream()
         .findAny()
         .ifPresentOrElse(this::dischargeHurt, () -> {
            if (!((ElysiumArmourHurtDischargeCallback)ElysiumArmourHurtDischargeCallback.EVENT.invoker()).handleHurtDischarge(this, this.entity, null)) {
               this.sendZap(target, this.entity.world);
            }
         });
      this.charge = 0;
      KEY.sync(this.entity);
   }

   public void sendZap(Vec3d target, World level) {
      if (level instanceof ServerWorld serverLevel) {
         ParticleS2CPacket clientboundLevelParticlesPacket = new ParticleS2CPacket(
            new ArcParticleOption(target.x, target.y, target.z),
            false,
            this.entity.getX(),
            this.entity.getRandomBodyY(),
            this.entity.getZ(),
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            1
         );

         for (ServerPlayerEntity serverPlayer : serverLevel.getPlayers()) {
            if (serverPlayer != this.entity) {
               serverLevel.sendToPlayerIfNearby(
                  serverPlayer, false, this.entity.getX(), this.entity.getRandomBodyY(), this.entity.getZ(), clientboundLevelParticlesPacket
               );
            }
         }

         level.playSound(null, this.entity.getBlockPos(), ElysiumSounds.ELECTRODE_ZAP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      }
   }

   public boolean shouldDischargeAfterTakingDamage() {
      return this.hasMaxCharge() && this.hasElysiumArmour() && this.entity.getRandom().nextBoolean();
   }

   public boolean hasMaxCharge() {
      return this.charge >= 10;
   }

   public boolean shouldDischargeWhenAttacking() {
      return this.hasElysiumArmour() && this.entity.getRandom().nextBetween(1, 4) <= this.vulnerabilityLevel();
   }

   public int vulnerabilityLevel() {
      return switch (this.charge) {
         case 0 -> 0;
         case 1, 2, 3 -> 1;
         case 4, 5, 6 -> 2;
         case 7, 8, 9 -> 3;
         default -> 4;
      };
   }

   public boolean hasElysiumArmour() {
      for (ItemStack slot : this.entity.getArmorItems()) {
         if (!slot.isIn(ElysiumArmour.ELYSIUM_ARMOUR_TAG)) {
            return false;
         }
      }

      return true;
   }

   public void readFromNbt(NbtCompound tag) {
   }

   public void writeToNbt(NbtCompound tag) {
   }

   public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
      buf.writeByte(this.charge);
   }

   public void applySyncPacket(PacketByteBuf buf) {
      this.charge = buf.readByte();
   }

   public void clientTick() {
      if (this.hasElysiumArmour() && this.entity.age % 10 == 0) {
         Random random = this.entity.getRandom();
         int particles = (int)((float)this.charge * random.nextFloat());

         if (!EnchantingClientUtil.shouldAddParticles(this.entity))
         {
            particles = Math.min(particles, 1);
         }

         for (int i = 0; i < particles; i++) {
            this.entity
               .world
               .addParticle(
                  new ArcParticleOption(
                     this.entity.getParticleX(0.5), this.entity.getBodyY(random.nextDouble() * 0.8), this.entity.getParticleZ(0.5), 0.3F, this.entity.getId()
                  ),
                  this.entity.getParticleX(0.5),
                  this.entity.getRandomBodyY(),
                  this.entity.getParticleZ(0.5),
                  0.0,
                  0.0,
                  0.0
               );
         }
      }
   }
}
