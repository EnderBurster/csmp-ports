package aureum.asta.disks.mixin.ports.enchancement;

import net.minecraft.item.HoeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HoeItem.class)
public class HoeItemMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static int enchancement$rebalanceEquipmentDamage(int value) {
        return Math.max(0, value) + 1;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static float enchancement$rebalanceEquipmentSpeed(float value) {
        return -2;
    }

}
