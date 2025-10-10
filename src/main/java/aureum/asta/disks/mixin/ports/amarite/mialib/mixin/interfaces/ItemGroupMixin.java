package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.interfaces;

import java.util.function.Supplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MItemGroup;

@Mixin({ItemGroup.class})
public class ItemGroupMixin implements MItemGroup {
   @Unique
   private Supplier<ItemStack> iconSupplier;

   @Inject(
      method = {"getIcon"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mialib$constantIcon(CallbackInfoReturnable<ItemStack> cir) {
      if (this.iconSupplier != null) {
         cir.setReturnValue(this.iconSupplier.get());
      }
   }

   @Override
   public Supplier<ItemStack> mialib$hasConstantIcon() {
      return this.iconSupplier;
   }

   @Override
   public ItemGroup mialib$setConstantIcon(Supplier<ItemStack> constantIcon) {
      this.iconSupplier = constantIcon;
      return (ItemGroup)(Object)this;
   }
}
