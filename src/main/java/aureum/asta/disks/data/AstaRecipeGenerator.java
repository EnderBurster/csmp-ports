package aureum.asta.disks.data;

import aureum.asta.disks.init.AstaBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class AstaRecipeGenerator extends FabricRecipeProvider {
    public AstaRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        generateBlockSet(consumer, Items.SANDSTONE, Items.ORANGE_DYE, AstaBlocks.KYRATOS, AstaBlocks.KYRATOS_BRICK_STAIRS, AstaBlocks.KYRATOS_BRICK_WALL, AstaBlocks.KYRATOS_BRICK_SLAB, AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE, AstaBlocks.CHISELED_KYRATOS_BUTTON);
        generateBlockSet(consumer, Items.SANDSTONE, Items.WHITE_DYE, AstaBlocks.WHITE_SANDSTONE, AstaBlocks.WHITE_SANDSTONE_STAIRS, AstaBlocks.WHITE_SANDSTONE_WALL, AstaBlocks.WHITE_SANDSTONE_SLAB, AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.WHITE_SANDSTONE_BUTTON);
        generateBlockSet(consumer, Items.SANDSTONE, Items.RED_DYE, AstaBlocks.RED_SANDSTONE, AstaBlocks.RED_SANDSTONE_STAIRS, AstaBlocks.RED_SANDSTONE_WALL, AstaBlocks.RED_SANDSTONE_SLAB, AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE, AstaBlocks.RED_SANDSTONE_BUTTON);
        generateBlockSet(consumer, Items.SANDSTONE, Items.PINK_DYE, AstaBlocks.PINK_SANDSTONE, AstaBlocks.PINK_SANDSTONE_STAIRS, AstaBlocks.PINK_SANDSTONE_WALL, AstaBlocks.PINK_SANDSTONE_SLAB, AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE, AstaBlocks.PINK_SANDSTONE_BUTTON);
        generateBlockSet(consumer, Items.SANDSTONE, Items.YELLOW_DYE, AstaBlocks.YELLOW_SANDSTONE, AstaBlocks.YELLOW_SANDSTONE_STAIRS, AstaBlocks.YELLOW_SANDSTONE_WALL, AstaBlocks.YELLOW_SANDSTONE_SLAB, AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE, AstaBlocks.YELLOW_SANDSTONE_BUTTON);
        generateBlockSet(consumer, Items.SANDSTONE, Items.BLUE_DYE, AstaBlocks.BLUE_SANDSTONE, AstaBlocks.BLUE_SANDSTONE_STAIRS, AstaBlocks.BLUE_SANDSTONE_WALL, AstaBlocks.BLUE_SANDSTONE_SLAB, AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.BLUE_SANDSTONE_BUTTON);
        generateBlockSet(consumer, Items.STONE_BRICKS, Items.CYAN_DYE, AstaBlocks.BLUE_BRICK, AstaBlocks.BLUE_BRICK_STAIRS, AstaBlocks.BLUE_BRICK_WALL, AstaBlocks.BLUE_BRICK_SLAB, AstaBlocks.BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.BLUE_BRICK_BUTTON);
        generateBlockSet(consumer, Items.STONE_BRICKS, Items.BLUE_DYE, AstaBlocks.DARK_BLUE_BRICK, AstaBlocks.DARK_BLUE_BRICK_STAIRS, AstaBlocks.DARK_BLUE_BRICK_WALL, AstaBlocks.DARK_BLUE_BRICK_SLAB, AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.DARK_BLUE_BRICK_BUTTON);
        generateBlockSet(consumer, Items.STONE_BRICKS, Items.PURPLE_DYE, AstaBlocks.PURPLE_BRICK, AstaBlocks.PURPLE_BRICK_STAIRS, AstaBlocks.PURPLE_BRICK_WALL, AstaBlocks.PURPLE_BRICK_SLAB, AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE, AstaBlocks.PURPLE_BRICK_BUTTON);
        generateBlockSet(consumer, Items.STONE_BRICKS, Items.WHITE_DYE, AstaBlocks.WHITE_BRICK, AstaBlocks.WHITE_BRICK_STAIRS, AstaBlocks.WHITE_BRICK_WALL, AstaBlocks.WHITE_BRICK_SLAB, AstaBlocks.WHITE_BRICK_PRESSURE_PLATE, AstaBlocks.WHITE_BRICK_BUTTON);

        List<ItemConvertible> inputs = List.of(Items.SANDSTONE);
        offerSmelting(consumer, inputs, RecipeCategory.MISC, AstaBlocks.KYRATOS_GLASS, 0.1F, 200, null);
        offerBlasting(consumer, inputs, RecipeCategory.MISC, AstaBlocks.KYRATOS_GLASS, 0.1F, 100, null);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AstaBlocks.KYRATOS_GLASS_PANE)
                .pattern("   ")
                .pattern("GGG")
                .pattern("GGG")
                .input('G', AstaBlocks.KYRATOS_GLASS)
                .criterion(FabricRecipeProvider.hasItem(AstaBlocks.KYRATOS_GLASS), FabricRecipeProvider.conditionsFromItem(AstaBlocks.KYRATOS_GLASS))
                .offerTo(consumer, new Identifier(FabricRecipeProvider.getRecipeName(AstaBlocks.KYRATOS_GLASS_PANE)));

        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, AstaBlocks.KYRATOS_PILLAR, AstaBlocks.WHITE_SANDSTONE);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, AstaBlocks.PROTECTION_RUNE, AstaBlocks.WHITE_SANDSTONE);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, AstaBlocks.CREATION_RUNE, AstaBlocks.WHITE_SANDSTONE);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AstaBlocks.KYRATOS_DOOR)
                .pattern("GG ")
                .pattern("GG ")
                .pattern("GG ")
                .input('G', AstaBlocks.KYRATOS)
                .criterion(FabricRecipeProvider.hasItem(AstaBlocks.KYRATOS), FabricRecipeProvider.conditionsFromItem(AstaBlocks.KYRATOS))
                .offerTo(consumer, new Identifier(FabricRecipeProvider.getRecipeName(AstaBlocks.KYRATOS_DOOR)));
    }

    private void generateBlockSet(Consumer<RecipeJsonProvider> consumer, Item unlock, Item dye , Block block, Block stair, Block wall, Block slab, Block pressurePlate, Block button)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, block).input(unlock).input(dye).criterion(FabricRecipeProvider.hasItem(unlock), FabricRecipeProvider.conditionsFromItem(unlock)).criterion(FabricRecipeProvider.hasItem(dye), FabricRecipeProvider.conditionsFromItem(dye)).offerTo(consumer, new Identifier(FabricRecipeProvider.getRecipeName(block)));
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, stair, block);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, wall, block);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, slab, block, 2);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, pressurePlate, block);
        offerStonecuttingRecipe(consumer, RecipeCategory.MISC, button, block);
    }
}
