package aureum.asta.disks.ports.bundle;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import static java.lang.Math.*;

public class BundleSelection {
    public static final String SELECTED_NBT = "Selected";

    /*private static int getContentsSize(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT).size();
    }

    private static int clampToContents(ItemStack itemStack, int index) {
        return clamp(index, 0, max(getContentsSize(itemStack) - 1, 0));
    }*/

    public static int get(ItemStack itemStack) {
        NbtCompound nbtCompound = itemStack.getNbt();
        if (nbtCompound == null) return 0;
        return nbtCompound.getInt(SELECTED_NBT);
    }

    public static void set(ItemStack itemStack, int selected) {
        itemStack.getOrCreateNbt().putInt(BundleSelection.SELECTED_NBT, selected);
    }

    public static void clear(ItemStack itemStack) {
        itemStack.removeSubNbt(BundleSelection.SELECTED_NBT);
    }

    public static void add(ItemStack itemStack, int amount) {
        NbtCompound nbt = itemStack.getNbt();
        var itemCount = nbt != null ? max(nbt.getList("Items", NbtElement.COMPOUND_TYPE).size(), 1) : 1;
        set(itemStack, floorMod(get(itemStack) + amount, itemCount));
    }
}
