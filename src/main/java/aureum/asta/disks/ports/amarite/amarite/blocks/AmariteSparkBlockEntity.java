package aureum.asta.disks.ports.amarite.amarite.blocks;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.BuddedComponent;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;

public class AmariteSparkBlockEntity extends BlockEntity {
   private static final double RANGE = 56.0;
   private int powerTime;

   public AmariteSparkBlockEntity(BlockPos pos, BlockState state) {
      super(AmariteBlocks.AMARITE_SPARK_BLOCK_ENTITY, pos, state);
   }

   public static void tick(World world, BlockPos pos, BlockState state, @NotNull AmariteSparkBlockEntity spark) {
      if (world instanceof ServerWorld) {
         if (world.getTime() % 20L == 0L && spark.isPowered()) {
            spark.setPowered(spark.powerTime - 1);

            for (PlayerEntity player : world.getNonSpectatingEntities(PlayerEntity.class, Box.of(Vec3d.ofCenter(pos), 56.0, 56.0, 56.0))) {
               if (pos.isWithinDistance(player.getBlockPos(), 56.0)) {
                  if (((BuddedComponent)Amarite.BUDDED.get(player)).getBudTime() > 0) {
                     player.addStatusEffect(new StatusEffectInstance(AmariteEntities.BUDDING, 24, 0, false, false, true));
                  }

                  for (AmariteLongswordItem.LongswordMode mode : AmariteLongswordItem.getModes(player)) {
                     mode.absorbDamage(0.5F);
                  }
               }
            }
         }
      } else if (spark.isPowered() || world.getReceivedRedstonePower(pos) > 0) {
         clientTick(world, pos, state, spark);
      }
   }

   private static void clientTick(World world, BlockPos pos, BlockState state, @NotNull AmariteSparkBlockEntity spark) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         if (pos.isWithinDistance(player.getBlockPos(), 112.0)) {
            Direction direction = (Direction)state.get(AmariteSparkBlock.FACING);
            float r = 0.6745098F;
            float g = 0.3882353F;
            float b = 0.8784314F;
            Vec3d point = Vec3d.ofCenter(pos);

            for (int i = 0; i < 2; i++) {
               Vec3d motion = Vec3d.of(direction.getVector()).multiply((double)(world.random.nextFloat() * 0.65F));
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
                  .setSpinOffset((float)world.getRandom().nextInt(360))
                  .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                  .addMotion(motion.x, motion.y, motion.z)
                  .randomOffset(0.2F)
                  .spawn(world, point.x, point.y, point.z);
            }

            if (world.getTime() % 20L == 0L && spark.isPowered()) {
               for (int i = 0; i < 16; i++) {
                  ParticleBuilders.create(AmariteParticles.AMARITE)
                     .overrideAnimator(Animator.WITH_AGE)
                     .setLifetime(24)
                     .setAlpha(0.6F, 0.0F)
                     .setAlphaEasing(Easing.CUBIC_IN)
                     .setColorCoefficient(0.8F)
                     .setColorEasing(Easing.CIRC_OUT)
                     .setSpinEasing(Easing.SINE_IN)
                     .setColor(r, g, b, 1.0F)
                     .setScale(0.24F, 0.12F)
                     .setSpinOffset((float)world.getRandom().nextInt(360))
                     .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                     .randomMotion(0.32)
                     .randomOffset(0.2F)
                     .spawn(world, point.x, point.y, point.z);
               }
            }

            for (int i = 0; i < 1; i++) {
               ParticleBuilders.create(AmariteParticles.AMARITE)
                  .overrideAnimator(Animator.WITH_AGE)
                  .setLifetime(64)
                  .setAlpha(0.6F, 0.0F)
                  .setAlphaEasing(Easing.CUBIC_IN)
                  .setColorCoefficient(0.8F)
                  .setColorEasing(Easing.CIRC_OUT)
                  .setSpinEasing(Easing.SINE_IN)
                  .setColor(r, g, b, 1.0F)
                  .setScale(0.24F, 0.12F)
                  .setSpinOffset((float)world.getRandom().nextInt(360))
                  .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                  .randomMotion(0.02F)
                  .randomOffset(28.0)
                  .spawn(world, point.x, point.y, point.z);
            }
         }
      }
   }

   public boolean isPowered() {
      return this.powerTime > 0;
   }

   public void setPowered(int powerTime) {
      this.powerTime = powerTime;
      if (this.world != null) {
         this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 2);
      }
   }

   protected void writeNbt(@NotNull NbtCompound nbt) {
      nbt.putInt("powerTime", this.powerTime);
   }

   public void readNbt(@NotNull NbtCompound nbt) {
      this.powerTime = nbt.getInt("powerTime");
   }

   @Nullable
   public Packet<ClientPlayPacketListener> toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt() {
      return this.createNbt();
   }
}
