package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.text.Text;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;

@Mixin({EntityRenderer.class})
public class EntityMixin<T extends Entity> {
   @Inject(
      method = {"renderLabelIfPresent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$hideNames(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      if (entity instanceof LivingEntity living) {
         ItemStack mask = MaskItem.getWornMask(living);
         if (EnchantmentHelper.getLevel(AmariteEnchantments.CONCEALMENT, mask) > 0) {
            ci.cancel();
         }
      }
   }
}
