package aureum.asta.disks.mixin.ports.elysium.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.client.cheirosiphon.ServerboundAirblastPacketClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {MinecraftClient.class},
        priority = 999)
public class MinecraftMixin {
   @Shadow
   @Nullable
   public ClientPlayerEntity player;

   @Inject(
      method = {"doAttack()Z"},
      at = {@At(value = "HEAD")},
      cancellable = true
   )
   private void elysium$useCheirosiphonLeftClickAction(CallbackInfoReturnable<Boolean> cir) {
      if (this.player != null && this.player.getMainHandStack().isOf(Elysium.CHEIROSIPHON)) {
         ServerboundAirblastPacketClient.sendToServer();
         cir.setReturnValue(true);
      }
   }
}
