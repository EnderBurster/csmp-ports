package aureum.asta.disks.mixin.ports.elysium.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import aureum.asta.disks.ports.elysium.Elysium;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ClientPlayerEntity.class})
public class LocalPlayerMixin {
   @WrapOperation(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
      )}
   )
   private boolean elysium$noCheirosiphonSlowdown(ClientPlayerEntity player, Operation<Boolean> original) {
      return (Boolean)original.call(player) && !player.getActiveItem().isOf(Elysium.CHEIROSIPHON);
   }
}
