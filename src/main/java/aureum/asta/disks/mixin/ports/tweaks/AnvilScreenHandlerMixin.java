package aureum.asta.disks.mixin.ports.tweaks;

import aureum.asta.disks.ports.impaled.LoyalTrident;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Unique
    private boolean impaled$checkingRiptideCompat;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Redirect(
            method = {"updateResult"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
                    ordinal = 1
            )
    )
    private boolean updateResult(ItemStack stack, Item item) {
        if (stack.isOf(item)) {
            return true;
        } else {
            if (stack.getItem() instanceof ArmorItem armorItem
                    && armorItem.getMaterial() == ArmorMaterials.NETHERITE
                    && item instanceof ArmorItem repairItem
                    && repairItem.getMaterial() == ArmorMaterials.DIAMOND) {
                return armorItem.getSlotType() == repairItem.getSlotType();
            }

            if (stack.getItem() instanceof ToolItem toolItem
                    && toolItem.getMaterial() == ToolMaterials.NETHERITE
                    && item instanceof ToolItem repairItem
                    && repairItem.getMaterial() == ToolMaterials.DIAMOND) {
                return toolItem instanceof PickaxeItem && repairItem instanceof PickaxeItem
                        || toolItem instanceof ShovelItem && repairItem instanceof ShovelItem
                        || toolItem instanceof AxeItem && repairItem instanceof AxeItem
                        || toolItem instanceof SwordItem && repairItem instanceof SwordItem
                        || toolItem instanceof HoeItem && repairItem instanceof HoeItem;
            }

            return false;
        }
    }
}
