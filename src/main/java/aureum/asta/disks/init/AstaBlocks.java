package aureum.asta.disks.init;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.blocks.*;
import aureum.asta.disks.blocks.BarrierBlock;
import aureum.asta.disks.item.ModItemGroup;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteSparkBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public interface AstaBlocks {
    BlockSetType KyratosSandStone = new BlockSetType("kyratos_sandstone", BlockSoundGroup.STONE, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON);


    Block KYRATOS = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos", new Block(FabricBlockSettings.of(Material.STONE, MapColor.ORANGE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block KYRATOS_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_brick_wall", new WallBlock(FabricBlockSettings.copy(KYRATOS)), ModItemGroup.AureumAsta);
    Block KYRATOS_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_brick_stairs", new StairsBlock(KYRATOS.getDefaultState(), FabricBlockSettings.copy(KYRATOS)), ModItemGroup.AureumAsta);
    Block KYRATOS_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_brick_slab", new SlabBlock(FabricBlockSettings.copy(KYRATOS).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block KYRATOS_PILLAR = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_pillar", new PillarBlock(FabricBlockSettings.copy(KYRATOS)), ModItemGroup.AureumAsta);
    Block CHISELED_KYRATOS_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_brick_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block CHISELED_KYRATOS_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_brick_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block WHITE_SANDSTONE = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.OFF_WHITE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block WHITE_SANDSTONE_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone_wall", new WallBlock(FabricBlockSettings.copy(WHITE_SANDSTONE)), ModItemGroup.AureumAsta);
    Block WHITE_SANDSTONE_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone_stairs", new StairsBlock(WHITE_SANDSTONE.getDefaultState(), FabricBlockSettings.copy(WHITE_SANDSTONE)), ModItemGroup.AureumAsta);
    Block WHITE_SANDSTONE_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone_slab", new SlabBlock(FabricBlockSettings.copy(WHITE_SANDSTONE).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block WHITE_SANDSTONE_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block WHITE_SANDSTONE_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_sandstone_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block PINK_SANDSTONE = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PINK).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block PINK_SANDSTONE_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone_wall", new WallBlock(FabricBlockSettings.copy(PINK_SANDSTONE)), ModItemGroup.AureumAsta);
    Block PINK_SANDSTONE_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone_stairs", new StairsBlock(PINK_SANDSTONE.getDefaultState(), FabricBlockSettings.copy(PINK_SANDSTONE)), ModItemGroup.AureumAsta);
    Block PINK_SANDSTONE_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone_slab", new SlabBlock(FabricBlockSettings.copy(PINK_SANDSTONE).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block PINK_SANDSTONE_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.PINK).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block PINK_SANDSTONE_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("pink_sandstone_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block RED_SANDSTONE = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block RED_SANDSTONE_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone_wall", new WallBlock(FabricBlockSettings.copy(RED_SANDSTONE)), ModItemGroup.AureumAsta);
    Block RED_SANDSTONE_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone_stairs", new StairsBlock(RED_SANDSTONE.getDefaultState(), FabricBlockSettings.copy(RED_SANDSTONE)), ModItemGroup.AureumAsta);
    Block RED_SANDSTONE_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone_slab", new SlabBlock(FabricBlockSettings.copy(RED_SANDSTONE).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block RED_SANDSTONE_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.RED).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block RED_SANDSTONE_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("red_sandstone_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block YELLOW_SANDSTONE = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block YELLOW_SANDSTONE_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone_wall", new WallBlock(FabricBlockSettings.copy(YELLOW_SANDSTONE)), ModItemGroup.AureumAsta);
    Block YELLOW_SANDSTONE_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone_stairs", new StairsBlock(YELLOW_SANDSTONE.getDefaultState(), FabricBlockSettings.copy(YELLOW_SANDSTONE)), ModItemGroup.AureumAsta);
    Block YELLOW_SANDSTONE_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone_slab", new SlabBlock(FabricBlockSettings.copy(YELLOW_SANDSTONE).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block YELLOW_SANDSTONE_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block YELLOW_SANDSTONE_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_sandstone_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block BLUE_SANDSTONE = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.LIGHT_BLUE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block BLUE_SANDSTONE_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone_wall", new WallBlock(FabricBlockSettings.copy(BLUE_SANDSTONE)), ModItemGroup.AureumAsta);
    Block BLUE_SANDSTONE_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone_stairs", new StairsBlock(BLUE_SANDSTONE.getDefaultState(), FabricBlockSettings.copy(BLUE_SANDSTONE)), ModItemGroup.AureumAsta);
    Block BLUE_SANDSTONE_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone_slab", new SlabBlock(FabricBlockSettings.copy(BLUE_SANDSTONE).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block BLUE_SANDSTONE_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.LIGHT_BLUE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block BLUE_SANDSTONE_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_sandstone_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block BLUE_BRICK = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick", new Block(FabricBlockSettings.of(Material.STONE, MapColor.LIGHT_BLUE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block BLUE_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick_wall", new WallBlock(FabricBlockSettings.copy(BLUE_BRICK)), ModItemGroup.AureumAsta);
    Block BLUE_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick_stairs", new StairsBlock(BLUE_BRICK.getDefaultState(), FabricBlockSettings.copy(BLUE_BRICK)), ModItemGroup.AureumAsta);
    Block BLUE_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick_slab", new SlabBlock(FabricBlockSettings.copy(BLUE_BRICK).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block BLUE_BRICK_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.LIGHT_BLUE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block BLUE_BRICK_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("blue_brick_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block DARK_BLUE_BRICK = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick", new Block(FabricBlockSettings.of(Material.STONE, MapColor.BLUE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block DARK_BLUE_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick_wall", new WallBlock(FabricBlockSettings.copy(DARK_BLUE_BRICK)), ModItemGroup.AureumAsta);
    Block DARK_BLUE_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick_stairs", new StairsBlock(DARK_BLUE_BRICK.getDefaultState(), FabricBlockSettings.copy(DARK_BLUE_BRICK)), ModItemGroup.AureumAsta);
    Block DARK_BLUE_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick_slab", new SlabBlock(FabricBlockSettings.copy(DARK_BLUE_BRICK).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block DARK_BLUE_BRICK_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.BLUE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block DARK_BLUE_BRICK_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("dark_blue_brick_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block PURPLE_BRICK = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PURPLE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block PURPLE_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick_wall", new WallBlock(FabricBlockSettings.copy(PURPLE_BRICK)), ModItemGroup.AureumAsta);
    Block PURPLE_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick_stairs", new StairsBlock(PURPLE_BRICK.getDefaultState(), FabricBlockSettings.copy(PURPLE_BRICK)), ModItemGroup.AureumAsta);
    Block PURPLE_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick_slab", new SlabBlock(FabricBlockSettings.copy(PURPLE_BRICK).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block PURPLE_BRICK_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.PURPLE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block PURPLE_BRICK_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("purple_brick_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block WHITE_BRICK = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick", new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.STONE).requiresTool()), ModItemGroup.AureumAsta);
    Block WHITE_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick_wall", new WallBlock(FabricBlockSettings.copy(WHITE_BRICK)), ModItemGroup.AureumAsta);
    Block WHITE_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick_stairs", new StairsBlock(WHITE_BRICK.getDefaultState(), FabricBlockSettings.copy(WHITE_BRICK)), ModItemGroup.AureumAsta);
    Block WHITE_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick_slab", new SlabBlock(FabricBlockSettings.copy(WHITE_BRICK).strength(2.4F, 8.0F)), ModItemGroup.AureumAsta);
    Block WHITE_BRICK_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.STONE, MapColor.WHITE).requiresTool().noCollision().strength(0.5F), KyratosSandStone), ModItemGroup.AureumAsta);
    Block WHITE_BRICK_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("white_brick_button", new ButtonBlock(AbstractBlock.Settings.of(Material.STONE).noCollision().strength(0.5F), KyratosSandStone, 20, false), ModItemGroup.AureumAsta);

    Block KYRATOS_DOOR = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_door", new DoorBlock(FabricBlockSettings.copy(KYRATOS), KyratosSandStone), ModItemGroup.AureumAsta);
    Block KYRATOS_GLASS = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_glass", new GlassBlock(AbstractBlock.Settings.of(Material.GLASS).nonOpaque().strength(0.3F)), ModItemGroup.AureumAsta);
    Block KYRATOS_GLASS_PANE = AureumAstaDisks.REGISTRY.registerBlockWithItem("kyratos_glass_pane", new PaneBlock(AbstractBlock.Settings.of(Material.GLASS).nonOpaque().strength(0.3F)), ModItemGroup.AureumAsta);

    //Block PROTECTION_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("protection_rune", new PillarBlock(FabricBlockSettings.copy(WHITE_SANDSTONE)), ModItemGroup.AureumAsta);
    //Block CREATION_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("creation_rune", new PillarBlock(FabricBlockSettings.copy(WHITE_SANDSTONE)), ModItemGroup.AureumAsta);
    //Block AMP_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("amp_rune", new AmpRune(FabricBlockSettings.copy(WHITE_SANDSTONE).nonOpaque()), ModItemGroup.AureumAsta);

    Block PROTECTION_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("protection_rune", new BarrierBlock(FabricBlockSettings.copy(WHITE_SANDSTONE).strength(80f, 1200f)), ModItemGroup.AureumAsta);
    Block CREATION_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("creation_rune", new CreationBlock(FabricBlockSettings.copy(WHITE_SANDSTONE)), ModItemGroup.AureumAsta);
    Block AMP_RUNE = AureumAstaDisks.REGISTRY.registerBlockWithItem("amp_rune", new AmpRune(FabricBlockSettings.copy(WHITE_SANDSTONE).nonOpaque()), ModItemGroup.AureumAsta);
    BlockEntityType<? extends BarrierBlockEntity> PROTECTION_RUNE_ENTITY = AureumAstaDisks.REGISTRY.register("protection_rune", FabricBlockEntityTypeBuilder.create(BarrierBlockEntity::new, new Block[]{PROTECTION_RUNE}).build());
    BlockEntityType<? extends CreationBlockEntity> CREATION_RUNE_ENTITY = AureumAstaDisks.REGISTRY.register("creation_rune", FabricBlockEntityTypeBuilder.create(CreationBlockEntity::new, new Block[]{CREATION_RUNE}).build());
    BlockEntityType<? extends AmpBlockEntity> AMP_RUNE_ENTITY = AureumAstaDisks.REGISTRY.register("amp_rune", FabricBlockEntityTypeBuilder.create(AmpBlockEntity::new, new Block[]{AMP_RUNE}).build());

    static void init() {
    }
}
