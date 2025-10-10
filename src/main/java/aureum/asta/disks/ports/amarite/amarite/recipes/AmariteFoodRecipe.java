package aureum.asta.disks.ports.amarite.amarite.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class AmariteFoodRecipe extends SpecialCraftingRecipe {
   private static final Ingredient SHARD = Ingredient.ofItems(new ItemConvertible[]{AmariteItems.AMARITE_SHARD});
   private static final Ingredient HONEY = Ingredient.ofItems(new ItemConvertible[]{Items.HONEY_BOTTLE});

   public AmariteFoodRecipe(Identifier identifier, CraftingRecipeCategory category) {
      super(identifier, category);
   }

   public boolean matches(@NotNull CraftingInventory craftingInventory, World world) {
      int count = 0;
      boolean hasFood = false;
      boolean hasModifier = false;

      for (int i = 0; i < craftingInventory.size(); i++) {
         ItemStack itemStack = craftingInventory.getStack(i);
         if (!itemStack.isEmpty()) {
            if (!hasFood && itemStack.isFood()) {
               hasFood = true;
            } else {
               if (hasModifier || !SHARD.test(itemStack) && !HONEY.test(itemStack)) {
                  return false;
               }

               hasModifier = true;
            }

            count++;
         }
      }

      return hasFood && hasModifier && count == 2;
   }

   public ItemStack craft(@NotNull CraftingInventory craftingInventory, DynamicRegistryManager registryManage) {
      ItemStack outputStack = ItemStack.EMPTY;

      for (int i = 0; i < craftingInventory.size(); i++) {
         ItemStack stack = craftingInventory.getStack(i);
         if (!stack.isEmpty() && stack.isFood()) {
            outputStack = stack.copy();
            outputStack.setCount(1);
            i = craftingInventory.size();
         }
      }

      for (int ix = 0; ix < craftingInventory.size(); ix++) {
         ItemStack stack = craftingInventory.getStack(ix);
         if (!stack.isEmpty()) {
            if (SHARD.test(stack)) {
               outputStack.getOrCreateNbt().putBoolean("Budded", true);
               ix = craftingInventory.size();
            }

            if (HONEY.test(stack)) {
               outputStack.getOrCreateNbt().putBoolean("Curative", true);
               ix = craftingInventory.size();
            }
         }
      }

      return outputStack;
   }

   public boolean fits(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_ROCKET;
   }

   public static class AmariteFoodRecipeType implements RecipeType<AmariteFoodRecipe> {
      public static final AmariteFoodRecipe.AmariteFoodRecipeType INSTANCE = new AmariteFoodRecipe.AmariteFoodRecipeType();

      private AmariteFoodRecipeType() {
      }
   }
}
