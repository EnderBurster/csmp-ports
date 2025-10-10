package aureum.asta.disks.mixin.client;

import moriyashiine.enchancement.common.init.ModEnchantments;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public class EntityRendererMixin<T extends Entity> {
    public EntityRendererMixin() {
    }

    @Inject(
            method = {"renderLabelIfPresent"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void asta$veil(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Entity var9 = MinecraftClient.getInstance().cameraEntity;
        if (var9 instanceof LivingEntity cameraEntity) {
            if (entity instanceof LivingEntity living) {
                Vec3d vec3d = new Vec3d(cameraEntity.getX(), cameraEntity.getEyeY(), cameraEntity.getZ());
                Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
                boolean canSee = cameraEntity.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, cameraEntity)).getType() == HitResult.Type.MISS;

                if (!living.isGlowing() && EnchancementUtil.hasEnchantment(ModEnchantments.VEIL, living) && !EnchancementUtil.hasEnchantment(ModEnchantments.PERCEPTION, cameraEntity) && !canSee) {
                    ci.cancel();
                }
            }
        }

    }

    /*@Inject(
            method = {"renderLabelIfPresent"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I")},
            cancellable = true)
    private void asta$newVeil(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 2) float h, @Local(ordinal = 1) int i, @Local Matrix4f matrix4f, @Local(ordinal = 2) int j)
    {
        Entity var9 = MinecraftClient.getInstance().cameraEntity;
        if (var9 instanceof LivingEntity cameraEntity) {
            if (entity instanceof LivingEntity living) {
                if (!living.isGlowing() && EnchancementUtil.hasEnchantment(ModEnchantments.VEIL, living) && !EnchancementUtil.hasEnchantment(ModEnchantments.PERCEPTION, cameraEntity)) {
                    textRenderer.draw(text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, j, light);
                    matrices.pop();
                    ci.cancel();
                }
            }
        }
    }*/

    /*@Inject(
            method = {"isSneaky"},
            at = @At("HEAD"),
            cancellable = true
    )
    public void asta$veil(CallbackInfoReturnable<Boolean> cir)
    {
        Entity var9 = MinecraftClient.getInstance().cameraEntity;
        if (var9 instanceof LivingEntity cameraEntity) {
            if ((Object)this instanceof LivingEntity living) {
                if (!living.isGlowing() && EnchancementUtil.hasEnchantment(ModEnchantments.VEIL, living) && !EnchancementUtil.hasEnchantment(ModEnchantments.PERCEPTION, cameraEntity)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }*/
}
