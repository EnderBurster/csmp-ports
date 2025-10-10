package aureum.asta.disks.mixin.client;

import aureum.asta.disks.cca.BackWeaponComponent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin({InGameHud.class})
public abstract class InGameHudMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    private ItemStack currentStack;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    protected abstract void renderHotbarItem(MatrixStack var1, int var2, int var3, float var4, PlayerEntity var5, ItemStack var6, int var7);

    @Shadow @Final private static Identifier WIDGETS_TEXTURE;

    @Inject(
            method = {"renderHotbar"},
            at = {@At("TAIL")}
    )
    private void arsenal$renderWeaponSlot(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        if (player != null) {
            ItemStack stack = BackWeaponComponent.getBackWeapon(player);
            if (!stack.isEmpty()) {
                RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
                int i = this.scaledWidth / 2;
                if (BackWeaponComponent.isHoldingBackWeapon(player)) {
                    drawTexture(matrices, i - 12, this.scaledHeight - 23 - 70, 0, 22, 24, 24);
                    RenderSystem.enableBlend();
                    drawTexture(matrices, i - 12 + 4, this.scaledHeight - 23 - 70 + 4, 27, 26, 16, 16);
                    RenderSystem.defaultBlendFunc();
                    int o = i - 90 + 80 + 2;
                    int p = this.scaledHeight - 19 - 70;
                    this.renderHotbarItem(matrices, o, p, tickDelta, player, stack, 1);
                    RenderSystem.disableBlend();
                }
                else
                {
                    Arm arm = player.getMainArm().getOpposite();
                    if (arm == Arm.RIGHT) {
                        drawTexture(matrices, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
                    } else {
                        drawTexture(matrices, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
                    }

                    int n = this.scaledHeight - 16 - 3;
                    if (arm == Arm.RIGHT) {
                        this.renderHotbarItem(matrices, i - 91 - 26, n, tickDelta, player, stack, 0);
                    } else {
                        this.renderHotbarItem(matrices, i + 91 + 10, n, tickDelta, player, stack, 0);
                    }

                    RenderSystem.disableBlend();
                }
            }
        }
    }

    @WrapOperation(
            method = {"renderHotbar"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 1
            )}
    )
    private void arsenal$selection(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if (this.getCameraPlayer() == null || !BackWeaponComponent.isHoldingBackWeapon(this.getCameraPlayer())) {
            original.call(matrixStack, x, y, u, v, width, height);
        }
    }
}
