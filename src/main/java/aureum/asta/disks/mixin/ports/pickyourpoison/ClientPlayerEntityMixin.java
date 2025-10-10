package aureum.asta.disks.mixin.ports.pickyourpoison;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import aureum.asta.disks.ports.pickyourpoison.PickYourPoison;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyExpressionValue(method = "canSprint", at = @At(value = "CONSTANT", args = "floatValue=6.0"))
    private float replaceMinFoodLevelForSprinting(float foodLevel) {
        if (this.hasStatusEffect(PickYourPoison.STIMULATION)) {
            return -1.0f;
        }
        return foodLevel;
    }
}
