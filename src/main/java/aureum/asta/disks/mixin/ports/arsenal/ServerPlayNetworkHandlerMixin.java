package aureum.asta.disks.mixin.ports.arsenal;

import aureum.asta.disks.cca.BackWeaponComponent;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = {"onPlayerAction"},
            at = {@At("HEAD")}
    )
    private void arsenal$swapHands(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (packet.getAction() == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            BackWeaponComponent.setHoldingBackWeapon(this.player, false);
        }
    }
}
