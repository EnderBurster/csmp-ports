package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.ports.elysium.armour.ElysiumUpgradeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SmithingScreenHandler.class})
public abstract class SmithingMenuMixin extends ForgingScreenHandler {
   public SmithingMenuMixin(@Nullable ScreenHandlerType<?> menuType, int i, PlayerInventory inventory, ScreenHandlerContext containerLevelAccess) {
      super(menuType, i, inventory, containerLevelAccess);
   }

   @Inject(
      method = {"onTakeOutput"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/inventory/CraftingResultInventory;unlockLastRecipe(Lnet/minecraft/entity/player/PlayerEntity;)V"
      )}
   )
   private void elysium$takeExtraIngredients(PlayerEntity player, ItemStack itemStack, CallbackInfo ci) {
      if (this.output.getLastRecipe() instanceof ElysiumUpgradeRecipe upgradeRecipe) {
         int shrinkAmount = itemStack.getCount() / upgradeRecipe.getOutput(DynamicRegistryManager.EMPTY).getCount() * upgradeRecipe.getUpgradeItemCount() - 1;
         ItemStack toShrink = this.input.getStack(1);
         toShrink.decrement(shrinkAmount);
         this.input.setStack(1, toShrink);
      }
   }
}
