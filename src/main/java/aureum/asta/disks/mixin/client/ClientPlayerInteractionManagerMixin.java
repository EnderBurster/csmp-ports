package aureum.asta.disks.mixin.client;

import aureum.asta.disks.ports.other.ReachEntityAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ClientPlayerInteractionManager.class})
abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyConstant(
            method = {"getReachDistance()F"},
            require = 2,
            allow = 2,
            constant = {@Constant(
                    floatValue = 5.0F
            ), @Constant(
                    floatValue = 4.5F
            )}
    )
    private float getActualReachDistance(final float reachDistance) {
        return this.client.player != null
                ? (float) ReachEntityAttributes.getReachDistance(this.client.player, (double)reachDistance)
                : reachDistance;
    }
}
