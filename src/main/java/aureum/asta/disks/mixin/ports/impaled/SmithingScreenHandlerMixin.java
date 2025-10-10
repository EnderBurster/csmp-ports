package aureum.asta.disks.mixin.ports.impaled;

import aureum.asta.disks.ports.impaled.LoyalTrident;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@SuppressWarnings("all")
@Mixin(LegacySmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow @Final private World world;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "canTakeOutput", at = @At("RETURN"), cancellable = true)
    private void canTakeResult(PlayerEntity playerEntity, boolean resultNonEmpty, CallbackInfoReturnable<Boolean> cir) {
        if (resultNonEmpty && !cir.getReturnValueZ()) {
            ItemStack item = this.input.getStack(0);
            ItemStack upgradeItem = this.input.getStack(1);
            cir.setReturnValue(item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS));
        }
    }

    @ModifyArg(
            method = "updateResult",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemStack;EMPTY:Lnet/minecraft/item/ItemStack;")),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
            )
    )
    private ItemStack updateResult(ItemStack initialResult) {
        if (initialResult.isEmpty()) {
            ItemStack item = this.input.getStack(0);
            ItemStack upgradeItem = this.input.getStack(1);
            if (item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS)) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(item);
                if (enchantments.getOrDefault(Enchantments.LOYALTY, 0) == Enchantments.LOYALTY.getMaxLevel()) {
                    ItemStack result = item.copy();
                    // we can mutate the map as it is recreated with every call to getEnchantments
                    enchantments.put(Enchantments.LOYALTY, Enchantments.LOYALTY.getMaxLevel() + 1);
                    EnchantmentHelper.set(enchantments, result);
                    NbtCompound loyaltyData = result.getOrCreateSubNbt(LoyalTrident.MOD_NBT_KEY);
                    loyaltyData.putUuid(LoyalTrident.TRIDENT_OWNER_NBT_KEY, this.player.getUuid());
                    loyaltyData.putString(LoyalTrident.OWNER_NAME_NBT_KEY, this.player.getEntityName());
                    return result;
                }
            }
        }
        return initialResult;
    }
}