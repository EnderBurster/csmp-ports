package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;

public class BeaconComponent implements AutoSyncedComponent, CommonTickingComponent {
   private static final int CONVERSION_TIME = 14400;
   public static final int PARTICLE_COLOR = -5479456;
   private final BeaconBlockEntity beaconBlockEntity;
   private BlockPos target = null;
   private Block targetBlock = null;
   private int conversionTime = 0;

   public BeaconComponent(BeaconBlockEntity beaconBlockEntity) {
      this.beaconBlockEntity = beaconBlockEntity;
   }

   public void sync() {
      Amarite.BEACON.sync(this.beaconBlockEntity);
   }

   public void clientTick() {
      World world = this.beaconBlockEntity.getWorld();
      if (this.conversionTime > 0 && this.target != null && world != null) {
         for (int i = 0; i < 2; i++) {
            Vec3d offset = new Vec3d(world.random.nextGaussian(), world.random.nextGaussian(), world.random.nextGaussian()).normalize();
            double distanceToXEdge = offset.x > 0.0 ? 0.5 / offset.x : 0.5 / Math.abs(offset.x);
            double distanceToYEdge = offset.y > 0.0 ? 0.5 / offset.y : 0.5 / Math.abs(offset.y);
            double distanceToZEdge = offset.z > 0.0 ? 0.5 / offset.z : 0.5 / Math.abs(offset.z);
            double distanceToEdge = Math.min(distanceToXEdge, Math.min(distanceToYEdge, distanceToZEdge));
            Vec3d pos = Vec3d.ofCenter(this.target).add(offset.multiply(distanceToEdge));
            world.addParticle(
               new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(this.target)), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0
            );
         }

         float r = 0.6745098F;
         float g = 0.3882353F;
         float b = 0.8784314F;
         ParticleBuilders.create(AmariteParticles.AMARITE)
            .overrideAnimator(Animator.WITH_AGE)
            .setLifetime(16)
            .setAlpha(0.6F, 0.0F)
            .setAlphaEasing(Easing.CUBIC_IN)
            .setColorCoefficient(0.8F)
            .setColorEasing(Easing.CIRC_OUT)
            .setSpinEasing(Easing.SINE_IN)
            .setColor(r, g, b, 1.0F)
            .setScale(0.16F, 0.08F)
            .setSpinOffset((float)world.getRandom().nextInt(360))
            .setSpin(world.getRandom().nextBoolean() ? 0.3F : -0.3F)
            .randomMotion(0.04)
            .spawnAtEdges(world, this.target);
         if (this.conversionTime == 1) {
            for (int i = 0; i < 128; i++) {
               ParticleBuilders.create(AmariteParticles.AMARITE)
                  .overrideAnimator(Animator.WITH_AGE)
                  .setLifetime(32)
                  .setAlpha(0.6F, 0.0F)
                  .setAlphaEasing(Easing.CUBIC_IN)
                  .setColorCoefficient(0.8F)
                  .setColorEasing(Easing.CIRC_OUT)
                  .setSpinEasing(Easing.SINE_IN)
                  .setColor(r, g, b, 1.0F)
                  .setScale(0.24F, 0.12F)
                  .setSpinOffset((float)world.getRandom().nextInt(360))
                  .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                  .randomMotion(0.2)
                  .spawnAtEdges(world, this.target);
            }
         }
      }

      this.tick();
   }

   public void serverTick() {
      World world = this.beaconBlockEntity.getWorld();
      if (this.conversionTime > 0 && world != null) {
         if (this.target == null || this.targetBlock == null || world.getBlockState(this.target).getBlock() != this.targetBlock) {
            this.target = null;
            this.targetBlock = null;
            this.conversionTime = 0;
            this.sync();
            return;
         }

         if (this.conversionTime == 1 && this.targetBlock == Blocks.AMETHYST_BLOCK) {
            world.setBlockState(this.target, Blocks.BUDDING_AMETHYST.getDefaultState());
         }
      }

      this.tick();
   }

   public void tick() {
      if (this.conversionTime > 0) {
         this.conversionTime--;
         if (this.conversionTime == 1 || this.conversionTime == 0) {
            this.sync();
         }
      } else {
         this.target = null;
      }
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public void startConversion(BlockPos target) {
      if (this.beaconBlockEntity.getWorld() != null && !target.equals(this.target)) {
         this.target = target;
         this.targetBlock = this.beaconBlockEntity.getWorld().getBlockState(target).getBlock();
         this.conversionTime = 14400;
         this.sync();
      }
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.conversionTime = tag.getInt("conversionTime");
      if (tag.contains("target")) {
         NbtCompound targetTag = tag.getCompound("target");
         this.target = new BlockPos(targetTag.getInt("x"), targetTag.getInt("y"), targetTag.getInt("z"));
      }
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      tag.putInt("conversionTime", this.conversionTime);
      if (this.target != null) {
         NbtCompound targetTag = new NbtCompound();
         targetTag.putInt("x", this.target.getX());
         targetTag.putInt("y", this.target.getY());
         targetTag.putInt("z", this.target.getZ());
         tag.put("target", targetTag);
      }
   }
}
