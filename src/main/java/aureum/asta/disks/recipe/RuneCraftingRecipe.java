package aureum.asta.disks.recipe;

import aureum.asta.disks.AureumAstaDisks;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RuneCraftingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final Ingredient runeItem;  // Ingredient for center item
    private final List<Ingredient> pedestalItems; // 4 items
    private final ItemStack output;

    public RuneCraftingRecipe(Identifier id, Ingredient runeItem, List<Ingredient> pedestalItems, ItemStack output) {
        this.id = id;
        this.runeItem = runeItem;
        this.pedestalItems = pedestalItems;
        this.output = output;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        // you won’t use a normal Inventory, you’ll manually check in your block entity
        return false;
    }

    public boolean matches(ItemStack rune, List<ItemStack> pedestals) {
        if (!runeItem.test(rune)) return false;
        if (pedestals.size() != pedestalItems.size()) return false;

        // Make a mutable list of ingredients to match against
        List<Ingredient> remaining = new ArrayList<>(pedestalItems);

        for (ItemStack stack : pedestals) {
            boolean matched = false;

            Iterator<Ingredient> it = remaining.iterator();
            while (it.hasNext()) {
                Ingredient ing = it.next();
                if (ing.test(stack)) {
                    it.remove();
                    matched = true;
                    break;
                }
            }

            if (!matched) return false; // stack didn’t match anything
        }

        return remaining.isEmpty();
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AureumAstaDisks.RUNE_RECIPE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<RuneCraftingRecipe> {
        public Serializer() {
        }

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RuneCraftingRecipe read(Identifier id, JsonObject json) {
            Ingredient runeItem = Ingredient.fromJson(json.get("rune"));

            JsonArray pedestalArray = json.getAsJsonArray("pedestals");
            List<Ingredient> pedestals = new ArrayList<>();
            for (JsonElement e : pedestalArray) {
                pedestals.add(Ingredient.fromJson(e));
            }

            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            return new RuneCraftingRecipe(id, runeItem, pedestals, output);
        }

        @Override
        public RuneCraftingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient runeItem = Ingredient.fromPacket(buf);

            int size = buf.readVarInt();
            List<Ingredient> pedestals = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                pedestals.add(Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new RuneCraftingRecipe(id, runeItem, pedestals, output);
        }

        @Override
        public void write(PacketByteBuf buf, RuneCraftingRecipe recipe) {
            recipe.runeItem.write(buf);
            buf.writeVarInt(recipe.pedestalItems.size());
            for (Ingredient ing : recipe.pedestalItems) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.output);
        }
    }

    public static class Type implements RecipeType<RuneCraftingRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "rune_crafting";
    }

    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}