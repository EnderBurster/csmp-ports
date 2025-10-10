package aureum.asta.disks.ports.amarite.amarite.blocks;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.BlockRotation;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.state.StateManager.Builder;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;

public class AmariteBlock extends Block {
   public static final DirectionProperty FACING = Properties.FACING;
   public static final int PARTICLE_COLOR = -5479456;

   public AmariteBlock(Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
   }

   public void onProjectileHit(@NotNull World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      if (!world.isClient) {
         BlockPos blockPos = hit.getBlockPos();
         world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1.0F, 0.5F + world.random.nextFloat() * 0.6F);
         world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 0.5F + world.random.nextFloat() * 0.6F);
      } else {
         Vec3d point = hit.getPos();
         float r = 0.6745098F;
         float g = 0.3882353F;
         float b = 0.8784314F;

         for (int i = 0; i < 8; i++) {
            ParticleBuilders.create(AmariteParticles.AMARITE)
               .overrideAnimator(Animator.WITH_AGE)
               .setLifetime(16)
               .setAlpha(0.6F, 0.0F)
               .setAlphaEasing(Easing.CUBIC_IN)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor(r, g, b, 1.0F)
               .setScale(0.24F, 0.12F)
               .setSpinOffset((float)world.getRandom().nextInt(360))
               .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
               .randomMotion(0.1F)
               .spawn(world, point.x, point.y, point.z);
         }
      }
   }

   public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getSide());
   }

   public BlockState rotate(@NotNull BlockState state, @NotNull BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   public BlockState mirror(@NotNull BlockState state, @NotNull BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(@NotNull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
