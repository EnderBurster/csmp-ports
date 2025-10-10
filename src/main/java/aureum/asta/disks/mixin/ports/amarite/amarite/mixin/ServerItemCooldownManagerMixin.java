package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteMirrorItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({ServerItemCooldownManager.class})
public class ServerItemCooldownManagerMixin {
   @Shadow
   @Final
   private ServerPlayerEntity player;

   @Inject(
      method = {"onCooldownUpdate(Lnet/minecraft/item/Item;)V"},
      at = {@At("TAIL")}
   )
   private void amarite$mirrorRefresh(Item item, CallbackInfo info) {
      if (item == AmariteItems.AMARITE_MIRROR) {
         ItemStack stack = this.player.getOffHandStack();
         if (stack.isOf(AmariteItems.AMARITE_MIRROR)) {
            AmariteMirrorItem.useMirror(this.player.world, this.player, stack, false);
         }
      }
   }
}
