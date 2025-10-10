package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

public class LongswordDashComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   public static final int DASH_MAX_CHARGE = 400;
   public static final int DASH_MAX_DURATION = 10;
   public static final int DASH_COLOR = -5479456;
   private final PlayerEntity player;
   private final IntOpenHashSet slicedEntities = new IntOpenHashSet();
   private int dashCharge = 400;
   private int dashDuration = 0;
   private Box prevBoundingBox;

   public LongswordDashComponent(PlayerEntity player) {
      this.player = player;
   }

   private void sync() {
      Amarite.DASH.sync(this.player);
   }

   public void serverTick() {
      if (this.isDashing()) {
         this.player.fallDistance = 0.0F;

         if(this.prevBoundingBox == null) {
            this.prevBoundingBox = this.player.getBoundingBox();
            return;
         }

         for (Entity entity : this.player
            .world
            .getOtherEntities(
               this.player,
               this.prevBoundingBox.union(this.player.getBoundingBox()),
               e -> !this.slicedEntities.contains(e.getId()) && e instanceof LivingEntity
            )) {
            this.slicedEntities.add(entity.getId());
            entity.timeUntilRegen = 0;
            entity.damage(this.player.getWorld().getDamageSources().create(AmariteDamageTypes.DASH, this.player), 12.0F);
            entity.timeUntilRegen = 0;
         }

         this.prevBoundingBox = this.player.getBoundingBox();
      }

      this.tick();
   }

   public void clientTick() {
      if (this.isDashing()) {
         this.player.setVelocity(this.player.getRotationVector().multiply(1.5, 1.0, 1.5));
         ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
         if (clientPlayer != null) {
            double offset = 0.0;
            if (clientPlayer != this.player) {
               double dist = clientPlayer.getEyePos().distanceTo(this.player.getPos());
               double oldDist = clientPlayer.getEyePos().distanceTo(new Vec3d(this.player.prevX, this.player.prevY, this.player.prevZ));
               offset = MathHelper.lerp(-0.2, 0.2, dist - oldDist);
            }

            this.player
               .world
               .playSound(
                  this.player.getX(),
                  this.player.getY(),
                  this.player.getZ(),
                  AmariteSoundEvents.SWORD_DASH,
                  SoundCategory.PLAYERS,
                  1.0F,
                  (float)(0.8F + offset),
                  false
               );
         }

         float r = 0.6745098F;
         float g = 0.3882353F;
         float b = 0.8784314F;
         Vec3d motion = this.player.getVelocity();

         for (int i = 0; i < 4; i++) {
            Vec3d point = this.player.getPos().add(0.0, (double)(this.player.getHeight() / 2.0F), 0.0);
            ParticleBuilders.create(AmariteParticles.ACCUMULATION)
               .overrideAnimator(Animator.WITH_AGE)
               .setLifetime(12)
               .setAlpha(0.6F, 0.0F)
               .setAlphaEasing(Easing.CUBIC_IN)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor(r, g, b, 1.0F)
               .setScale(1.0F, 0.12F)
               .setSpinOffset((float)this.player.getRandom().nextInt(360))
               .setSpin(this.player.getRandom().nextBoolean() ? 0.5F : -0.5F)
               .setMotion(motion.x, motion.y, motion.z)
               .spawn(this.player.world, point.x, point.y, point.z);
         }

         for (int i = 0; i < 8; i++) {
            Vec3d point = this.player.getPos().add(0.0, (double)(this.player.getHeight() / 2.0F), 0.0);
            Vec3d randVel = motion.add(
                  new Vec3d(
                        (double)this.player.getRandom().nextFloat(),
                        (double)this.player.getRandom().nextFloat(),
                        (double)this.player.getRandom().nextFloat()
                     )
                     .subtract(0.5, 0.5, 0.5)
                     .multiply(0.6)
               )
               .normalize()
               .multiply(motion.length());
            ParticleBuilders.create(AmariteParticles.AMARITE)
               .overrideAnimator(Animator.WITH_AGE)
               .setLifetime(8)
               .setAlpha(0.6F, 0.0F)
               .setAlphaEasing(Easing.CUBIC_IN)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor(r, g, b, 1.0F)
               .setScale(0.24F, 0.12F)
               .setSpinOffset((float)this.player.getRandom().nextInt(360))
               .setSpin(this.player.getRandom().nextBoolean() ? 0.5F : -0.5F)
               .setMotion(randVel.x, randVel.y, randVel.z)
               .spawn(this.player.world, point.x, point.y, point.z);
         }
      }

      this.tick();
   }

   public void tick() {
      if (!this.isDashing()) {
         if (this.dashCharge < 400) {
            this.dashCharge++;
            if (this.dashCharge == 400) {
               this.sync();
            }
         }
      } else {
         this.dashDuration--;
         if (this.dashDuration == 0) {
            this.sync();
         }
      }
   }

   public boolean isDashing() {
      return this.dashDuration > 0;
   }

   @Override
   public void absorbDamage(float base) {
      this.dashCharge += (int)(base * 20.0F);
      if (this.dashCharge > 800) {
         this.dashCharge = 800;
      }

      this.sync();
   }

   @Override
   public void useAbility() {
      if (this.dashCharge >= 400 || this.player.isCreative()) {
         this.slicedEntities.clear();
         if (!this.player.isCreative()) {
            this.dashCharge -= 400;
         }

         this.dashDuration = 10;
         this.prevBoundingBox = this.player.getBoundingBox();
         this.sync();
      }
   }

   @Override
   public int getModeColor() {
      return -5479456;
   }

   @Override
   public int getSwordTint() {
      float percent = MathHelper.clamp(this.getChargeProgress(), 0.0F, 1.0F);
      int r = (int)(255.0F - percent * 83.0F);
      int g = (int)(255.0F - percent * 156.0F);
      int b = (int)(255.0F - percent * 31.0F);
      return MMath.packRgb(r, g, b);
   }

   @Override
   public float getChargeProgress() {
      return (float)this.dashCharge / 400.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".dash";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.dashCharge = tag.getInt("dashCharge");
      this.dashDuration = tag.getInt("dashDuration");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.dashCharge != 0) {
         tag.putInt("dashCharge", this.dashCharge);
      }

      if (this.dashDuration != 0) {
         tag.putInt("dashDuration", this.dashDuration);
      }
   }
}
