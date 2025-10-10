package aureum.asta.disks.data;

import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.AstaItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import static net.minecraft.data.client.BlockStateModelGenerator.*;

public class AstaModelProvider extends FabricModelProvider {

    public AstaModelProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //Pillars
        blockStateModelGenerator.registerAxisRotated(AstaBlocks.KYRATOS_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(AstaBlocks.PROTECTION_RUNE, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(AstaBlocks.CREATION_RUNE, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);

        //Glass
        blockStateModelGenerator.registerGlassPane(AstaBlocks.KYRATOS_GLASS, AstaBlocks.KYRATOS_GLASS_PANE);

        //Block Groups
        createBlockgroup(blockStateModelGenerator, "kyratos", AstaBlocks.KYRATOS, AstaBlocks.KYRATOS_BRICK_STAIRS, AstaBlocks.KYRATOS_BRICK_WALL, AstaBlocks.KYRATOS_BRICK_SLAB, AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE, AstaBlocks.CHISELED_KYRATOS_BUTTON);
        createBlockgroup(blockStateModelGenerator, "white_sandstone", AstaBlocks.WHITE_SANDSTONE, AstaBlocks.WHITE_SANDSTONE_STAIRS, AstaBlocks.WHITE_SANDSTONE_WALL, AstaBlocks.WHITE_SANDSTONE_SLAB, AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.WHITE_SANDSTONE_BUTTON);
        createBlockgroup(blockStateModelGenerator, "red_sandstone", AstaBlocks.RED_SANDSTONE, AstaBlocks.RED_SANDSTONE_STAIRS, AstaBlocks.RED_SANDSTONE_WALL, AstaBlocks.RED_SANDSTONE_SLAB, AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE, AstaBlocks.RED_SANDSTONE_BUTTON);
        createBlockgroup(blockStateModelGenerator, "pink_sandstone", AstaBlocks.PINK_SANDSTONE, AstaBlocks.PINK_SANDSTONE_STAIRS, AstaBlocks.PINK_SANDSTONE_WALL, AstaBlocks.PINK_SANDSTONE_SLAB, AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE, AstaBlocks.PINK_SANDSTONE_BUTTON);
        createBlockgroup(blockStateModelGenerator, "yellow_sandstone", AstaBlocks.YELLOW_SANDSTONE, AstaBlocks.YELLOW_SANDSTONE_STAIRS, AstaBlocks.YELLOW_SANDSTONE_WALL, AstaBlocks.YELLOW_SANDSTONE_SLAB, AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE, AstaBlocks.YELLOW_SANDSTONE_BUTTON);
        createBlockgroup(blockStateModelGenerator, "blue_sandstone", AstaBlocks.BLUE_SANDSTONE, AstaBlocks.BLUE_SANDSTONE_STAIRS, AstaBlocks.BLUE_SANDSTONE_WALL, AstaBlocks.BLUE_SANDSTONE_SLAB, AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.BLUE_SANDSTONE_BUTTON);
        createBlockgroup(blockStateModelGenerator, "blue_brick", AstaBlocks.BLUE_BRICK, AstaBlocks.BLUE_BRICK_STAIRS, AstaBlocks.BLUE_BRICK_WALL, AstaBlocks.BLUE_BRICK_SLAB, AstaBlocks.BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.BLUE_BRICK_BUTTON);
        createBlockgroup(blockStateModelGenerator, "dark_blue_brick", AstaBlocks.DARK_BLUE_BRICK, AstaBlocks.DARK_BLUE_BRICK_STAIRS, AstaBlocks.DARK_BLUE_BRICK_WALL, AstaBlocks.DARK_BLUE_BRICK_SLAB, AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.DARK_BLUE_BRICK_BUTTON);
        createBlockgroup(blockStateModelGenerator, "purple_brick", AstaBlocks.PURPLE_BRICK, AstaBlocks.PURPLE_BRICK_STAIRS, AstaBlocks.PURPLE_BRICK_WALL, AstaBlocks.PURPLE_BRICK_SLAB, AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE, AstaBlocks.PURPLE_BRICK_BUTTON);
        createBlockgroup(blockStateModelGenerator, "white_brick", AstaBlocks.WHITE_BRICK, AstaBlocks.WHITE_BRICK_STAIRS, AstaBlocks.WHITE_BRICK_WALL, AstaBlocks.WHITE_BRICK_SLAB, AstaBlocks.WHITE_BRICK_PRESSURE_PLATE, AstaBlocks.WHITE_BRICK_BUTTON);

        //Doors
        createDoors(blockStateModelGenerator, "kyratos_door", AstaBlocks.KYRATOS_DOOR);
    }

    private void createBlockgroup(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block, Block stairs, Block wall, Block slab, Block pressurePlate, Block button)
    {
        blockStateModelGenerator.registerCubeAllModelTexturePool(block);
        createStairs(blockStateModelGenerator,textureName, stairs);
        createWalls(blockStateModelGenerator,textureName, wall);
        createSlabs(blockStateModelGenerator, textureName, slab);
        createPressurePlate(blockStateModelGenerator, pressurePlate, block);
        createButtons(blockStateModelGenerator, textureName, button);
    }

    private void createDoors(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block)
    {
        final TextureMap texture = TextureMap.all(AureumAstaDisks.id("block/" + textureName + "_bottom"));
        final TextureMap textureTop = TextureMap.all(AureumAstaDisks.id("block/" + textureName + "_top"));

        final Identifier bottomLeftHingeClosedModelId = Models.DOOR_BOTTOM_LEFT .upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier bottomLeftHingeOpenModelId = Models.DOOR_BOTTOM_LEFT_OPEN.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier bottomRightHingeClosedModelId = Models.DOOR_BOTTOM_RIGHT.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier bottomRightHingeOpenModelId = Models.DOOR_BOTTOM_RIGHT_OPEN.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier topLeftHingeClosedModelId = Models.DOOR_TOP_LEFT.upload(block, textureTop, blockStateModelGenerator.modelCollector);
        final Identifier topLeftHingeOpenModelId = Models.DOOR_TOP_LEFT_OPEN.upload(block, textureTop, blockStateModelGenerator.modelCollector);
        final Identifier topRightHingeClosedModelId = Models.DOOR_TOP_RIGHT.upload(block, textureTop, blockStateModelGenerator.modelCollector);
        final Identifier topRightHingeOpenModelId = Models.DOOR_TOP_RIGHT_OPEN.upload(block, textureTop, blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(block, bottomLeftHingeClosedModelId, bottomLeftHingeOpenModelId, bottomRightHingeClosedModelId, bottomRightHingeOpenModelId, topLeftHingeClosedModelId, topLeftHingeOpenModelId, topRightHingeClosedModelId, topRightHingeOpenModelId));
    }

    private void createStairs(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block)
    {
        final TextureMap texture = TextureMap.all(AureumAstaDisks.id("block/" + textureName));
        final Identifier stairsModelId = Models.STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier innerStairsModelId = Models.INNER_STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier outerStairsModelId = Models.OUTER_STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(block, innerStairsModelId, stairsModelId, outerStairsModelId));
        blockStateModelGenerator.registerParentedItemModel(block, stairsModelId);
    }

    private void createWalls(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block)
    {
        final TextureMap texture = TextureMap.all(AureumAstaDisks.id("block/" + textureName));
        final Identifier wallModelId = Models.TEMPLATE_WALL_POST.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier lowWallModelId = Models.TEMPLATE_WALL_SIDE.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier tallWallModelId = Models.TEMPLATE_WALL_SIDE_TALL.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier inventoryWallModelId = Models.WALL_INVENTORY.upload(block, texture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(block, wallModelId, lowWallModelId, tallWallModelId));
        blockStateModelGenerator.registerParentedItemModel(block, inventoryWallModelId);
    }

    private void createSlabs(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block)
    {
        final TextureMap texture = TextureMap.all(AureumAstaDisks.id("block/" + textureName));
        final Identifier slabBottomModelId = Models.SLAB.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier slabTopModelId = Models.SLAB_TOP.upload(block, texture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block, slabBottomModelId, slabTopModelId, AureumAstaDisks.id("block/" + textureName)));
        blockStateModelGenerator.registerParentedItemModel(block, slabBottomModelId);
    }

    private void createButtons(BlockStateModelGenerator blockStateModelGenerator, String textureName, Block block)
    {
        final TextureMap texture = TextureMap.all(AureumAstaDisks.id("block/" + textureName));
        final Identifier regularModelId = Models.BUTTON.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier pressedModelId = Models.BUTTON_PRESSED.upload(block, texture, blockStateModelGenerator.modelCollector);
        final Identifier inventoryModelId = Models.BUTTON_INVENTORY.upload(block, texture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(block, regularModelId, pressedModelId));
        blockStateModelGenerator.registerParentedItemModel(block, inventoryModelId);
    }

    public final void createPressurePlate(BlockStateModelGenerator blockStateModelGenerator, Block pressurePlate, Block textureSource) {
        TextureMap textureMap = TextureMap.texture(textureSource);
        Identifier identifier = Models.PRESSURE_PLATE_UP.upload(pressurePlate, textureMap, blockStateModelGenerator.modelCollector);
        Identifier identifier2 = Models.PRESSURE_PLATE_DOWN.upload(pressurePlate, textureMap, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(pressurePlate).coordinate(createBooleanModelMap(Properties.POWERED, identifier2, identifier)));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        /*itemModelGenerator.register(AstaItems.THE_VEIL_MUSIC_DISK, Models.GENERATED);
        itemModelGenerator.register(AstaItems.PARTY_LIFETIME_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.HEADLOCK_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.ME_GUSTAS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.YOUR_SISTER_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.MINE_YOURS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.NEW_CHINA_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.MISSES_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(AstaItems.BORED_YET_MUSIC_DISC, Models.GENERATED);*/

        itemModelGenerator.register(AstaItems.AMARITE_HELMET, Models.GENERATED);
        itemModelGenerator.register(AstaItems.AMARITE_HELMET_CROWN, Models.GENERATED);
        itemModelGenerator.register(AstaItems.AMARITE_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(AstaItems.AMARITE_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(AstaItems.AMARITE_BOOTS, Models.GENERATED);

        itemModelGenerator.register(AstaItems.HOST_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.RAISER_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.SHAPER_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.SILENCE_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.WAYFINDER_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.BOLT_ARMOR_TRIM, Models.GENERATED);
        itemModelGenerator.register(AstaItems.FLOW_ARMOR_TRIM, Models.GENERATED);

        itemModelGenerator.register(AstaBlocks.KYRATOS_DOOR.asItem(), Models.GENERATED);
    }

}
