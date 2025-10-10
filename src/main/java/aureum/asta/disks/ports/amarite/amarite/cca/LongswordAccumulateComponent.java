package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;
import aureum.asta.disks.ports.amarite.mialib.raycasting.MRaycasting;

public class LongswordAccumulateComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   public static final Identifier ACCUMULATE_TARGETS = Amarite.id("accumulate");
   public static final int ACCUMULATE_MAX_CHARGE = 800;
   public static final int ACCUMULATE_TICK_COST = 13;
   public static final int ACCUMULATE_COLOR = -1082054;
   private final PlayerEntity player;
   private final List<Entity> accumulateTargets = new ArrayList<>();
   private int accumulateCharge = 800;
   public boolean accumulateActive = false;
   public int accumulateActiveTicks = 0;

   public LongswordAccumulateComponent(PlayerEntity player) {
      this.player = player;
   }

   private void sync() {
      Amarite.ACCUMULATE.sync(this.player);
   }

   public void serverTick() {
      if (this.accumulateActive && !this.accumulateTargets.isEmpty()) {
         Vec3d userAnchor = this.player.getEyePos().add(this.player.getRotationVec(1.0F).multiply(1.32));

         for (int i = 0; i < this.accumulateTargets.size(); i++) {
            Entity target = this.accumulateTargets.get(i);
            if (!(target instanceof PersistentProjectileEntity)) {
               target.setVelocity(
                  target.getVelocity()
                     .multiply(0.4)
                     .add(
                        userAnchor.subtract(target.getPos().subtract(0.0, (double)(-target.getHeight() / 2.0F), 0.0))
                           .normalize()
                           .multiply(0.15)
                     )
               );
               target.velocityModified = true;
            }

            if (target instanceof LivingEntity) {
               target.timeUntilRegen = 0;
               target.damage(this.player.getWorld().getDamageSources().create(AmariteDamageTypes.ACCUMULATE, this.player), 0.04F);
               target.timeUntilRegen = 0;
            }

            if (!this.player.isDead()) {
               this.player.setHealth(this.player.getHealth() + 0.06F / (float)(i + 1));
            }
         }
      }

      this.tick();
   }

   public void clientTick() {
      if (this.accumulateActive) {
         float r = 0.9372549F;
         float g = 0.49019608F;
         float b = 0.22745098F;
         if (this.player.age % 2 == 0 && MinecraftClient.getInstance().player == this.player) {
            List<Entity> targets = MRaycasting.raycast(this.player, 96.0, (p, e) -> e.getRootVehicle() != this.player.getRootVehicle(), 0, 0.48);
            ClientPlayNetworking.send(ACCUMULATE_TARGETS, PacketByteBufs.create().writeIntArray(targets.stream().mapToInt(Entity::getId).toArray()));

            for (Entity entity : targets) {
               for (int i = 0; i < 4; i++) {
                  Vec3d pos = entity.getPos().add(0.0, (double)(this.player.getHeight() / 2.0F), 0.0);
                  ParticleBuilders.create(AmariteParticles.AMARITE)
                     .overrideAnimator(Animator.WITH_AGE)
                     .setLifetime(12)
                     .setAlpha(0.6F, 0.0F)
                     .setAlphaEasing(Easing.CUBIC_IN)
                     .setColorCoefficient(0.8F)
                     .setColorEasing(Easing.CIRC_OUT)
                     .setSpinEasing(Easing.SINE_IN)
                     .setColor(r, g, b, 1.0F)
                     .setScale(0.3F, 0.12F)
                     .setSpinOffset((float)this.player.getRandom().nextInt(360))
                     .setSpin(this.player.getRandom().nextBoolean() ? 0.5F : -0.5F)
                     .randomMotion(0.2)
                     .randomOffset((double)(entity.getWidth() / 2.0F), (double)(entity.getHeight() / 2.0F))
                     .spawn(this.player.world, pos.x, pos.y, pos.z);
               }
            }
         }

         if (this.player.age % 4 == 0) {
            this.player
               .world
               .playSound(
                  this.player.getX(),
                  this.player.getY(),
                  this.player.getZ(),
                  AmariteSoundEvents.SWORD_ACCUMULATE,
                  SoundCategory.PLAYERS,
                  3.0F,
                  2.0F,
                  false
               );
         }

         BlockHitResult hit = this.player
            .world
            .raycast(
               new RaycastContext(
                  this.player.getEyePos(),
                  this.player.getEyePos().add(this.player.getRotationVector().multiply(64.0)),
                  ShapeType.COLLIDER,
                  FluidHandling.NONE,
                  this.player
               )
            );
         Vec3d hitPos = hit.getPos();
         double totalDist = this.player.getEyePos().distanceTo(hitPos);

         for (int i = 0; i < 4; i++) {
            double dist = (double)this.player.getRandom().nextFloat() * totalDist;
            Vec3d point = this.player.getEyePos().add(this.player.getRotationVector().multiply(dist));
            Vec3d motion = this.player.getRotationVector().multiply(-dist / 36.0);
            ParticleBuilders.create(AmariteParticles.ACCUMULATION)
               .overrideAnimator(Animator.WITH_AGE)
               .setLifetime(36)
               .setAlpha(0.6F, 0.0F)
               .setAlphaEasing(Easing.CUBIC_IN)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor(r, g, b, 1.0F)
               .setScale((float)(dist / 16.0), 0.12F)
               .setSpinOffset((float)this.player.getRandom().nextInt(360))
               .setSpin(this.player.getRandom().nextBoolean() ? 0.5F : -0.5F)
               .setMotion(motion.x, motion.y, motion.z)
               .spawn(this.player.world, point.x, point.y, point.z);
         }
      }

      this.tick();
   }

   public void tick() {
      if (!this.accumulateActive) {
         this.accumulateActiveTicks = 0;
         if (this.accumulateCharge < 800) {
            this.accumulateCharge++;
            if (this.accumulateCharge == 800) {
               this.sync();
            }
         }
      }
      else {
         this.accumulateActiveTicks++;
         if (!this.player.isCreative()) {
            this.accumulateCharge -= 13;
         }

         if (this.accumulateCharge <= 0
            || this.accumulateActiveTicks > 2 && this.player.mialib$startedAttacking()
            || AmariteLongswordItem.getMode(this.player, this.player.getMainHandStack()) != this) {
            this.accumulateActive = false;
            this.sync();
         }
      }
   }

   @Override
   public void absorbDamage(float base) {
      this.accumulateCharge += (int)(base * 20.0F);
      if (this.accumulateCharge > 1600) {
         this.accumulateCharge = 1600;
      }

      this.sync();
   }

   @Override
   public void useAbility() {
      if (this.accumulateCharge >= 800 || this.player.isCreative()) {
         this.accumulateTargets.clear();
         this.accumulateActive = true;
         this.sync();
      }
   }

   @Override
   public int getModeColor() {
      return -1082054;
   }

   @Override
   public int getSwordTint() {
      float percent = MathHelper.clamp(this.getChargeProgress(), 0.0F, 1.0F);
      int r = (int)(255.0F - percent * 16.0F);
      int g = (int)(255.0F - percent * 130.0F);
      int b = (int)(255.0F - percent * 197.0F);

      return MMath.packRgb(r, g, b);
   }

   @Override
   public float getChargeProgress() {
      return (float)this.accumulateCharge / 800.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".accumulate";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.accumulateCharge = tag.getInt("accumulateCharge");
      this.accumulateActive = tag.getBoolean("accumulateActive");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.accumulateCharge != 0) {
         tag.putInt("accumulateCharge", this.accumulateCharge);
      }

      if (this.accumulateActive) {
         tag.putBoolean("accumulateActive", true);
      }
   }

   static {
      ServerPlayNetworking.registerGlobalReceiver(ACCUMULATE_TARGETS, (server, player, handler, buf, responseSender) -> {
         int[] array = buf.readIntArray();
         server.execute(() -> {
            LongswordAccumulateComponent component = (LongswordAccumulateComponent)Amarite.ACCUMULATE.get(player);
            component.accumulateTargets.clear();

            for (int id : array) {
               Entity entity = player.world.getEntityById(id);
               if (entity != null) {
                  component.accumulateTargets.add(entity);
               }
            }
         });
      });
   }
}
