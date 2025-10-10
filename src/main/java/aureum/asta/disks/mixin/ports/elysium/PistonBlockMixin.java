package aureum.asta.disks.mixin.ports.elysium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.piston.MovingPistonBlockEntityHooks;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({PistonBlock.class})
public class PistonBlockMixin {
   @ModifyExpressionValue(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/BlockState;hasBlockEntity()Z"
      )},
      method = {"isMovable"}
   )
   private static boolean elysium$allowPushingElysiumBlockEntities(boolean original, BlockState state) {
      return original && !Registries.BLOCK.getId(state.getBlock()).getNamespace().equals("elysium");
   }

   @Inject(
           method = {"move"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"
           )},
           slice = {@Slice(
                   from = @At("HEAD"),
                   to = @At(
                           value = "INVOKE",
                           target = "Lnet/minecraft/block/piston/PistonHandler;getBrokenBlocks()Ljava/util/List;"
                   )
           )}
   )
   private void elysium$saveBEs(
           World level,
           BlockPos pistonPos,
           Direction pushDir,
           boolean extending,
           CallbackInfoReturnable<Boolean> cir,
           @Local List<BlockPos> blocksToPush,
           @Share("pushingBlockEntityData") LocalRef<Map<BlockPos, NbtCompound>> pushingBlockEntityData
   ) {
      List<Pair<BlockPos, BlockEntity>> positionsWithBlockEntities = blocksToPush.stream()
         .map(pos -> Pair.of(pos, level.getBlockEntity(pos)))
         .filter(pairx -> pairx.getSecond() != null)
         .toList();
      pushingBlockEntityData.set(
         (Map)positionsWithBlockEntities.stream()
            .map(pairx -> pairx.mapSecond(BlockEntity::createNbt))
            .map(pairx -> pairx.mapFirst(pos -> pos.offset(extending ? pushDir : pushDir.getOpposite())))
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
      );

      for (Pair<BlockPos, BlockEntity> pair : positionsWithBlockEntities) {
         level.removeBlockEntity((BlockPos)pair.getFirst());
      }
   }

   @ModifyExpressionValue(
      method = {"move"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;",
              ordinal = 0
      )}
   )
   private BlockEntity elysium$saveBEToMovingPiston1(
      BlockEntity movingPiston, @Share("pushingBlockEntityData") LocalRef<Map<BlockPos, NbtCompound>> pushingBlockEntityData
   ) {
      if (movingPiston != null) {
         loadBE((PistonBlockEntity)movingPiston, (Map<BlockPos, NbtCompound>)pushingBlockEntityData.get());
      } else {
         Elysium.LOGGER.warn("Moving piston BE is null @ saveBEToMovingPiston1");
      }

      return movingPiston;
   }

   @ModifyExpressionValue(
      method = {"move"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;",
              ordinal = 1
      )}
   )
   private BlockEntity elysium$saveBEToMovingPiston2(
      BlockEntity movingPiston, @Share("pushingBlockEntityData") LocalRef<Map<BlockPos, NbtCompound>> pushingBlockEntityData
   ) {
      if (movingPiston != null) {
         loadBE((PistonBlockEntity)movingPiston, (Map<BlockPos, NbtCompound>)pushingBlockEntityData.get());
      } else {
         Elysium.LOGGER.warn("Moving piston BE is null @ saveBEToMovingPiston2");
      }

      return movingPiston;
   }

   @Unique
   private static void loadBE(PistonBlockEntity movingPiston, Map<BlockPos, NbtCompound> beTags) {
      NbtCompound tag = beTags.get(movingPiston.getPos());
      if (tag != null) {
         Elysium.LOGGER.debug("Saving block entity {} to moving piston @ {}", tag, movingPiston.getPos());
         ((MovingPistonBlockEntityHooks)movingPiston).elysium$setMovingBlockEntityTag(tag);
      }
   }
}
