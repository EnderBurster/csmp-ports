package aureum.asta.disks.loot;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.AstaItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class AstaLootGenerator {

    //private static final Identifier TREASURE_CHEST_LOOT_TABLE_ID = new Identifier("betterdungeons", "small_dungeon/chests/loot_piles");
    private static final Identifier SKELETON_DUNGEON_CHEST_LOOT_TABLE_ID = new Identifier("betterdungeons", "skeleton_dungeon/chests/common");
    //private static final Identifier SKELETON_DUNGEON_CHEST_LOOT_TABLE_ID_2 = new Identifier("betterdungeons", "skeleton_dungeon/chests/middle");
    private static final Identifier SPIDER_DUNGEON_CHEST_LOOT_TABLE_ID = new Identifier("betterdungeons", "spider_dungeon/chests/egg_room");
    private static final Identifier ZOMBIE_DUNGEON_CHEST_LOOT_TABLE_ID = new Identifier("betterdungeons", "zombie_dungeon/chests/common");
    private static final Identifier ZOMBIE_DUNGEON_CHEST_LOOT_TABLE_ID_2 = new Identifier("betterdungeons", "zombie_dungeon/chests/special");
    //private static final Identifier ZOMBIE_DUNGEON_CHEST_LOOT_TABLE_ID_3 = new Identifier("betterdungeons", "zombie_dungeon/chests/tombstone");
    private static final Identifier DUNGEON_SMALL_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/simple_dungeon");

    private static final Identifier OCEAN_MONUMENT_ID = new Identifier("betteroceanmonuments", "chests/upper_side_chamber");

    private static final Identifier ANCIENT_CITY_ID = new Identifier("minecraft", "chests/ancient_city");
    private static final Identifier BASTION_BRIDGE_ID = new Identifier("minecraft", "chests/bastion_bridge");
    private static final Identifier BASTION_HOGLIN_ID = new Identifier("minecraft", "chests/bastion_hoglin_stable");
    private static final Identifier BASTION_OTHER_ID = new Identifier("minecraft", "chests/bastion_other");
    private static final Identifier BASTION_ID = new Identifier("minecraft", "chests/bastion_treasure");
    private static final Identifier DESERT_TEMPLE_ID = new Identifier("minecraft", "chests/desert_pyramid");
    private static final Identifier END_CITY_ID = new Identifier("minecraft", "chests/end_city_treasure");
    private static final Identifier JUNGLE_TEMPLE_ID = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier NETHER_BRIDGE_ID = new Identifier("minecraft", "chests/nether_bridge");
    private static final Identifier PILLAGER_ID = new Identifier("minecraft", "chests/pillager_outpost");
    private static final Identifier SHIPWRECK_MAP_ID = new Identifier("minecraft", "chests/shipwreck_map");
    private static final Identifier SHIPWRECK_SUPPLY_ID = new Identifier("minecraft", "chests/shipwreck_supply");
    private static final Identifier SHIPWRECK_TREASURE_ID = new Identifier("minecraft", "chests/shipwreck_treasure");
    private static final Identifier STRONGHOLD_CORRIDOR_ID = new Identifier("minecraft", "chests/stronghold_corridor");
    private static final Identifier STRONGHOLD_LIBRARY_ID = new Identifier("minecraft", "chests/stronghold_library");
    private static final Identifier WOODLAND_MANSION_ID = new Identifier("minecraft", "chests/woodland_mansion");

    private static final Identifier ELDER_GUARDIAN_ID = new Identifier("minecraft", "entities/elder_guardian");

    public static void init()
    {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {

            if (SKELETON_DUNGEON_CHEST_LOOT_TABLE_ID.equals(id) || SPIDER_DUNGEON_CHEST_LOOT_TABLE_ID.equals(id) || ZOMBIE_DUNGEON_CHEST_LOOT_TABLE_ID.equals(id) || ZOMBIE_DUNGEON_CHEST_LOOT_TABLE_ID_2.equals(id) || DUNGEON_SMALL_CHEST_LOOT_TABLE_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.332F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(AstaItems.HOST_ARMOR_TRIM).build()).with(ItemEntry.builder(AstaItems.RAISER_ARMOR_TRIM).build()).with(ItemEntry.builder(AstaItems.SHAPER_ARMOR_TRIM).build()).with(ItemEntry.builder(AstaItems.WAYFINDER_ARMOR_TRIM).build()).build();
                supplier.pool(lootPool);
            }

            if (ANCIENT_CITY_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.05F).build();
                LootCondition chanceLootCondition2 = RandomChanceLootCondition.builder(0.012F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                LootPool lootPool2 = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition2).with(ItemEntry.builder(AstaItems.SILENCE_ARMOR_TRIM).build()).build();
                supplier.pool(lootPool);
                supplier.pool(lootPool2);
            }

            if (BASTION_BRIDGE_ID.equals(id) || BASTION_HOGLIN_ID.equals(id) || BASTION_OTHER_ID.equals(id) || BASTION_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.083F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (DESERT_TEMPLE_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.143F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))).build()).build();
                supplier.pool(lootPool);
            }

            if (END_CITY_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.067F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (JUNGLE_TEMPLE_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.333F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))).build()).build();
                supplier.pool(lootPool);
            }

            if (NETHER_BRIDGE_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.067F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (PILLAGER_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.25F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))).build()).build();
                supplier.pool(lootPool);
            }

            if (SHIPWRECK_MAP_ID.equals(id) || SHIPWRECK_SUPPLY_ID.equals(id) || SHIPWRECK_TREASURE_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(1F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))).build()).build();
                supplier.pool(lootPool);
            }

            if (STRONGHOLD_CORRIDOR_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.1F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (STRONGHOLD_LIBRARY_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(1F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (WOODLAND_MANSION_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.5F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }

            if (OCEAN_MONUMENT_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(1F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(AstaItems.BOLT_ARMOR_TRIM).build()).with(ItemEntry.builder(AstaItems.FLOW_ARMOR_TRIM).build()).build();
                supplier.pool(lootPool);
            }

            if (ELDER_GUARDIAN_ID.equals(id)) {
                UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
                LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.2F).build();
                LootPool lootPool = LootPool.builder().rolls(lootTableRange).conditionally(chanceLootCondition).with(ItemEntry.builder(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE).build()).build();
                supplier.pool(lootPool);
            }
        });
    }
}
