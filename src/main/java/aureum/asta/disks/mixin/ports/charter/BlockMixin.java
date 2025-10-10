package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.block.CharterStoneBlock;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.component.DiamondOfProtection;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Block.class})
public class BlockMixin {
   @WrapWithCondition(
      method = {"afterBreak"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"
      )}
   )
   private boolean charter$cancelBlockDrop(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity player, ItemStack stack) {
      boolean bl = false;

      for (DiamondOfProtection dia : ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER)).diamonds) {
         boolean var10000;
         label29: {
            if (dia.isPosInside(pos)
               && player instanceof PlayerEntity pl
               && !dia.isOwner(pl)
               && !(world.getBlockState(pos).getBlock() instanceof CharterStoneBlock)) {
               var10000 = true;
               break label29;
            }

            var10000 = false;
         }

         bl = var10000;
         if (bl) {
            break;
         }
      }

      return !bl;
   }
}
