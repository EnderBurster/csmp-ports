package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.ports.elysium.armour.ElysiumUpgradeRecipe;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ForgingScreenHandler.class})
public abstract class ItemCombinerMenuMixin {
   @Shadow
   @Final
   protected CraftingResultInventory output;
   @Shadow
   @Final
   protected Inventory input;

   @Inject(
           method = {"quickMove"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/screen/slot/Slot;onQuickTransfer(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
           )}
   )
   private void elysium$takeExtraIngredientsQuickmove(
           PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir, @Local(ordinal = 0) ItemStack newStack, @Local(ordinal = 1) ItemStack oldStack
   ) {
      if (this.output.getLastRecipe() instanceof ElysiumUpgradeRecipe upgradeRecipe) {
         int i = newStack.getCount() - oldStack.getCount();
         int shrinkAmount = i / upgradeRecipe.getOutput(DynamicRegistryManager.EMPTY).getCount() * upgradeRecipe.getUpgradeItemCount();
         ItemStack toShrink = this.input.getStack(1);
         toShrink.decrement(shrinkAmount);
         this.input.setStack(1, toShrink);
      }
   }
}
