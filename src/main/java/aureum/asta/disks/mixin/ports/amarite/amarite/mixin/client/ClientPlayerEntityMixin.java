package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({ClientPlayerEntity.class})
public class ClientPlayerEntityMixin {
   @WrapOperation(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
      )}
   )
   private boolean amarite$noSwordSlowdown(ClientPlayerEntity player, @NotNull Operation<Boolean> original) {
      return (Boolean)original.call(player) && !player.getActiveItem().isOf(AmariteItems.AMARITE_LONGSWORD);
   }
}
