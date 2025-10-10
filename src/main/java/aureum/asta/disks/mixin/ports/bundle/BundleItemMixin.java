package aureum.asta.disks.mixin.ports.bundle;

import aureum.asta.disks.ports.bundle.BundleSelection;
import aureum.asta.disks.ports.bundle.SelectionBundleTooltipData;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
    @Unique
    private static int bundleScroll$itemCount(ItemStack itemStack) {
        var nbt = itemStack.getNbt();
        if (nbt == null) return 0;
        return nbt.getList("Items", NbtElement.COMPOUND_TYPE).size();
    }


    @Redirect(
            method = "getTooltipData",
            at = @At(value = "NEW", target = "(Lnet/minecraft/util/collection/DefaultedList;I)Lnet/minecraft/client/item/BundleTooltipData;")
    )
    private BundleTooltipData setDataSelected(DefaultedList<ItemStack> inventory, int bundleOccupancy, @Local(argsOnly = true) ItemStack itemStack) {
        return new SelectionBundleTooltipData(inventory, bundleOccupancy, BundleSelection.get(itemStack));
    }

    @ModifyConstant(
            method = "removeFirstStack",
            constant = @Constant(intValue = 0)
    )
    private static int removeSelected(int constant, @Local(argsOnly = true) ItemStack itemStack) {
        return max(min(BundleSelection.get(itemStack), bundleScroll$itemCount(itemStack) - 1), 0);
    }

    @ModifyArg(
            method = "addToBundle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;add(ILnet/minecraft/nbt/NbtElement;)V"),
            index = 0
    )
    private static int addAtSelected(int i, @Local(argsOnly = true, ordinal = 0) ItemStack itemStack) {
        return max(min(BundleSelection.get(itemStack), bundleScroll$itemCount(itemStack) - 1), 0);
    }

    @Inject(
            method = "removeFirstStack",
            at = @At("TAIL")
    )
    private static void fixAfterRemove(ItemStack itemStack, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        var items = bundleScroll$itemCount(itemStack);
        if (items == 0)
            BundleSelection.clear(itemStack);
        else if (BundleSelection.get(itemStack) >= items)
            BundleSelection.set(itemStack, items - 1);
    }

    @Inject(
            method = "dropAllBundledItems",
            at = @At("TAIL")
    )
    private static void fixAfterDropAll(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BundleSelection.clear(stack);
    }

    @Redirect(
            method = "canMergeStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private static boolean checkAnyBundleForMerge(ItemStack instance, Item item) {
        return instance.getItem() instanceof BundleItem;
    }

    @Redirect(
            method = "getItemOccupancy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0)
    )
    private static boolean checkAnyBundleForOccupancy(ItemStack instance, Item item) {
        return instance.getItem() instanceof BundleItem;
    }
}
