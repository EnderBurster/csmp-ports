package aureum.asta.disks.recipe;

import aureum.asta.disks.AureumAstaDisks;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.Optional;

public class TrimRecipe implements CraftingRecipe {
    private final Identifier id;
    public final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public TrimRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int bTemplate = 0;
        int bBase = 0;
        int bAddition = 0;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (this.template.test(itemStack)) {
                bTemplate++;
            } else if (this.base.test(itemStack)) {
                bBase++;
            }
            else if (this.addition.test(itemStack)) {
                bAddition++;
            }
            else if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return bTemplate == 1 && bBase == 1 && bAddition == 1;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack base = null;
        ItemStack addition = null;
        ItemStack template = null;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (this.template.test(itemStack)) {
                template = itemStack;
            } else if (this.base.test(itemStack)) {
                base = itemStack;
            } else if (this.addition.test(itemStack)) {
                addition = itemStack;
            }
        }

        if (base != null && addition != null && template != null) {

            Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(registryManager, addition);
            Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional2 = ArmorTrimPatterns.get(registryManager, template);

            if (optional.isPresent() && optional2.isPresent()) {
                Optional<ArmorTrim> optional3 = ArmorTrim.getTrim(registryManager, base);
                if (optional3.isPresent() && ((ArmorTrim)optional3.get()).equals((RegistryEntry)optional2.get(), (RegistryEntry)optional.get())) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack2 = base.copy();
                itemStack2.setCount(1);
                ArmorTrim armorTrim = new ArmorTrim((RegistryEntry)optional.get(), (RegistryEntry)optional2.get());
                if (ArmorTrim.apply(registryManager, itemStack2, armorTrim)) {
                    return itemStack2;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = registryManager.get(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
        if (optional.isPresent()) {
            Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional2 = registryManager.get(RegistryKeys.TRIM_MATERIAL).getEntry(ArmorTrimMaterials.REDSTONE);
            if (optional2.isPresent()) {
                ArmorTrim armorTrim = new ArmorTrim((RegistryEntry)optional2.get(), (RegistryEntry)optional.get());
                ArmorTrim.apply(registryManager, itemStack, armorTrim);
            }
        }

        return itemStack;
    }

    public Identifier getId() {
        return this.id;
    }

    public static class Serializer implements RecipeSerializer<TrimRecipe> {
        public Serializer() {
        }

        public TrimRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "template"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient3 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            return new TrimRecipe(identifier, ingredient, ingredient2, ingredient3);
        }

        public TrimRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
            return new TrimRecipe(identifier, ingredient, ingredient2, ingredient3);
        }

        public void write(PacketByteBuf packetByteBuf, TrimRecipe smithingTrimRecipe) {
            smithingTrimRecipe.template.write(packetByteBuf);
            smithingTrimRecipe.base.write(packetByteBuf);
            smithingTrimRecipe.addition.write(packetByteBuf);
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AureumAstaDisks.TRIM_RECIPE;
    }

    public static class Type implements RecipeType<TrimRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();

        public static final String ID = "trim_recipe";
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.EQUIPMENT;
    }

    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
