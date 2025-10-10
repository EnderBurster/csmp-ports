package aureum.asta.disks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Item.Settings.class)
public class SettingsMixin {

    @Shadow
    FeatureSet requiredFeatures;

    @Inject(method = "requires", at = @At(value = "RETURN"))
    private void requires(FeatureFlag[] features, CallbackInfoReturnable<Item.Settings> cir)
    {
        FeatureFlag[] filteredFeatures = Arrays.stream(features)
                .filter(flag -> flag != FeatureFlags.UPDATE_1_20)
                .toArray(FeatureFlag[]::new);
        this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(filteredFeatures);
    }
}
