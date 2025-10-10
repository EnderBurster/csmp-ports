package aureum.asta.disks.ports.elysium.armour;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElysiumUpgradeRecipe extends LegacySmithingRecipe {
   final int upgradeItemCount;

   public ElysiumUpgradeRecipe(Identifier resourceLocation, Ingredient ingredient, Ingredient ingredient2, ItemStack itemStack, int upgradeItemCount) {
      super(resourceLocation, ingredient, ingredient2, itemStack);
      this.upgradeItemCount = upgradeItemCount;
   }

   public int getUpgradeItemCount() {
      return this.upgradeItemCount;
   }

   public boolean matches(Inventory inventory, World world) {
      return super.matches(inventory, world) && inventory.getStack(1).getCount() >= this.upgradeItemCount;
   }

   public static class Builder {
      private final Ingredient base;
      private final Ingredient addition;
      private final Item result;
      private final int additionAmount;
      private final net.minecraft.advancement.Advancement.Builder advancement = net.minecraft.advancement.Advancement.Builder.create();
      private final RecipeSerializer<?> type;

      public Builder(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, Ingredient ingredient2, Item item, int additionAmount) {
         this.type = recipeSerializer;
         this.base = ingredient;
         this.addition = ingredient2;
         this.result = item;
         this.additionAmount = additionAmount;
      }

      public static Builder create(Ingredient base, Ingredient addition, Item result, int additionAmount) {
         return new Builder(ElysiumArmour.ELYSIUM_UPGRADE_RECIPE_SERIALIZER, base, addition, result, additionAmount);
      }

      public Builder criterion(String name, CriterionConditions criterion) {
         this.advancement.criterion(name, criterion);
         return this;
      }

      public void build(Consumer<RecipeJsonProvider> finishedRecipeConsumer, Identifier id) {
         this.ensureValid(id);
         this.advancement
            .parent(CraftingRecipeJsonBuilder.ROOT)
            .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
            .rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(id))
            .criteriaMerger(CriterionMerger.OR);
         finishedRecipeConsumer.accept(
            new Result(
               id,
               this.type,
               this.base,
               this.addition,
               this.result,
               this.additionAmount,
               this.advancement,
               new Identifier(id.getNamespace(), "recipes/" + getFirstItemGroup(this.result).getDisplayName() + "/" + id.getPath())
            )
         );
      }

      public static ItemGroup getFirstItemGroup(Item item) {
         for (ItemGroup group : ItemGroups.getGroups()) {
            Collection<ItemStack> stacks = group.getDisplayStacks(); // <- THIS replaces appendStacks
            for (ItemStack stack : stacks) {
               if (stack.getItem() == item) {
                  return group;
               }
            }
         }
         return null;
      }

      private void ensureValid(Identifier id) {
         if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
         }
      }

      public static class Result implements RecipeJsonProvider {
         private final Identifier id;
         private final Ingredient base;
         private final Ingredient addition;
         private final Item result;
         private final int additionAmount;
         private final net.minecraft.advancement.Advancement.Builder advancement;
         private final Identifier advancementId;
         private final RecipeSerializer<?> type;

         public Result(
            Identifier resourceLocation,
            RecipeSerializer<?> recipeSerializer,
            Ingredient ingredient,
            Ingredient ingredient2,
            Item item,
            int additionAmount,
            net.minecraft.advancement.Advancement.Builder builder,
            Identifier resourceLocation2
         ) {
            this.id = resourceLocation;
            this.type = recipeSerializer;
            this.base = ingredient;
            this.addition = ingredient2;
            this.result = item;
            this.additionAmount = additionAmount;
            this.advancement = builder;
            this.advancementId = resourceLocation2;
         }

         public void serialize(JsonObject json) {
            json.add("base", this.base.toJson());
            json.add("addition", this.addition.toJson());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registries.ITEM.getId(this.result).toString());
            json.add("result", jsonObject);
            json.addProperty("addition_amount", this.additionAmount);
         }

         public Identifier getRecipeId() {
            return this.id;
         }

         public RecipeSerializer<?> getSerializer() {
            return this.type;
         }

         @Nullable
         public JsonObject toAdvancementJson() {
            return this.advancement.toJson();
         }

         @Nullable
         public Identifier getAdvancementId() {
            return this.advancementId;
         }
      }
   }

   public static class Serializer implements RecipeSerializer<ElysiumUpgradeRecipe> {
      private final LegacySmithingRecipe.Serializer superSerializer = new LegacySmithingRecipe.Serializer();

      public ElysiumUpgradeRecipe read(Identifier recipeId, JsonObject json) {
         Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
         Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
         ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
         int additionAmount = JsonHelper.getInt(json, "addition_amount", 1);
         return new ElysiumUpgradeRecipe(recipeId, ingredient, ingredient2, itemStack, additionAmount);
      }

      public ElysiumUpgradeRecipe read(Identifier recipeId, PacketByteBuf buffer) {
         Ingredient ingredient = Ingredient.fromPacket(buffer);
         Ingredient ingredient2 = Ingredient.fromPacket(buffer);
         ItemStack itemStack = buffer.readItemStack();
         int additionAmount = buffer.readVarInt();
         return new ElysiumUpgradeRecipe(recipeId, ingredient, ingredient2, itemStack, additionAmount);
      }

      public void write(PacketByteBuf buffer, ElysiumUpgradeRecipe recipe) {
         this.superSerializer.write(buffer, recipe);
         buffer.writeVarInt(recipe.upgradeItemCount);
      }
   }
}
