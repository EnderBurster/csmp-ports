package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.item.HoarderMawItem;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerInventory.class})
public abstract class PlayerInventoryMixin implements Inventory {
   @Unique
   private boolean charter$bl = false;

   @Inject(
      method = {"dropAll"},
      at = {@At("HEAD")}
   )
   private void charter$dropAllPre(CallbackInfo ci) {
      this.charter$bl = this.containsAny(itemStack -> itemStack.getItem().equals(CharterItems.HOARDER_MAW));
   }

   @WrapWithCondition(
      method = {"dropAll"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"
      )}
   )
   private boolean charter$dropAll(PlayerEntity player, ItemStack stack, boolean a, boolean b) {
      if (stack.getItem() instanceof HoarderMawItem && stack.getOrCreateNbt().contains("item_nm")) {
         stack.getOrCreateNbt().remove("item_nm");
         stack.getOrCreateNbt().remove("item_path");
         stack.getOrCreateNbt().remove("held_item_count");
      }

      return !this.charter$bl || stack.getItem() instanceof HoarderMawItem;
   }
}
