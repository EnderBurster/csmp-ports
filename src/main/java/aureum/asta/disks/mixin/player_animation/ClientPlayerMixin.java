package aureum.asta.disks.mixin.player_animation;

import aureum.asta.disks.ports.charter.CharterClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin {

    private void playTestAnimation(Hand interactionHand, CallbackInfo ci) {
        CharterClient.playTestAnimation();
    }
}