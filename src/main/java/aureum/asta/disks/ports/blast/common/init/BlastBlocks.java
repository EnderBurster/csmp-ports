package aureum.asta.disks.ports.blast.common.init;

import aureum.asta.disks.ports.blast.common.block.*;
import aureum.asta.disks.item.ModItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import static aureum.asta.disks.ports.blast.common.Blast.MODID;

public class BlastBlocks {

    public static Block GUNPOWDER_BLOCK;
    public static Block STRIPMINER;
    public static Block COLD_DIGGER;
    public static Block BONESBURRIER;
    public static Block REMOTE_DETONATOR;
    public static Block DRY_ICE;
    public static Block FOLLY_RED_PAINT;
    public static Block FRESH_FOLLY_RED_PAINT;
    public static Block DRIED_FOLLY_RED_PAINT;

    public static void init() {
        GUNPOWDER_BLOCK = registerBlock(new GunpowderBlock(FabricBlockSettings.of(Material.AGGREGATE, DyeColor.BLACK).strength(0.5F, 0.5f).sounds(BlockSoundGroup.SAND)), "gunpowder_block", ModItemGroup.BlastGroup);
        STRIPMINER = registerBlock(new StripminerBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5f, 2.5f).sounds(BlockSoundGroup.WOOD).nonOpaque(), BlastEntities.STRIPMINER), "stripminer", ModItemGroup.BlastGroup);
        COLD_DIGGER = registerBlock(new StripminerBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5f, 2.5f).sounds(BlockSoundGroup.WOOD).nonOpaque(), BlastEntities.COLD_DIGGER), "cold_digger", ModItemGroup.BlastGroup);
        BONESBURRIER = registerBlock(new BonesburrierBlock(FabricBlockSettings.copyOf(Blocks.BONE_BLOCK)), "bonesburrier", ModItemGroup.BlastGroup);
        REMOTE_DETONATOR = registerBlock(new RemoteDetonatorBlock(FabricBlockSettings.of(Material.METAL).strength(2.5f, 2.5f).sounds(BlockSoundGroup.LANTERN).nonOpaque()), "remote_detonator", ModItemGroup.BlastGroup);
        DRY_ICE = registerBlock(new DryIceBlock(FabricBlockSettings.of(Material.ICE).mapColor(MapColor.LIGHT_GRAY).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque()), "dry_ice", ModItemGroup.BlastGroup);
        FOLLY_RED_PAINT = registerBlock(new FollyRedPaintBlock(FabricBlockSettings.copyOf(Blocks.HONEY_BLOCK).ticksRandomly().strength(0.2f).mapColor(MapColor.BRIGHT_RED)), "folly_red_paint", ModItemGroup.BlastGroup);
        FRESH_FOLLY_RED_PAINT = registerBlock(new FollyRedPaintBlock(FabricBlockSettings.copyOf(Blocks.HONEY_BLOCK).strength(0.2f).mapColor(MapColor.BRIGHT_RED)), "fresh_folly_red_paint", ModItemGroup.BlastGroup);
        DRIED_FOLLY_RED_PAINT = registerBlock(new FollyRedPaintBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).mapColor(MapColor.BRIGHT_RED).strength(0.4f).sounds(BlockSoundGroup.DRIPSTONE_BLOCK).mapColor(MapColor.BRIGHT_RED)), "dried_folly_red_paint", ModItemGroup.BlastGroup);
    }

    private static Block registerBlock(Block block, String name, @Nullable ItemGroup itemGroupKey) {
        return registerBlock(block, name, itemGroupKey, true);
    }

    private static Block registerBlock(Block block, String name, @Nullable ItemGroup itemGroupKey, boolean registerBlockItem) {
        Registry.register(Registries.BLOCK, MODID + ":" + name, block);

        if (registerBlockItem) {
            var blockItem = new BlockItem(block, new Item.Settings());
            blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
            BlastItems.registerItem(blockItem, name, itemGroupKey);
        }
        return block;
    }

}
