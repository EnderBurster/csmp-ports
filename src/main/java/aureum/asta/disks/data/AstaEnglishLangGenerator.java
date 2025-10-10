package aureum.asta.disks.data;

import aureum.asta.disks.damage.AstaDamageSources;
import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.item.AstaItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;

import java.nio.file.Path;

public class AstaEnglishLangGenerator extends FabricLanguageProvider {

    public AstaEnglishLangGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        addBlockSet(translationBuilder, "Orange Sandstone", AstaBlocks.KYRATOS, AstaBlocks.KYRATOS_BRICK_STAIRS, AstaBlocks.KYRATOS_BRICK_WALL, AstaBlocks.KYRATOS_BRICK_SLAB, AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE, AstaBlocks.CHISELED_KYRATOS_BUTTON);
        addBlockSet(translationBuilder, "White Sandstone", AstaBlocks.WHITE_SANDSTONE, AstaBlocks.WHITE_SANDSTONE_STAIRS, AstaBlocks.WHITE_SANDSTONE_WALL, AstaBlocks.WHITE_SANDSTONE_SLAB, AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.WHITE_SANDSTONE_BUTTON);
        addBlockSet(translationBuilder, "Red Sandstone", AstaBlocks.RED_SANDSTONE, AstaBlocks.RED_SANDSTONE_STAIRS, AstaBlocks.RED_SANDSTONE_WALL, AstaBlocks.RED_SANDSTONE_SLAB, AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE, AstaBlocks.RED_SANDSTONE_BUTTON);
        addBlockSet(translationBuilder, "Pink Sandstone", AstaBlocks.PINK_SANDSTONE, AstaBlocks.PINK_SANDSTONE_STAIRS, AstaBlocks.PINK_SANDSTONE_WALL, AstaBlocks.PINK_SANDSTONE_SLAB, AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE, AstaBlocks.PINK_SANDSTONE_BUTTON);
        addBlockSet(translationBuilder, "Yellow Sandstone", AstaBlocks.YELLOW_SANDSTONE, AstaBlocks.YELLOW_SANDSTONE_STAIRS, AstaBlocks.YELLOW_SANDSTONE_WALL, AstaBlocks.YELLOW_SANDSTONE_SLAB, AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE, AstaBlocks.YELLOW_SANDSTONE_BUTTON);
        addBlockSet(translationBuilder, "Blue Sandstone", AstaBlocks.BLUE_SANDSTONE, AstaBlocks.BLUE_SANDSTONE_STAIRS, AstaBlocks.BLUE_SANDSTONE_WALL, AstaBlocks.BLUE_SANDSTONE_SLAB, AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE, AstaBlocks.BLUE_SANDSTONE_BUTTON);

        addBlockSetBrick(translationBuilder, "Blue Brick", AstaBlocks.BLUE_BRICK, AstaBlocks.BLUE_BRICK_STAIRS, AstaBlocks.BLUE_BRICK_WALL, AstaBlocks.BLUE_BRICK_SLAB, AstaBlocks.BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.BLUE_BRICK_BUTTON);
        addBlockSetBrick(translationBuilder, "Dark Blue Brick", AstaBlocks.DARK_BLUE_BRICK, AstaBlocks.DARK_BLUE_BRICK_STAIRS, AstaBlocks.DARK_BLUE_BRICK_WALL, AstaBlocks.DARK_BLUE_BRICK_SLAB, AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE, AstaBlocks.DARK_BLUE_BRICK_BUTTON);
        addBlockSetBrick(translationBuilder, "Purple Brick", AstaBlocks.PURPLE_BRICK, AstaBlocks.PURPLE_BRICK_STAIRS, AstaBlocks.PURPLE_BRICK_WALL, AstaBlocks.PURPLE_BRICK_SLAB, AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE, AstaBlocks.PURPLE_BRICK_BUTTON);
        addBlockSetBrick(translationBuilder, "White Brick", AstaBlocks.WHITE_BRICK, AstaBlocks.WHITE_BRICK_STAIRS, AstaBlocks.WHITE_BRICK_WALL, AstaBlocks.WHITE_BRICK_SLAB, AstaBlocks.WHITE_BRICK_PRESSURE_PLATE, AstaBlocks.WHITE_BRICK_BUTTON);

        translationBuilder.add(AstaBlocks.KYRATOS_GLASS,"Kyratos Glass");
        translationBuilder.add(AstaBlocks.KYRATOS_GLASS_PANE,"Kyratos Glass Pane");
        translationBuilder.add(AstaBlocks.KYRATOS_DOOR,"Kyratos Door");
        translationBuilder.add(AstaBlocks.KYRATOS_PILLAR,"White Sandstone Pillar");
        translationBuilder.add(AstaBlocks.PROTECTION_RUNE,"Protection Rune");
        translationBuilder.add(AstaBlocks.CREATION_RUNE,"Creation Rune");

        translationBuilder.add(AstaItems.GRIMOIRE, "Grimoire");

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/aureum-asta-disks/lang/en_us.existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    private void addBlockSet(TranslationBuilder translationBuilder, String name, Block block, Block stair, Block wall, Block slab, Block pressurePlate, Block button)
    {
        translationBuilder.add(block, name);
        translationBuilder.add(stair, name + " Stairs");
        translationBuilder.add(wall, name + " Wall");
        translationBuilder.add(slab, name + " Slab");
        translationBuilder.add(pressurePlate, name + " Pressure Plate");
        translationBuilder.add(button, name + " Button");
    }

    private void addBlockSetBrick(TranslationBuilder translationBuilder, String name, Block block, Block stair, Block wall, Block slab, Block pressurePlate, Block button)
    {
        translationBuilder.add(block, name + "s");
        translationBuilder.add(stair, name + " Stairs");
        translationBuilder.add(wall, name + " Wall");
        translationBuilder.add(slab, name + " Slab");
        translationBuilder.add(pressurePlate, name + " Pressure Plate");
        translationBuilder.add(button, name + " Button");
    }
}
