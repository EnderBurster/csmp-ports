package aureum.asta.disks.item;

import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.index.ArsenalToolMaterials;
import aureum.asta.disks.item.custom.AmariteArmorItem;
import aureum.asta.disks.item.custom.GrimoireItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public interface AstaItems {
    Item SCYTHE = AureumAstaDisks.REGISTRY.registerItem("scythe", new Item(new FabricItemSettings()), ModItemGroup.AureumAsta);

    Item BLOOD_SCYTHE = AureumAstaDisks.REGISTRY.registerItem("blood_scythe", new ScytheItem(ArsenalToolMaterials.SCYTHE, 5, -3f, new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);
    Item DUK_SWORD = AureumAstaDisks.REGISTRY.registerItem("duk_sword", new DukSwordItem(ToolMaterials.NETHERITE, 5, -2.7f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)), ModItemGroup.AureumAsta);

    Item GRIMOIRE = AureumAstaDisks.REGISTRY.registerItem("grimoire", new GrimoireItem(new FabricItemSettings().maxCount(1).fireproof()), ModItemGroup.AureumAsta);

    Item GRIMOIRE_AQUABLADE_ITEM = AureumAstaDisks.REGISTRY.registerItem("aquablade", new Item(new FabricItemSettings()), ModItemGroup.AureumAsta);

    Item AMARITE_HELMET = AureumAstaDisks.REGISTRY.registerItem("amarite_helmet", new AmariteArmorItem(AstaArmorMaterials.AMARITE, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);
    Item AMARITE_HELMET_CROWN = AureumAstaDisks.REGISTRY.registerItem("amarite_helmet_crown", new AmariteArmorItem(AstaArmorMaterials.AMARITE, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);
    Item AMARITE_CHESTPLATE = AureumAstaDisks.REGISTRY.registerItem("amarite_chestplate", new AmariteArmorItem(AstaArmorMaterials.AMARITE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);
    Item AMARITE_LEGGINGS = AureumAstaDisks.REGISTRY.registerItem("amarite_leggings", new AmariteArmorItem(AstaArmorMaterials.AMARITE, ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);
    Item AMARITE_BOOTS = AureumAstaDisks.REGISTRY.registerItem("amarite_boots", new AmariteArmorItem(AstaArmorMaterials.AMARITE, ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.COMMON)), ModItemGroup.AureumAsta);

    /*Item THE_VEIL_MUSIC_DISK = AureumAstaDisks.REGISTRY.registerItem("the_veil_music_disk", new MusicDiscItem(6, AstaSounds.THE_VEIL, new FabricItemSettings().maxCount(1), 244), ModItemGroup.AureumAsta);
    Item PARTY_LIFETIME_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("party_lifetime_music_disc", new MusicDiscItem(6, AstaSounds.PARTY_LIFETIME, new FabricItemSettings().maxCount(1), 217), ModItemGroup.AureumAsta);
    Item HEADLOCK_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("headlock_music_disc", new MusicDiscItem(6, AstaSounds.HEADLOCK, new FabricItemSettings().maxCount(1), 215), ModItemGroup.AureumAsta);
    Item ME_GUSTAS_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("me_gustas_music_disc", new MusicDiscItem(6, AstaSounds.ME_GUSTAS, new FabricItemSettings().maxCount(1), 240), ModItemGroup.AureumAsta);
    Item YOUR_SISTER_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("your_sister_music_disc", new MusicDiscItem(6, AstaSounds.YOUR_SISTER, new FabricItemSettings().maxCount(1), 153), ModItemGroup.AureumAsta);
    Item MINE_YOURS_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("mine_yours_music_disc", new MusicDiscItem(6, AstaSounds.MINE_YOURS, new FabricItemSettings().maxCount(1), 187), ModItemGroup.AureumAsta);
    Item NEW_CHINA_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("new_china_music_disc", new MusicDiscItem(6, AstaSounds.NEW_CHINA, new FabricItemSettings().maxCount(1), 99), ModItemGroup.AureumAsta);
    Item MISSES_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("misses_music_disc", new MusicDiscItem(6, AstaSounds.MISSES, new FabricItemSettings().maxCount(1), 75), ModItemGroup.AureumAsta);
    Item BORED_YET_MUSIC_DISC = AureumAstaDisks.REGISTRY.registerItem("bored_yet_music_disc", new MusicDiscItem(6, AstaSounds.BORED_YET, new FabricItemSettings().maxCount(1), 207), ModItemGroup.AureumAsta);*/

    RegistryKey<ArmorTrimPattern> HOST = of("host");
    RegistryKey<ArmorTrimPattern> RAISER = of("raiser");
    RegistryKey<ArmorTrimPattern> SHAPER = of("shaper");
    RegistryKey<ArmorTrimPattern> SILENCE = of("silence");
    RegistryKey<ArmorTrimPattern> WAYFINDER = of("wayfinder");
    RegistryKey<ArmorTrimPattern> BOLT = of("bolt");
    RegistryKey<ArmorTrimPattern> FLOW = of("flow");

    Item HOST_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("host_armor_trim_smithing_template", SmithingTemplateItem.of(HOST), ModItemGroup.AureumAsta);
    Item RAISER_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("raiser_armor_trim_smithing_template", SmithingTemplateItem.of(RAISER), ModItemGroup.AureumAsta);
    Item SHAPER_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("shaper_armor_trim_smithing_template", SmithingTemplateItem.of(SHAPER), ModItemGroup.AureumAsta);
    Item SILENCE_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("silence_armor_trim_smithing_template", SmithingTemplateItem.of(SILENCE), ModItemGroup.AureumAsta);
    Item WAYFINDER_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("wayfinder_armor_trim_smithing_template", SmithingTemplateItem.of(WAYFINDER), ModItemGroup.AureumAsta);
    Item BOLT_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("bolt_armor_trim_smithing_template", SmithingTemplateItem.of(BOLT), ModItemGroup.AureumAsta);
    Item FLOW_ARMOR_TRIM = AureumAstaDisks.REGISTRY.registerVanillaItem("flow_armor_trim_smithing_template", SmithingTemplateItem.of(FLOW), ModItemGroup.AureumAsta);

    //Item DEBUG_ITEM = AureumAstaDisks.REGISTRY.registerItem("debug_item", new Item(new FabricItemSettings().maxCount(1)), ModItemGroup.AureumAsta);

    private static void addToItemGroup()
    {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(list -> {
            list.addAfter(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, AstaItems.SILENCE_ARMOR_TRIM, BOLT_ARMOR_TRIM, FLOW_ARMOR_TRIM, HOST_ARMOR_TRIM, RAISER_ARMOR_TRIM, SHAPER_ARMOR_TRIM, WAYFINDER_ARMOR_TRIM);
        });

        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.AureumAsta).register(list -> {
            list.add(AstaItems.SCYTHE.getDefaultStack());

            list.add(AstaItems.BLOOD_SCYTHE.getDefaultStack());
            list.add(AstaItems.DUK_SWORD.getDefaultStack());

            /*list.add(AstaItems.AMARITE_HELMET_CROWN.getDefaultStack());
            list.add(AstaItems.AMARITE_HELMET.getDefaultStack());
            list.add(AstaItems.AMARITE_CHESTPLATE.getDefaultStack());
            list.add(AstaItems.AMARITE_LEGGINGS.getDefaultStack());
            list.add(AstaItems.AMARITE_BOOTS.getDefaultStack());

            list.add(AstaItems.THE_VEIL_MUSIC_DISK.getDefaultStack());
            list.add(AstaItems.PARTY_LIFETIME_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.HEADLOCK_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.ME_GUSTAS_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.YOUR_SISTER_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.MINE_YOURS_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.NEW_CHINA_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.MISSES_MUSIC_DISC.getDefaultStack());
            list.add(AstaItems.BORED_YET_MUSIC_DISC.getDefaultStack());

            list.add(AstaItems.DEBUG_ITEM.getDefaultStack());*/
        });

        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.Kyratos).register(list -> {
            list.add(AstaItems.GRIMOIRE.getDefaultStack());

            list.add(AstaBlocks.KYRATOS.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_BRICK_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_BRICK_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_BRICK_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.CHISELED_KYRATOS_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.KYRATOS_PILLAR.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_DOOR.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_GLASS.asItem().getDefaultStack());
            list.add(AstaBlocks.KYRATOS_GLASS_PANE.asItem().getDefaultStack());
            list.add(AstaBlocks.PROTECTION_RUNE.asItem().getDefaultStack());
            list.add(AstaBlocks.CREATION_RUNE.asItem().getDefaultStack());
            list.add(AstaBlocks.AMP_RUNE.asItem().getDefaultStack());

            list.add(AstaBlocks.WHITE_SANDSTONE.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_SANDSTONE_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_SANDSTONE_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_SANDSTONE_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_SANDSTONE_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.RED_SANDSTONE.asItem().getDefaultStack());
            list.add(AstaBlocks.RED_SANDSTONE_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.RED_SANDSTONE_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.RED_SANDSTONE_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.RED_SANDSTONE_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.PINK_SANDSTONE.asItem().getDefaultStack());
            list.add(AstaBlocks.PINK_SANDSTONE_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.PINK_SANDSTONE_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.PINK_SANDSTONE_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.PINK_SANDSTONE_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.YELLOW_SANDSTONE.asItem().getDefaultStack());
            list.add(AstaBlocks.YELLOW_SANDSTONE_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.YELLOW_SANDSTONE_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.YELLOW_SANDSTONE_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.YELLOW_SANDSTONE_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.BLUE_SANDSTONE.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_SANDSTONE_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_SANDSTONE_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_SANDSTONE_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_SANDSTONE_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.BLUE_BRICK.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_BRICK_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_BRICK_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_BRICK_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_BRICK_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.BLUE_BRICK_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.DARK_BLUE_BRICK.asItem().getDefaultStack());
            list.add(AstaBlocks.DARK_BLUE_BRICK_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.DARK_BLUE_BRICK_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.DARK_BLUE_BRICK_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.DARK_BLUE_BRICK_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.PURPLE_BRICK.asItem().getDefaultStack());
            list.add(AstaBlocks.PURPLE_BRICK_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.PURPLE_BRICK_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.PURPLE_BRICK_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.PURPLE_BRICK_BUTTON.asItem().getDefaultStack());

            list.add(AstaBlocks.WHITE_BRICK.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_BRICK_STAIRS.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_BRICK_WALL.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_BRICK_SLAB.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_BRICK_PRESSURE_PLATE.asItem().getDefaultStack());
            list.add(AstaBlocks.WHITE_BRICK_BUTTON.asItem().getDefaultStack());
        });
    }

    private static RegistryKey<ArmorTrimPattern> of(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_PATTERN, new Identifier(id));
    }

    static void registerModItems()
    {
        AureumAstaDisks.LOGGER.info("Registering Mod Items for " + AureumAstaDisks.MOD_ID);

        addToItemGroup();
    }
}
