package aureum.asta.disks.mixin.ports.bundle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FeatureFlags.class)
public class FeatureFlagsMixin {
    @Shadow @Final public static FeatureFlag BUNDLE;

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/resource/featuretoggle/FeatureFlags;VANILLA_FEATURES:Lnet/minecraft/resource/featuretoggle/FeatureSet;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private static FeatureSet addBundles(FeatureSet original) {
        return original.combine(FeatureSet.of(BUNDLE));
    }
}
