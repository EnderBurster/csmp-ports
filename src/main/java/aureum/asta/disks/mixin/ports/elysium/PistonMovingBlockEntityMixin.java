package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.piston.MovingPistonBlockEntityHooks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PistonBlockEntity.class})
public abstract class PistonMovingBlockEntityMixin extends BlockEntity implements MovingPistonBlockEntityHooks {
   @Unique
   private NbtCompound movingBlockEntityTag;

   public PistonMovingBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/World;updateNeighbor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
      )},
      method = {"finish"}
   )
   private void elysium$moveBEOnFinalTick(CallbackInfo info) {
      moveBE(this.world, (PistonBlockEntity)(Object)this);
   }

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/World;updateNeighbor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
      )},
      method = {"tick"}
   )
   private static void elysium$moveBEOnTick(World level, BlockPos pos, BlockState state, PistonBlockEntity blockEntity, CallbackInfo info) {
      moveBE(level, blockEntity);
   }

   @Unique
   private static void moveBE(World level, PistonBlockEntity pmbe) {
      BlockPos pos = pmbe.getPos();
      Elysium.LOGGER.trace("Possibly going to move a block entity @ {} with {}", pos, pmbe);
      if (level == null) {
         Elysium.LOGGER.trace("Not moving a block entity, no level!");
      } else {
         BlockState bs = level.getBlockState(pos);
         if (level.isClient()) {
            Elysium.LOGGER.trace("Not moving a block entity, clientside!");
            level.updateListeners(pos, bs, bs, 2);
         } else {
            NbtCompound tag = ((MovingPistonBlockEntityHooks)pmbe).elysium$getMovingBlockEntityTag();
            if (tag == null) {
               Elysium.LOGGER.trace("Not moving a block entity, no tag!");
            } else {
               tag.putInt("x", pos.getX());
               tag.putInt("y", pos.getY());
               tag.putInt("z", pos.getZ());
               BlockEntity bEntity = level.getBlockEntity(pos);
               if (bEntity == null) {
                  Elysium.LOGGER.trace("Not moving a block entity, no BE in level!");
               } else {
                  bEntity.readNbt(tag);
                  ((ServerWorld)level).getChunkManager().markForUpdate(pos);
               }
            }
         }
      }
   }

   @Override
   public void elysium$setMovingBlockEntityTag(NbtCompound tag) {
      Elysium.LOGGER.trace("I ({}) have been given a tag ({})", this, tag);
      this.movingBlockEntityTag = tag;
   }

   @Nullable
   @Override
   public NbtCompound elysium$getMovingBlockEntityTag() {
      return this.movingBlockEntityTag;
   }

   @Inject(
      method = {"writeNbt"},
      at = {@At("TAIL")}
   )
   private void elysium$saveBETag(NbtCompound tag, CallbackInfo ci) {
      if (this.movingBlockEntityTag != null) {
         tag.put("elysium$movingBlockEntityTag", this.movingBlockEntityTag);
      }
   }

   @Inject(
      method = {"readNbt"},
      at = {@At("TAIL")}
   )
   private void elysium$loadBETag(NbtCompound tag, CallbackInfo ci) {
      if (tag.contains("elysium$movingBlockEntityTag")) {
         this.movingBlockEntityTag = tag.getCompound("elysium$movingBlockEntityTag");
      }
   }
}
