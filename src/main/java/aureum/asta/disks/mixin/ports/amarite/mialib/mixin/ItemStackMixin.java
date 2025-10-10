package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class ItemStackMixin {
   @Shadow
   public abstract Item getItem();

   @Inject(
      method = {"getName"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void getName(CallbackInfoReturnable<Text> cir) {
      int color = this.getItem().mialib$getNameColor((ItemStack)(Object)this);
      if (color != -1) {
         Text text = (Text)cir.getReturnValue();
         if (text.getStyle().getColor() == null) {
            cir.setReturnValue(text.mialib$withColor(color));
         }
      }
   }
}
