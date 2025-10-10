package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.fire.ElysiumFireBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({AbstractFireBlock.class})
public class BaseFireBlockMixin {
   @Inject(
      method = {"getState"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/SoulFireBlock;isSoulBase(Lnet/minecraft/block/BlockState;)Z"
      )},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private static void elysium$placeElysiumFire(
      BlockView blockGetter, BlockPos blockPos, CallbackInfoReturnable<BlockState> cir, BlockPos blockPos2, BlockState blockState
   ) {
      if (ElysiumFireBlock.canSurviveOnBlock(blockState)) {
         cir.setReturnValue(Elysium.ELYSIUM_FIRE.getDefaultState());
      }
   }
}
