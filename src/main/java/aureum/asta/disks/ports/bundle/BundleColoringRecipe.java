package aureum.asta.disks.ports.bundle;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static aureum.asta.disks.ports.bundle.BundleBackportishItems.*;
import static java.util.Map.entry;

public class BundleColoringRecipe extends SpecialCraftingRecipe {

    public BundleColoringRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    private <T> boolean containsExactly(Iterable<T> collection, Predicate<T> condition, int matchCount) {
        var count = 0;
        for (var item : collection) {
            if (condition.test(item)) {
                count++;
                if (count == matchCount) return true;
            }
        }
        return false;
    }

    private <T> @Nullable T find(Iterable<T> collection, Predicate<T> condition) {
        for (var item : collection) {
            if (condition.test(item)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                items.add(stack.getItem());
            }
        }
        return containsExactly(items, item -> item instanceof BundleItem, 1) &&
                containsExactly(items, COLORS::containsKey, 1);
    }

    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager dynamicRegistryManager) {

        ItemStack bundle = null;
        ItemStack dye = null;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof BundleItem) {
                bundle = stack;
            }
            else if (COLORS.containsKey(stack.getItem()))
            {
                dye = stack;
            }
        }

        if (bundle == null || dye == null) return ItemStack.EMPTY; // This shouldn't happen but just in case
        Item outputBundle = COLORS.get(dye.getItem());
        if (bundle.isOf(outputBundle)) return ItemStack.EMPTY;
        ItemStack resultStack = new ItemStack(outputBundle);
        resultStack.setNbt(bundle.getNbt());
        return resultStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return BundleBackportish.COLORING_RECIPE;
    }

    public static final Map<Item, Item> COLORS = Map.ofEntries(
            entry(Items.WHITE_DYE, WHITE_BUNDLE),
            entry(Items.ORANGE_DYE, ORANGE_BUNDLE),
            entry(Items.MAGENTA_DYE, MAGENTA_BUNDLE),
            entry(Items.LIGHT_BLUE_DYE, LIGHT_BLUE_BUNDLE),
            entry(Items.YELLOW_DYE, YELLOW_BUNDLE),
            entry(Items.LIME_DYE, LIME_BUNDLE),
            entry(Items.PINK_DYE, PINK_BUNDLE),
            entry(Items.GRAY_DYE, GRAY_BUNDLE),
            entry(Items.LIGHT_GRAY_DYE, LIGHT_GRAY_BUNDLE),
            entry(Items.CYAN_DYE, CYAN_BUNDLE),
            entry(Items.PURPLE_DYE, PURPLE_BUNDLE),
            entry(Items.BLUE_DYE, BLUE_BUNDLE),
            entry(Items.BROWN_DYE, BROWN_BUNDLE),
            entry(Items.GREEN_DYE, GREEN_BUNDLE),
            entry(Items.RED_DYE, RED_BUNDLE),
            entry(Items.BLACK_DYE, BLACK_BUNDLE)
    );

}
