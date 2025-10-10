package aureum.asta.disks.client.render.item;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.ScytheItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScytheDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final List<ModelIdentifier> MODELS_TO_REGISTER = new ArrayList<>();
    //public static final Pair<ModelIdentifier, ModelIdentifier> DEFAULT_MODEL_IDENTIFIER = registerVariantModelPair("");
    public static final Pair<ModelIdentifier, ModelIdentifier> GRACE_MODEL_IDENTIFIER = registerVariantModelPair("scythe");
    //public static final Pair<ModelIdentifier, ModelIdentifier> CARRION_MODEL_IDENTIFIER = registerVariantModelPair("carrion");
    //public static final Pair<ModelIdentifier, ModelIdentifier> GILDED_MODEL_IDENTIFIER = registerVariantModelPair("gilded");
    //public static final Pair<ModelIdentifier, ModelIdentifier> ROZE_MODEL_IDENTIFIER = registerVariantModelPair("roze");

    @NotNull
    private static Pair<ModelIdentifier, ModelIdentifier> registerVariantModelPair(String name) {
        String s = "blood" + (name.isEmpty() ? "" : "_") + name;
        ModelIdentifier inventoryModelIdentifier = new ModelIdentifier(AureumAstaDisks.id(s + "_inventory"), "inventory");
        ModelIdentifier inHandModelIdentifier = new ModelIdentifier(AureumAstaDisks.id(s + "_in_hand"), "inventory");
        MODELS_TO_REGISTER.add(inventoryModelIdentifier);
        MODELS_TO_REGISTER.add(inHandModelIdentifier);

        return new Pair(inventoryModelIdentifier, inHandModelIdentifier);
    }

   /* @NotNull
    private static Pair<ModelIdentifier, ModelIdentifier> getModelIdentifierModelIdentifierPair(ItemStack stack) {
        Pair<ModelIdentifier, ModelIdentifier> modelIdentifierPair = DEFAULT_MODEL_IDENTIFIER;
        WeaponSkinComponent weaponSkinComponent = (WeaponSkinComponent)ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(stack);
        if (weaponSkinComponent != null) {
            if (ScytheItem.Skin.fromString(weaponSkinComponent.getSkinName()) == ScytheItem.Skin.GRACE) {
                modelIdentifierPair = GRACE_MODEL_IDENTIFIER;
            } else if (ScytheItem.Skin.fromString(weaponSkinComponent.getSkinName()) == ScytheItem.Skin.CARRION) {
                modelIdentifierPair = CARRION_MODEL_IDENTIFIER;
            } else if (ScytheItem.Skin.fromString(weaponSkinComponent.getSkinName()) == ScytheItem.Skin.GILDED) {
                modelIdentifierPair = GILDED_MODEL_IDENTIFIER;
            } else if (ScytheItem.Skin.fromString(weaponSkinComponent.getSkinName()) == ScytheItem.Skin.ROZE) {
                modelIdentifierPair = ROZE_MODEL_IDENTIFIER;
            }
        }

        return modelIdentifierPair;
    }*/

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean inHand = mode.isFirstPerson()
                || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
                || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
                || mode == ModelTransformationMode.HEAD
                || mode == ModelTransformationMode.FIXED;
        boolean inInventory = mode == ModelTransformationMode.GUI;
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        Pair<ModelIdentifier, ModelIdentifier> modelIdentifierPair = GRACE_MODEL_IDENTIFIER;
        BakedModel model = MinecraftClient.getInstance()
                .getBakedModelManager()
                .getModel(!inHand ? (ModelIdentifier)modelIdentifierPair.getLeft() : (ModelIdentifier)modelIdentifierPair.getRight());
        if (inInventory) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
        ((VertexConsumerProvider.Immediate)vertexConsumers).draw();
        if (inInventory) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
    }
}
