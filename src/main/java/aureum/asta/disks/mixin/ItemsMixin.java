package aureum.asta.disks.mixin;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public class ItemsMixin {
    @Redirect(
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {
                                    "stringValue=cherry_boat"
                            },
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "NEW",
                    target = "(ZLnet/minecraft/entity/vehicle/BoatEntity$Type;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/BoatItem;",
                    ordinal = 0
            ),
            method = "<clinit>")
    private static BoatItem asta$boat(boolean chest, BoatEntity.Type type, Item.Settings settings) {
        return new BoatItem(false, net.minecraft.entity.vehicle.BoatEntity.Type.CHERRY, (new Item.Settings()).maxCount(1).requires(new FeatureFlag[]{FeatureFlags.VANILLA}));
    }
}
