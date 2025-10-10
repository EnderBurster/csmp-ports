package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import java.util.List;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteSparkBlockEntity;
import aureum.asta.disks.ports.amarite.amarite.cca.BeaconComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MBeaconBeamSegment;
import aureum.asta.disks.mixin.ports.amarite.mialib.mixin.accessors.BeaconBlockEntityAccessor;

@Mixin({BeaconBlockEntity.class})
public abstract class BeaconBlockEntityMixin {
   @Shadow
   public static void playSound(World world, BlockPos pos, SoundEvent sound) {
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private static void amarite$sparkling(
      @NotNull World world,
      BlockPos pos,
      BlockState state,
      BeaconBlockEntity blockEntity,
      CallbackInfo ci,
      @Share("stop") @NotNull LocalBooleanRef stop,
      @Share("started") @NotNull LocalBooleanRef started
   ) {
      stop.set(false);
      started.set(false);
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
      )}
   )
   private static Block amarite$sparkling(
      BlockState instance,
      Operation<Block> original,
      World world,
      BlockPos pos,
      BlockState state,
      BeaconBlockEntity blockEntity,
      @Local(ordinal = 1) BlockPos blockPos,
      @Share("stop") LocalBooleanRef stop,
      @Share("started") @NotNull LocalBooleanRef started
   ) {
      if (started.get()) {
         BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor)blockEntity;
         accessor.setMinY(world.getBottomY() - 1);
         return Blocks.AIR;
      } else {
         BlockState targetBlock = world.getBlockState(blockPos);
         boolean stopped = false;
         if (targetBlock.isOf(AmariteBlocks.AMARITE_SPARK) && world.getBlockEntity(blockPos) instanceof AmariteSparkBlockEntity spark) {
            spark.setPowered(2);
            stopped = true;
         } else if (targetBlock.isOf(Blocks.TINTED_GLASS)) {
            stopped = true;
         } else if (targetBlock.isOf(Blocks.AMETHYST_BLOCK)) {
            ((BeaconComponent)Amarite.BEACON.get(blockEntity)).startConversion(blockPos);
            stopped = true;
         }

         if (stopped) {
            BeamSegment endSegment = new BeamSegment(new float[]{1.0F, 1.0F, 1.0F});
            ((MBeaconBeamSegment)endSegment).mialib$setHidden(true);
            BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor)blockEntity;
            List<BeamSegment> segments = accessor.getField();
            segments.add(endSegment);
            accessor.setBeamSegments(segments);
            accessor.setMinY(world.getBottomY() - 1);
            stop.set(true);
            started.set(true);
            return Blocks.AIR;
         } else {
            return (Block)original.call(instance);
         }
      }
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/BlockState;getOpacity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)I"
      )}
   )
   private static int amarite$ord(
      BlockState instance, BlockView blockView, BlockPos blockPos, Operation<Integer> original, @Share("stop") @NotNull LocalBooleanRef stop
   ) {
      return !stop.get() ? (Integer)original.call(instance, blockView, blockPos) : 0;
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/entity/BeaconBlockEntity$BeamSegment;increaseHeight()V",
         ordinal = 1
      )}
   )
   private static void amarite$ord(BeamSegment instance, Operation<Void> original, @Share("stop") @NotNull LocalBooleanRef stop) {
      if (!stop.get()) {
         original.call(instance);
      }
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
      )}
   )
   private static BlockPos amarite$up(
      BlockPos instance,
      Operation<BlockPos> original,
      World world,
      BlockPos pos,
      BlockState state,
      BeaconBlockEntity blockEntity,
      @Share("stop") @NotNull LocalBooleanRef stop
   ) {
      if (stop.get()) {
         ((BeaconBlockEntityAccessor)blockEntity).setMinY(world.getBottomY() - 1);
         return instance.add(0, world.getTopY(), 0);
      } else {
         return (BlockPos)original.call(instance);
      }
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I"
      )}
   )
   private static int amarite$stop(World world, int x, int y, int z, @NotNull Operation<Integer> original, @Share("stop") @NotNull LocalBooleanRef stop) {
      Integer level = (Integer)original.call(world, x, y, z);
      return level == 0 && stop.get() ? -1 : level;
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/World;getBottomY()I"
      )},
      cancellable = true
   )
   private static void amarite$on(
      World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci, @Share("stop") @NotNull LocalBooleanRef stop
   ) {
      if (stop.get()) {
         ci.cancel();
      }
   }
}
