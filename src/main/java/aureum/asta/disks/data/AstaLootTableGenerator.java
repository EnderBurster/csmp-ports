package aureum.asta.disks.data;

import aureum.asta.disks.init.AstaBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class AstaLootTableGenerator extends FabricBlockLootTableProvider {

    public AstaLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(AstaBlocks.KYRATOS);
        addDrop(AstaBlocks.KYRATOS_BRICK_STAIRS);
        addDrop(AstaBlocks.KYRATOS_BRICK_WALL);
        addDrop(AstaBlocks.KYRATOS_BRICK_SLAB);
        addDrop(AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE);
        addDrop(AstaBlocks.CHISELED_KYRATOS_BUTTON);

        addDrop(AstaBlocks.WHITE_SANDSTONE);
        addDrop(AstaBlocks.WHITE_SANDSTONE_STAIRS);
        addDrop(AstaBlocks.WHITE_SANDSTONE_WALL);
        addDrop(AstaBlocks.WHITE_SANDSTONE_SLAB);
        addDrop(AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE);
        addDrop(AstaBlocks.WHITE_SANDSTONE_BUTTON);

        addDrop(AstaBlocks.RED_SANDSTONE);
        addDrop(AstaBlocks.RED_SANDSTONE_STAIRS);
        addDrop(AstaBlocks.RED_SANDSTONE_WALL);
        addDrop(AstaBlocks.RED_SANDSTONE_SLAB);
        addDrop(AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE);
        addDrop(AstaBlocks.RED_SANDSTONE_BUTTON);

        addDrop(AstaBlocks.PINK_SANDSTONE);
        addDrop(AstaBlocks.PINK_SANDSTONE_STAIRS);
        addDrop(AstaBlocks.PINK_SANDSTONE_WALL);
        addDrop(AstaBlocks.PINK_SANDSTONE_SLAB);
        addDrop(AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE);
        addDrop(AstaBlocks.PINK_SANDSTONE_BUTTON);

        addDrop(AstaBlocks.YELLOW_SANDSTONE);
        addDrop(AstaBlocks.YELLOW_SANDSTONE_STAIRS);
        addDrop(AstaBlocks.YELLOW_SANDSTONE_WALL);
        addDrop(AstaBlocks.YELLOW_SANDSTONE_SLAB);
        addDrop(AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE);
        addDrop(AstaBlocks.YELLOW_SANDSTONE_BUTTON);

        addDrop(AstaBlocks.BLUE_SANDSTONE);
        addDrop(AstaBlocks.BLUE_SANDSTONE_STAIRS);
        addDrop(AstaBlocks.BLUE_SANDSTONE_WALL);
        addDrop(AstaBlocks.BLUE_SANDSTONE_SLAB);
        addDrop(AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE);
        addDrop(AstaBlocks.BLUE_SANDSTONE_BUTTON);

        addDrop(AstaBlocks.BLUE_BRICK);
        addDrop(AstaBlocks.BLUE_BRICK_STAIRS);
        addDrop(AstaBlocks.BLUE_BRICK_WALL);
        addDrop(AstaBlocks.BLUE_BRICK_SLAB);
        addDrop(AstaBlocks.BLUE_BRICK_PRESSURE_PLATE);
        addDrop(AstaBlocks.BLUE_BRICK_BUTTON);

        addDrop(AstaBlocks.DARK_BLUE_BRICK);
        addDrop(AstaBlocks.DARK_BLUE_BRICK_STAIRS);
        addDrop(AstaBlocks.DARK_BLUE_BRICK_WALL);
        addDrop(AstaBlocks.DARK_BLUE_BRICK_SLAB);
        addDrop(AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE);
        addDrop(AstaBlocks.DARK_BLUE_BRICK_BUTTON);

        addDrop(AstaBlocks.PURPLE_BRICK);
        addDrop(AstaBlocks.PURPLE_BRICK_STAIRS);
        addDrop(AstaBlocks.PURPLE_BRICK_WALL);
        addDrop(AstaBlocks.PURPLE_BRICK_SLAB);
        addDrop(AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE);
        addDrop(AstaBlocks.PURPLE_BRICK_BUTTON);

        addDrop(AstaBlocks.WHITE_BRICK);
        addDrop(AstaBlocks.WHITE_BRICK_STAIRS);
        addDrop(AstaBlocks.WHITE_BRICK_WALL);
        addDrop(AstaBlocks.WHITE_BRICK_SLAB);
        addDrop(AstaBlocks.WHITE_BRICK_PRESSURE_PLATE);
        addDrop(AstaBlocks.WHITE_BRICK_BUTTON);

        addDrop(AstaBlocks.KYRATOS_DOOR);
        addDrop(AstaBlocks.PROTECTION_RUNE);
        addDrop(AstaBlocks.CREATION_RUNE);
        addDrop(AstaBlocks.AMP_RUNE);

        addDropWithSilkTouch(AstaBlocks.KYRATOS_GLASS);
        addDropWithSilkTouch(AstaBlocks.KYRATOS_GLASS_PANE);
    }
}
