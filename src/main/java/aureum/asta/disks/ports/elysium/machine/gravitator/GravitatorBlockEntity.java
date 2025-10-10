package aureum.asta.disks.ports.elysium.machine.gravitator;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.ElysiumUtil;
import aureum.asta.disks.ports.elysium.machine.BeamPowered;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachineBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import aureum.asta.disks.ports.elysium.particles.MagneticWaveParticleOption;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GravitatorBlockEntity extends BlockEntity implements BeamPowered {
   private static final double[] STRENGTH_BY_POWER = new double[]{0.0, 0.04, 0.08, 0.16, 0.2};
   private static final int[] REACH_BY_POWER = new int[]{0, 5, 10, 12, 16};
   private static final int[] WIDTH_BY_POWER = new int[]{0, 1, 1, 3, 4};
   private static final float[] PARTICLE_WIDTH_BY_POWER = new float[]{0.0F, 1.0F, 1.0F, 1.5F, 2.5F};
   @Nullable
   private BlockPos beamSourcePos;
   private Box[] aabbs = calculateAABBs(this.pos, this.getCachedState());
   private BlockState lastState = this.getCachedState();

   public GravitatorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElysiumMachines.GRAVITATOR_BE, blockPos, blockState);
   }

   private Box getAABB(BlockState state) {
      if (this.lastState.get(Properties.FACING) != state.get(Properties.FACING)) {
         this.lastState = state;
         this.aabbs = calculateAABBs(this.pos, state);
      }

      return this.aabbs[state.get(ElysiumMachines.ELYSIUM_POWER) - 1];
   }

   public static void tick(World level, BlockPos pos, BlockState state, GravitatorBlockEntity be, boolean pushesOutwards) {
      int power = (Integer)state.get(ElysiumMachines.ELYSIUM_POWER);

      if (be.getBeamSourcePos() != null) {
         int actualPower = be.getBeamPower(level, pos);
         if (actualPower == 0) {
            be.setBeamSourcePos(null);
         }

         if (actualPower != power) {
            Direction neighbourDir = ((Direction)state.get(Properties.FACING)).getOpposite();
            BlockPos neighbourPos = pos.offset(neighbourDir);
            level.setBlockState(pos, state.getStateForNeighborUpdate(neighbourDir, level.getBlockState(neighbourPos), level, pos, neighbourPos));
         }
      }

      Vec3i dir = ((Direction)state.get(Properties.FACING)).getVector();
      if (power >= 1) {
         if (level.isClient()) {
            if (level.getTime() % 12L == 0L) {
               Vec3d particlePos = Vec3d.ofCenter(pos).add(Vec3d.of(dir).multiply(pushesOutwards ? 0.5 : (double)REACH_BY_POWER[power]));
               Vec3d particleVel = Vec3d.of(pushesOutwards ? dir : dir.multiply(-1)).multiply(0.3 * (double)power * 0.5);
               level.addParticle(
                  new MagneticWaveParticleOption(!pushesOutwards, (float)REACH_BY_POWER[power] - 0.5F, PARTICLE_WIDTH_BY_POWER[power]),
                  particlePos.x,
                  particlePos.y,
                  particlePos.z,
                  particleVel.x,
                  particleVel.y,
                  particleVel.z
               );
            }
         }
         else {
            Vec3d pushDir = Vec3d.of(dir);
            Vec3d targetVel = pushDir.multiply(pushesOutwards ? STRENGTH_BY_POWER[power] : -STRENGTH_BY_POWER[power]);

            for (Entity entity : level.getOtherEntities((Entity)null, be.getAABB(state), Predicate.not(GravitatorBlockEntity::isImmune))) {
               pushEntity(
                  targetVel.multiply(
                     Math.max(0.0, 1.0 - entity.squaredDistanceTo(Vec3d.ofCenter(pos)) / (double)(REACH_BY_POWER[power] * REACH_BY_POWER[power]))
                  ),
                  entity
               );
            }
         }
      }
   }

   public static void pushEntity(Vec3d targetVel, Entity entity) {
      double mass = getMass(entity);
      double magnetism = getMagnetism(entity);
      Vec3d scaledTargetVel = targetVel.multiply(mass == 0.0 ? magnetism : magnetism / mass);

      entity.addVelocity(scaledTargetVel.x, scaledTargetVel.y, scaledTargetVel.z);
      if (targetVel.getY() >= 0.0) {
         entity.fallDistance = 0.0F;
      }

      entity.velocityDirty = true;
      entity.velocityModified = true;
   }

   private static boolean isImmune(Entity entity) {
      return entity.isSpectator() || entity instanceof PlayerEntity player && player.getAbilities().creativeMode || entity instanceof AbstractDecorationEntity;
   }

   private static double getMass(Entity entity) {
      return Math.max((double)entity.getHeight(), 1.0)
         * Math.max((double)entity.getWidth(), 1.0)
         / (double)(EntityType.PLAYER.getHeight() * EntityType.PLAYER.getWidth());
   }

   private static double getMagnetism(Entity entity) {
      double entityMagnetism = ElysiumMachines.ENTITY_MAGNETISM.get(entity.getType()).orElse(1.0);
      double itemMagnetism = ElysiumUtil.getItemForEntity(entity).<Double>flatMap(ElysiumMachines.ITEM_MAGNETISM::get).orElse(0.0);
      double armourMagnetism = entity instanceof LivingEntity lE ? getArmourMagnetism(lE) : 0.0;
      return entityMagnetism + itemMagnetism + armourMagnetism;
   }

   private static double getArmourMagnetism(LivingEntity entity) {
      double count = 0.0;

      for (ItemStack armour : entity.getArmorItems()) {
         count += ElysiumMachines.ITEM_MAGNETISM.get(armour.getItem()).orElse(0.0);
      }

      return count;
   }

   private static Box calculateAABB(BlockPos pos, Direction pointing, int width, int length) {
      Vec3i pointingVec = pointing.getVector();
      int[] dims = new int[]{pointingVec.getX() == 0 ? width : length, pointingVec.getY() == 0 ? width : length, pointingVec.getZ() == 0 ? width : length};
      return Box.of(Vec3d.ofCenter(pos).add(Vec3d.of(pointingVec).multiply((double)length / 2.0 + 0.5)), (double)dims[0], (double)dims[1], (double)dims[2]);
   }

   private static Box[] calculateAABBs(BlockPos pos, BlockState state) {
      Direction pointing = (Direction)state.get(Properties.FACING);
      return new Box[]{
         calculateAABB(pos, pointing, WIDTH_BY_POWER[1], REACH_BY_POWER[1]),
         calculateAABB(pos, pointing, WIDTH_BY_POWER[2], REACH_BY_POWER[2]),
         calculateAABB(pos, pointing, WIDTH_BY_POWER[3], REACH_BY_POWER[3]),
         calculateAABB(pos, pointing, WIDTH_BY_POWER[4], REACH_BY_POWER[4])
      };
   }

   @Nullable
   @Override
   public BlockPos getBeamSourcePos() {
      return this.beamSourcePos;
   }

   @Override
   public void setBeamSourcePos(@Nullable BlockPos pos) {
      this.beamSourcePos = pos;
   }

   @Override
   public boolean canAcceptBeam(Direction beamDir) {
      return ((ElysiumMachineBlock)ElysiumMachines.GRAVITATOR.getFirst()).isReceivingSide(this.getCachedState(), beamDir.getOpposite());
   }
}
