package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockSetType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.Material;
import net.minecraft.block.MapColor;
import net.minecraft.block.AbstractBlock.OffsetType;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.sound.SoundEvents;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteClusterBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteSparkBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmariteSparkBlockEntity;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmethystButtonBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.AmethystPressurePlateBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.BuddingAmariteBlock;
import aureum.asta.disks.ports.amarite.amarite.blocks.YellowCarnationPlantBlock;

public interface AmariteBlocks {
   BlockSetType AmethystBlockStateType = new BlockSetType("amethyst", BlockSoundGroup.AMETHYST_BLOCK, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP);

   //Block AMETHYST_BRICKS = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_bricks", new Block(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()), AmariteItems.AMARITE_GROUP);
   Block AMETHYST_BRICKS = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_bricks", new Block(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(1.6F, 8.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()), AmariteItems.AMARITE_GROUP);
   Block AMETHYST_BRICK_WALL = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_brick_wall", new WallBlock(FabricBlockSettings.copy(AMETHYST_BRICKS)), AmariteItems.AMARITE_GROUP);
   Block AMETHYST_BRICK_STAIRS = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_brick_stairs", new StairsBlock(AMETHYST_BRICKS.getDefaultState(), FabricBlockSettings.copy(AMETHYST_BRICKS)), AmariteItems.AMARITE_GROUP);
   Block AMETHYST_BRICK_SLAB = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_brick_slab", new SlabBlock(FabricBlockSettings.copy(AMETHYST_BRICKS).strength(2.4F, 8.0F)), AmariteItems.AMARITE_GROUP);
   Block AMETHYST_PILLAR = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_pillar", new PillarBlock(FabricBlockSettings.copy(AMETHYST_BRICKS)), AmariteItems.AMARITE_GROUP);

   Block CHISELED_AMETHYST = AureumAstaDisks.REGISTRY.registerBlockWithItem("chiseled_amethyst", new Block(FabricBlockSettings.copy(AMETHYST_BRICKS)), AmariteItems.AMARITE_GROUP);
   Block CHISELED_AMETHYST_PRESSURE_PLATE = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_brick_pressure_plate", new AmethystPressurePlateBlock(Settings.of(Material.AMETHYST, MapColor.PURPLE).requiresTool().noCollision().strength(0.5F), AmethystBlockStateType), AmariteItems.AMARITE_GROUP);
   Block CHISELED_AMETHYST_BUTTON = AureumAstaDisks.REGISTRY.registerBlockWithItem("amethyst_brick_button", new AmethystButtonBlock(Settings.of(Material.AMETHYST).noCollision().strength(0.5F), AmethystBlockStateType, 20, false), AmariteItems.AMARITE_GROUP);

   Block AMARITE_BLOCK = AureumAstaDisks.REGISTRY.registerBlockWithItem("amarite_block", new AmariteBlock(FabricBlockSettings.copy(AMETHYST_BRICKS).strength(1.8F, 8.0F)), AmariteItems.AMARITE_GROUP);
   Block BUDDING_AMARITE = AureumAstaDisks.REGISTRY.registerBlockWithItem("budding_amarite", new BuddingAmariteBlock(FabricBlockSettings.copy(AMARITE_BLOCK).ticksRandomly().requiresTool().strength(16.0F, 64.0F)), AmariteItems.AMARITE_GROUP);
   Block AMARITE_CLUSTER = AureumAstaDisks.REGISTRY.registerBlockWithItem("amarite_cluster", new AmariteClusterBlock(7, 3, FabricBlockSettings.copy(AMARITE_BLOCK).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).luminance(state -> 3)), AmariteItems.AMARITE_GROUP);
   Block PARTIAL_AMARITE_BUD = AureumAstaDisks.REGISTRY.registerBlockWithItem("partial_amarite_bud", new AmariteClusterBlock.AmariteBud(5, 3, FabricBlockSettings.copy(AMARITE_CLUSTER).sounds(BlockSoundGroup.LARGE_AMETHYST_BUD).luminance(state -> 2)), AmariteItems.AMARITE_GROUP);
   Block FRESH_AMARITE_BUD = AureumAstaDisks.REGISTRY.registerBlockWithItem("fresh_amarite_bud", new AmariteClusterBlock.AmariteBud(4, 3, FabricBlockSettings.copy(AMARITE_CLUSTER).sounds(BlockSoundGroup.MEDIUM_AMETHYST_BUD).luminance(state -> 1)), AmariteItems.AMARITE_GROUP);
   Block AMARITE_SPARK = AureumAstaDisks.REGISTRY.registerBlockWithItem("amarite_spark", new AmariteSparkBlock(FabricBlockSettings.copy(AMARITE_BLOCK).luminance(state -> 10).nonOpaque().ticksRandomly().strength(5.0F, 32.0F)), AmariteItems.AMARITE_GROUP);

   Block YELLOW_CARNATION_BOUQUET = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_carnation_bouquet", new TallFlowerBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(OffsetType.XZ)), AmariteItems.AMARITE_GROUP);
   Block YELLOW_CARNATION = AureumAstaDisks.REGISTRY.registerBlockWithItem("yellow_carnation", new YellowCarnationPlantBlock(StatusEffects.UNLUCK, 12, FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(OffsetType.XZ)), AmariteItems.AMARITE_GROUP);
   Block POTTED_YELLOW_CARNATION = AureumAstaDisks.REGISTRY.registerBlockWithItem("potted_yellow_carnation", new FlowerPotBlock(YELLOW_CARNATION, FabricBlockSettings.copy(Blocks.POTTED_DANDELION)), AmariteItems.AMARITE_GROUP);
   BlockEntityType<? extends AmariteSparkBlockEntity> AMARITE_SPARK_BLOCK_ENTITY = AureumAstaDisks.REGISTRY.register("amarite_spark", FabricBlockEntityTypeBuilder.create(AmariteSparkBlockEntity::new, new Block[]{AMARITE_SPARK}).build());

   static void init() {
   }

   @Environment(EnvType.CLIENT)
   static void initClient() {
      BlockRenderLayerMap.INSTANCE.putBlocks(
         RenderLayer.getCutout(),
         new Block[]{
            FRESH_AMARITE_BUD, PARTIAL_AMARITE_BUD, AMARITE_CLUSTER, AMARITE_SPARK, YELLOW_CARNATION, YELLOW_CARNATION_BOUQUET, POTTED_YELLOW_CARNATION
         }
      );
   }
}
