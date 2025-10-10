package aureum.asta.disks.mixin.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.mixin.ports.arsenal.ItemRendererAccessor;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ItemRenderer.class})
public class ItemRendererMixin {
    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            argsOnly = true)
    public BakedModel useScytheBig(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (stack.isOf(AstaItems.BLOOD_SCYTHE) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND)
        {
            return ((ItemRendererAccessor) this).asta$getModels().getModelManager().getModel(new ModelIdentifier(AureumAstaDisks.MOD_ID, "blood_scythe_in_hand", "inventory"));
        }
        else if (stack.isOf(AstaItems.DUK_SWORD) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND)
        {
            return ((ItemRendererAccessor) this).asta$getModels().getModelManager().getModel(new ModelIdentifier(AureumAstaDisks.MOD_ID, "duk_sword_in_hand", "inventory"));
        }
        return value;
    }

}
