package aureum.asta.disks.mixin.ports.elysium.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import aureum.asta.disks.ports.elysium.cheirosiphon.HeatingItem;
import aureum.asta.disks.ports.elysium.cheirosiphon.HeatingItemsComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererMixin {
   @Unique
   ItemStack stack = null;

   @Inject(
           method = "renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
           at = @At("HEAD")
   )
   private void captureStack(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci)
   {
      this.stack = stack;
   }


   @ModifyExpressionValue(
      method = {"renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/ItemCooldownManager;getCooldownProgress(Lnet/minecraft/item/Item;F)F"
      )}
   )
   private float elysium$renderCheirosiphonCooldownOverlay(float original) {
      PlayerEntity player = MinecraftClient.getInstance().player;
      if (stack.getItem() instanceof HeatingItem heatingItem
         && player != null
         && ((HeatingItemsComponent)player.getComponent(HeatingItemsComponent.KEY)).isOverheated(heatingItem)) {
         int heat = ((HeatingItemsComponent)player.getComponent(HeatingItemsComponent.KEY)).getHeat(heatingItem);
         float progress = (float)heat / (float)heatingItem.getMaxHeat();
         return Math.max(original, progress);
      }

      return original;
   }
}
