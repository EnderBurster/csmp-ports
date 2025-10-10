package aureum.asta.disks.mixin.client;

import aureum.asta.disks.util.WeaponSlot;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.mixin.accessor.CreativeSlotAccessor;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CreativeInventoryScreen.class})
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow
    private static ItemGroup selectedTab;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @WrapOperation(
            method = {"setSelectedTab"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z",
                    ordinal = 2
            )}
    )
    private boolean arsenal$moveWeaponSlot(DefaultedList<Slot> slots, Object object, Operation<Boolean> operation) {
        if (object instanceof CreativeInventoryScreen.CreativeSlot newSlot) {
            Slot slot = ((CreativeSlotAccessor)newSlot).getSlot();
            if (slot instanceof WeaponSlot) {
                return (Boolean)operation.call(slots, object);
            }
        }

        return (Boolean)operation.call(slots, object);
    }

    @Inject(
            method = {"drawBackground"},
            at = {@At("TAIL")}
    )
    private void arsenal$drawSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (selectedTab.equals(ItemGroups.INVENTORY))
        {
            int i = this.x + 126;
            int j = this.y + 19;

            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            drawTexture(matrices, i, j, 76, 61, 18, 18);
        }
    }

    /*@Inject(
            method = {"drawBackground"},
            at = {@At("TAIL")}
    )
    private void arsenal$drawSlots(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (selectedTab.equals(Registries.ITEM_GROUP.get(ItemGroups.INVENTORY))) {
            int i = this.x + 126;
            int j = this.y + 19;
            context.drawTexture(HandledScreen.field_2801, i, j, 76, 61, 18, 18);
        }
    }*/
}
