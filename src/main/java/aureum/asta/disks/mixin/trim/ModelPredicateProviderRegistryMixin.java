package aureum.asta.disks.mixin.trim;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/item/ModelPredicateProviderRegistry;register(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/item/ClampedModelPredicateProvider;)Lnet/minecraft/client/item/ClampedModelPredicateProvider;",
                    ordinal = 2
            ),
            index = 1
    )
    private static ClampedModelPredicateProvider modifyTrimPredicate(ClampedModelPredicateProvider original) {
        return (stack, world, entity, seed) -> {
            if (!stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
                return Float.NEGATIVE_INFINITY;
            } else if (world == null) {
                return 0.0F;
            } else {
                // SKIP the feature flag check
                return ArmorTrim.getTrim(world.getRegistryManager(), stack)
                        .map(ArmorTrim::getMaterial)
                        .map(RegistryEntry::value)
                        .map(ArmorTrimMaterial::itemModelIndex)
                        .orElse(0.0F);
            }
        };
    }

}
