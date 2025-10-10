package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.component.DiamondOfProtection;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockItem.class})
public class BlockItemMixin {
   @Inject(
      method = {"place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
         shift = Shift.BEFORE
      )}
   )
   private void yea(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
      if (!context.getWorld().isClient) {
         for (DiamondOfProtection dia : ((CharterWorldComponent)context.getWorld().getComponent(CharterComponents.CHARTER)).diamonds) {
            boolean bl = dia.isPosInside(context.getBlockPos()) && !dia.isOwner(context.getPlayer());
            if (bl) {
               ((CharterWorldComponent)context.getWorld().getComponent(CharterComponents.CHARTER))
                  .addBlockChange(new QueuedBlockChange(200, context.getBlockPos(), context.getWorld().getBlockState(context.getBlockPos())));
               break;
            }
         }
      }
   }
}
