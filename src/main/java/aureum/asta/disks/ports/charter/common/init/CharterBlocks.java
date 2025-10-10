package aureum.asta.disks.ports.charter.common.init;

import aureum.asta.disks.item.ModItemGroup;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.charter.common.block.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public interface CharterBlocks {
   Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
   Map<BlockEntityType<?>, Identifier> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();
   TagKey<Block> BREAKABLES = TagKey.of(RegistryKeys.BLOCK, Charter.id("breakable_blocks"));

   Block PAWN = createBlock(
           "pawn",
           new PawnBlock(FabricBlockSettings.copyOf(Blocks.PACKED_MUD)
                   .requiresTool()
                   .luminance(createLightLevelFromLitBlockState(10))
                   .ticksRandomly()),
           true
   );

   Block CHARTER_STONE = createBlock(
           "charter_stone",
           new CharterStoneBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE)
                   .requiresTool()
                   .nonOpaque()),
           true
   );

   Block SUSPICIOUS_DIRT = createBlock("suspicious_dirt", new BrushableBlock(FabricBlockSettings.copyOf(Blocks.DIRT).dropsNothing()), true);
   Block SUSPICIOUS_SAND = createBlock("suspicious_sand", new SuspiciousSandBlock(FabricBlockSettings.copyOf(Blocks.SAND).dropsNothing()), true);

   BlockEntityType<PawnBlockEntity> PAWN_BLOCK_ENTITY = createBlockEntity(
           "pawn",
           FabricBlockEntityTypeBuilder.create(PawnBlockEntity::new, PAWN).build()
   );

   BlockEntityType<BrushableBlockEntity> SUSPICIOUS_DIRT_ENTITY = createBlockEntity(
           "suspicious_dirt",
           FabricBlockEntityTypeBuilder.create(BrushableBlockEntity::new, SUSPICIOUS_DIRT).build()
   );

   BlockEntityType<BrushableBlockEntity> SUSPICIOUS_SAND_ENTITY = createBlockEntity(
           "suspicious_sand",
        FabricBlockEntityTypeBuilder.create(BrushableBlockEntity::new, SUSPICIOUS_SAND).build()
   );

   private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String name, BlockEntityType<T> type) {
      BLOCK_ENTITY_TYPES.put(type, new Identifier("charter", name));
      return type;
   }

   private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
      BLOCKS.put(block, new Identifier("charter", name));
      if (createItem) {
         CharterItems.ITEMS.put(new BlockItem(block, new Settings()), BLOCKS.get(block));
      }

      return block;
   }

   private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
      return state -> state.get(Properties.LIT) ? litLevel : 0;
   }

   static void init() {
      BLOCKS.keySet().forEach(block -> Registry.register(Registries.BLOCK, BLOCKS.get(block), block));
      BLOCK_ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registries.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPES.get(entityType), entityType));
   }
}
