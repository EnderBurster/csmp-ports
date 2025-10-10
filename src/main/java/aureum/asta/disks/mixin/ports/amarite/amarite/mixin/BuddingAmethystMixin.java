package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.Direction.Axis;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

@Mixin({BuddingAmethystBlock.class})
public class BuddingAmethystMixin {
   @Inject(
      method = {"randomTick"},
      at = {@At("HEAD")}
   )
   private void amarite$buddingAmarite(BlockState state, ServerWorld world, BlockPos pos, @NotNull Random random, CallbackInfo ci) {
      if (random.nextInt(8) == 0) {
         boolean nether = world.getRegistryKey() == World.NETHER;
         if (!nether) {
            if (pos.getY() > 0 || !world.getBlockState(pos.offset(Direction.DOWN)).isOf(Blocks.MAGMA_BLOCK)) {
               return;
            }

            for (Direction direction : Direction.values()) {
               if (direction.getAxis() != Axis.Y) {
                  BlockPos offsetPos = pos.offset(direction);
                  BlockState offsetState = world.getBlockState(offsetPos);
                  if (!offsetState.isOf(Blocks.RAW_GOLD_BLOCK)) {
                     return;
                  }
               }
            }
         } else {
            for (Direction directionx : Direction.values()) {
               if (directionx != Direction.UP) {
                  BlockPos offsetPos = pos.offset(directionx);
                  BlockState offsetState = world.getBlockState(offsetPos);
                  if (!offsetState.isOf(Blocks.RAW_GOLD_BLOCK) && !offsetState.isOf(Blocks.NETHER_GOLD_ORE)) {
                     return;
                  }
               }
            }
         }

         world.setBlockState(pos, AmariteBlocks.BUDDING_AMARITE.getDefaultState());

         for (ServerPlayerEntity entity : world.getPlayers(e -> pos.isWithinDistance(e.getPos(), 64.0)))
         {
            ServerPlayNetworking.send(entity, Amarite.id("budgrow"), PacketByteBufs.create().writeBlockPos(pos));
         }

         world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), AmariteSoundEvents.AMARITE_DECAYS, null, 1.0F, 0.6F, false);
      }
   }

   @Inject(
      method = {"getPistonBehavior"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$nonBreaking(BlockState state, @NotNull CallbackInfoReturnable<PistonBehavior> cir) {
      cir.setReturnValue(PistonBehavior.NORMAL);
   }
}
