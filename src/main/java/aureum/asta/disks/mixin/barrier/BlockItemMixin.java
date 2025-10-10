package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockItem.class})
public class BlockItemMixin {
    @Inject(
            method = {"place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
                    shift = At.Shift.BEFORE
            )}
    )
    private void asta$queueChanges(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.getWorld().isClient) {
            for (WaterBarrier bar : (context.getWorld().getComponent(AureumAstaDisks.KYRATOS)).barriers) {
                boolean bl = bar.shouldReverseBlockChange(context.getBlockPos()) && !bar.isOwner(context.getPlayer());
                if (bl) {
                    (context.getWorld().getComponent(AureumAstaDisks.KYRATOS)).addBlockChange(new QueuedBlockChange(200, context.getBlockPos(), context.getWorld().getBlockState(context.getBlockPos())));
                    break;
                }
            }
        }
    }
}
