package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

public class LongswordDoubleDashComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   public static final int DOUBLE_DASH_MAX_CHARGE = 240;
   public static final int DOUBLE_DASH_MAX_DURATION = 3;
   public static final int DOUBLE_DASH_COLOR = -11627802;
   private final PlayerEntity player;
   private int doubleDashCharge = 240;
   private int doubleDashDuration = 0;

   public LongswordDoubleDashComponent(PlayerEntity player) {
      this.player = player;
   }

   private void sync() {
      Amarite.DOUBLE_DASH.sync(this.player);
   }

   public void serverTick() {
      if (this.isDashing()) {
         this.player.fallDistance = 0.0F;
      }

      this.tick();
   }

   public void clientTick() {
      if (this.isDashing()) {
         this.player.setVelocity(this.player.getRotationVector().multiply(3.0, 1.0, 3.0));
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
                  (float)(1.2F + offset),
                  false
               );
         }

         float r = 0.30588236F;
         float g = 0.57254905F;
         float b = 0.9019608F;
         Vec3d motion = this.player.getVelocity();

         for (int i = 0; i < 4; i++) {
            Vec3d point = this.player.getPos().add(0.0, (double)(this.player.getHeight() / 2.0F), 0.0);
            ParticleBuilders.create(AmariteParticles.ACCUMULATION)
               .overrideAnimator(Animator.WITH_AGE)
               .setLifetime(8)
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
               .setLifetime(6)
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
         if (this.doubleDashCharge < 240) {
            this.doubleDashCharge++;
            if (this.doubleDashCharge == 240) {
               this.sync();
            }
         }
      } else {
         this.doubleDashDuration--;
         if (this.doubleDashDuration == 0) {
            this.sync();
         }
      }
   }

   public boolean isDashing() {
      return this.doubleDashDuration > 0;
   }

   @Override
   public void absorbDamage(float base) {
      this.doubleDashCharge += (int)(base * 20.0F);
      if (this.doubleDashCharge > 480) {
         this.doubleDashCharge = 480;
      }

      this.sync();
   }

   @Override
   public void useAbility() {
      if (this.doubleDashCharge >= 120 || this.player.isCreative()) {
         if (!this.player.isCreative()) {
            this.doubleDashCharge -= 120;
         }

         this.doubleDashDuration = 3;
         this.sync();
      }
   }

   @Override
   public int getModeColor() {
      return -11627802;
   }

   @Override
   public int getSwordTint() {
      float percent = MathHelper.clamp(this.getChargeProgress() * 2.0F, 0.0F, 1.0F);
      int r = (int)(255.0F - percent * 177.0F);
      int g = (int)(255.0F - percent * 109.0F);
      int b = (int)(255.0F - percent * 25.0F);
      return MMath.packRgb(r, g, b);
   }

   @Override
   public float getChargeProgress() {
      return (float)this.doubleDashCharge / 240.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".double_dash";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.doubleDashCharge = tag.getInt("dashCharge");
      this.doubleDashDuration = tag.getInt("dashDuration");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.doubleDashCharge != 0) {
         tag.putInt("dashCharge", this.doubleDashCharge);
      }

      if (this.doubleDashDuration != 0) {
         tag.putInt("dashDuration", this.doubleDashDuration);
      }
   }
}
