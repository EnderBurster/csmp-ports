package aureum.asta.disks.ports.amarite.amarite;

/*import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Block;
import net.minecraft.class_2320;
import net.minecraft.registry.Registry;
import net.minecraft.class_2405;
import net.minecraft.class_2430;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.class_2446;
import net.minecraft.class_2447;
import net.minecraft.class_2450;
import net.minecraft.class_2756;
import net.minecraft.util.Identifier;
import net.minecraft.class_3481;
import net.minecraft.class_3489;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.class_4943;
import net.minecraft.TexturedModel;
import net.minecraft.loot.LootTable;
import net.minecraft.class_5793;
import net.minecraft.class_5794;
import net.minecraft.class_7403;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.BlockStateModelGenerator.class_4913;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;*/
import aureum.asta.disks.ports.amarite.mialib.data.MDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AmariteDatagen extends MDataGen {
   @Override
   public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

   }
   /*private static final class_5794 AMETHYST_BRICK = class_5793.method_33468(AmariteBlocks.AMETHYST_BRICKS)
      .method_33497(AmariteBlocks.AMETHYST_BRICK_WALL)
      .method_33493(AmariteBlocks.AMETHYST_BRICK_STAIRS)
      .method_33492(AmariteBlocks.AMETHYST_BRICK_SLAB)
      .method_33481();
   private static final class_5794 CHISELED_AMETHYST = class_5793.method_33468(AmariteBlocks.CHISELED_AMETHYST)
      .method_33494(AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE)
      .method_33482(AmariteBlocks.CHISELED_AMETHYST_BUTTON)
      .method_33481();

   @Override
   public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
      super.onInitializeDataGenerator(generator);
      generator.addProvider(gen -> new AmariteDatagen.MirrorCooldownProvider(gen) {
            @Override
            public void generateCooldowns(BiConsumer<Identifier, Integer> consumer) {
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.SPEED), 120);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.HASTE), 100);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.RESISTANCE), 640);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.JUMP_BOOST), 80);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.STRENGTH), 560);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.REGENERATION), 640);
               consumer.accept(Registry.STATUS_EFFECT.method_10221(StatusEffects.CONDUIT_POWER), 240);
            }
         });
   }

   @Override
   protected void generateBlockLootTables(@NotNull MDataGen.MBlockLootTableProvider provider) {
      provider.method_16329(AmariteBlocks.AMETHYST_BRICKS);
      provider.method_16329(AmariteBlocks.AMETHYST_BRICK_WALL);
      provider.method_16329(AmariteBlocks.AMETHYST_BRICK_STAIRS);
      provider.addDrop(AmariteBlocks.AMETHYST_BRICK_SLAB, class_2430::method_10383);
      provider.method_16329(AmariteBlocks.AMETHYST_PILLAR);
      provider.method_16329(AmariteBlocks.CHISELED_AMETHYST);
      provider.method_16329(AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE);
      provider.method_16329(AmariteBlocks.CHISELED_AMETHYST_BUTTON);
      provider.method_16329(AmariteBlocks.AMARITE_BLOCK);
      provider.method_16258(AmariteBlocks.BUDDING_AMARITE, LootTable.builder());
      provider.addDrop(
         AmariteBlocks.AMARITE_CLUSTER,
         block -> class_2430.method_10397(block, ItemEntry.builder(AmariteItems.AMARITE_SHARD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))))
      );
      provider.addDrop(AmariteBlocks.PARTIAL_AMARITE_BUD, class_2430::method_10373);
      provider.addDrop(AmariteBlocks.FRESH_AMARITE_BUD, class_2430::method_10373);
      provider.method_16329(AmariteBlocks.AMARITE_SPARK);
      provider.addDrop(AmariteBlocks.YELLOW_CARNATION_BOUQUET, block -> class_2430.method_10375(block, class_2320.field_10929, class_2756.field_12607));
      provider.method_16329(AmariteBlocks.YELLOW_CARNATION);
      provider.method_16285(AmariteBlocks.POTTED_YELLOW_CARNATION);
   }

   @Override
   protected void generateTranslations(@NotNull MDataGen.MLanguageProvider provider, @NotNull TranslationBuilder builder) {
      builder.add(AmariteBlocks.AMETHYST_BRICKS.method_9539(), "Amethyst Bricks");
      builder.add(AmariteBlocks.AMETHYST_BRICK_WALL.method_9539(), "Amethyst Brick Wall");
      builder.add(AmariteBlocks.AMETHYST_BRICK_STAIRS.method_9539(), "Amethyst Brick Stairs");
      builder.add(AmariteBlocks.AMETHYST_BRICK_SLAB.method_9539(), "Amethyst Brick Slab");
      builder.add(AmariteBlocks.AMETHYST_PILLAR.method_9539(), "Amethyst Pillar");
      builder.add(AmariteBlocks.CHISELED_AMETHYST.method_9539(), "Chiseled Amethyst");
      builder.add(AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE.method_9539(), "Chiseled Amethyst Pressure Plate");
      builder.add(AmariteBlocks.CHISELED_AMETHYST_BUTTON.method_9539(), "Chiseled Amethyst Button");
      builder.add(AmariteBlocks.AMARITE_BLOCK.method_9539(), "Amarite Block");
      builder.add(AmariteBlocks.BUDDING_AMARITE.method_9539(), "Budding Amarite");
      builder.add(AmariteBlocks.AMARITE_CLUSTER.method_9539(), "Amarite Cluster");
      builder.add(AmariteBlocks.PARTIAL_AMARITE_BUD.method_9539(), "Partial Amarite Bud");
      builder.add(AmariteBlocks.FRESH_AMARITE_BUD.method_9539(), "Fresh Amarite Bud");
      builder.add(AmariteBlocks.AMARITE_SPARK.method_9539(), "Amarite Spark");
      builder.add(AmariteBlocks.YELLOW_CARNATION.method_9539(), "Yellow Carnation");
      builder.add(AmariteBlocks.YELLOW_CARNATION_BOUQUET.method_9539(), "Yellow Carnation Bouquet");
      builder.add(AmariteBlocks.POTTED_YELLOW_CARNATION.method_9539(), "Potted Yellow Carnation");
      builder.add("block.amarite.engulfed_beacon", "Engulfed Beacon");
      builder.add("block.amarite.amarite_prism", "Amarite Prism");
      builder.add("block.amarite.amarite_emitter", "Amarite Emitter");
      String[] buddingKeys = provider.getDamageKeys("amarite.budding");
      builder.add(buddingKeys[0], "%1$s was vitrified");
      builder.add(buddingKeys[1], "%1$s was vitrified by %2$s");
      builder.add(buddingKeys[2], "%1$s was vitrified by %2$s using %3$s");
      String[] dashKeys = provider.getDamageKeys("amarite.dash");
      builder.add(dashKeys[0], "%1$s was subdivided");
      builder.add(dashKeys[1], "%1$s was subdivided by %2$s");
      builder.add(dashKeys[2], "%1$s was subdivided by %2$s wielding %3$s");
      String[] accumulateKeys = provider.getDamageKeys("amarite.accumulate");
      builder.add(accumulateKeys[0], "%1$s was absorbed");
      builder.add(accumulateKeys[1], "%1$s was absorbed by %2$s");
      builder.add(accumulateKeys[2], "%1$s was absorbed by %2$s wielding %3$s");
      String[] malignancyKeys = provider.getDamageKeys("amarite.malignancy");
      builder.add(malignancyKeys[0], "%1$s was tarnished");
      builder.add(malignancyKeys[1], "%1$s was tarnished by %2$s");
      builder.add(malignancyKeys[2], "%1$s was tarnished by %2$s wielding %3$s");
      String[] discKeys = provider.getDamageKeys("amarite.disc");
      builder.add(discKeys[0], "%1$s was diced up");
      builder.add(discKeys[1], "%1$s was diced up by %2$s");
      builder.add(discKeys[2], "%1$s was diced up by %2$s lobbing %3$s");
      String[] raiserKeys = provider.getDamageKeys("amarite.raiser");
      builder.add(raiserKeys[0], "%1$s was detonated");
      builder.add(raiserKeys[1], "%1$s was detonated by %2$s");
      builder.add(raiserKeys[2], "%1$s was detonated by %2$s lobbing %3$s");
      String[] swapKeys = provider.getDamageKeys("amarite.swap");
      builder.add(swapKeys[0], "%1$s was telefragged");
      builder.add(swapKeys[1], "%1$s was telefragged by %2$s");
      builder.add(swapKeys[2], "%1$s was telefragged by %2$s lobbing %3$s");
      String[] disintegratedKeys = provider.getDamageKeys("amarite.disintegrated");
      builder.add(disintegratedKeys[0], "%1$s was disintegrated");
      builder.add(disintegratedKeys[1], "%1$s was disintegrated by %2$s");
      builder.add(disintegratedKeys[2], "%1$s was disintegrated by %2$s wielding %3$s");
      builder.add(AmariteEnchantments.DOUBLE_DASH.method_8184(), "Double Dash");
      builder.add(AmariteEnchantments.DOUBLE_DASH.method_8184() + ".desc", "Allows you to use two faster dashes that dont deal damage.");
      builder.add(AmariteEnchantments.ACCUMULATE.method_8184(), "Accumulate");
      builder.add(AmariteEnchantments.ACCUMULATE.method_8184() + ".desc", "Replaces your dash with a beam which can pull in entities.");
      builder.add(AmariteEnchantments.REBOUND.method_8184(), "Rebound");
      builder.add(AmariteEnchantments.REBOUND.method_8184() + ".desc", "Causes all discs to target nearby enemies.");
      builder.add(AmariteEnchantments.ANONYMITY.method_8184(), "Anonymity");
      builder.add(AmariteEnchantments.ANONYMITY.method_8184() + ".desc", "Hides your name from kill messages.");
      builder.add(AmariteEnchantments.CONCEALMENT.method_8184(), "Concealment");
      builder.add(AmariteEnchantments.CONCEALMENT.method_8184() + ".desc", "Conceals your name tag.");
      builder.add("enchantment.amarite.malignancy", "Malignancy");
      builder.add("enchantment.amarite.malignancy.desc", "Allows you to fire out a toxic cloud.");
      builder.add("enchantment.amarite.revelation", "Revelation");
      builder.add("enchantment.amarite.revelation.desc", "Replaces your dash with a purifying beam that deals heavy damage.");
      builder.add("enchantment.amarite.crystal_rush", "Crystal Rush");
      builder.add("enchantment.amarite.crystal_rush.desc", "Replaces your dash with a flurry of amarite shards.");
      builder.add("enchantment.amarite.rubberband", "Rubberband");
      builder.add("enchantment.amarite.rubberband.desc", "Replaces your dash with a return to your location 5 seconds prior.");
      builder.add("enchantment.amarite.duplicate", "Duplicate");
      builder.add("enchantment.amarite.duplicate.desc", "Replaces your dash with a crystal clone of yourself.");
      builder.add("enchantment.amarite.drain", "Drain");
      builder.add("enchantment.amarite.drain.desc", "Replaces your dash with a temporary damage buff at the cost of amarite charge.");
      builder.add("enchantment.amarite.raiser", "Raiser");
      builder.add("enchantment.amarite.raiser.desc", "Causes all of your discs to explode on demand.");
      builder.add("enchantment.amarite.orbit", "Orbit");
      builder.add("enchantment.amarite.orbit.desc", "Causes your discs to orbit around you.");
      builder.add("enchantment.amarite.magnet", "Magnet");
      builder.add("enchantment.amarite.magnet.desc", "Causes your discs to be attracted to a magnet.");
      builder.add("enchantment.amarite.swap", "Swap");
      builder.add("enchantment.amarite.swap.desc", "Causes you to swap places with the last thrown disc.");
      builder.add("enchantment.amarite.pluto", "Pluto");
      builder.add("enchantment.amarite.pluto.desc", "Grants 9 smaller discs with lower durability.");
      builder.add("enchantment.amarite.split", "Split");
      builder.add("enchantment.amarite.split.desc", "Grants 3 discs that split into 3 on impact.");
      builder.add("enchantment.amarite.engorged", "Engorged");
      builder.add("enchantment.amarite.engorged.desc", "Grants 1 large unbreakable disc.");
      builder.add("enchantment.amarite.lazy", "Lazy");
      builder.add("enchantment.amarite.lazy.desc", "Grants 3 discs that fly at half the normal speed.");
      builder.add(AmariteEntities.DISC.method_5882(), "Amarite Disc");
      builder.add("entity.amarite.beam", "Revelation Beam");
      builder.add("entity.amarite.magnet", "Magnet");
      builder.add("entity.amarite.shard", "Amarite Shard");
      builder.add("entity.amarite.duplicate", "Crystal Clone");
      builder.add("entity.amarite.planet", "Crystal Planet");
      builder.add(AmariteEntities.BUDDING.getTranslationKey(), "Budding");
      builder.add("effect.amarite.drained", "Drained");
      builder.add("itemGroup.amarite.amarite_group", "Amarite");
      builder.add("itemGroup.amarite.mirror_group", "Amarite Mirrors");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey(), "Amarite Longsword");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".desc_1", "Block attacks by holding %s to absorb half of incoming damage.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".desc_2", "%s while blocking to %s");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".dash", "dash forward slicing through enemies.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".double_dash", "dash forward with great speed.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".accumulate", "absorb entities in front of you.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".malignancy", "launch a toxic cloud forward.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".revelation", "charge a purified beam where you look.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".crystal_rush", "unleash a flurry of amarite shards.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".rubberband", "return to your location from 5 seconds prior.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".duplicate", "create a crystal clone of yourself who will walk forward.");
      builder.add(AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".drain", "drain all charge from all amarite items to gain a temporary damage buff.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey(), "Amarite Disc");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".desc_1", "Throw discs with %s");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".desc_2", "%s to %s");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".desc_3", "%s");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".desc", "StarbornNebula - Pacificus");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".recall", "recall all discs at high speed.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".rebound", "cause all discs to target nearby enemies.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".raiser", "detonate all of your discs, at the cost of a durability.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".orbit", "spin your discs around yourself.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".magnet", "fire a magnet that will attract discs.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".swap", "swap places with the last thrown disc.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".count", "Hold 3 discs with 3 durability.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".pluto", "Hold 9 smaller discs that only have 1 durability.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".split", "Hold 3 discs that split into 3 on impact, but only have 1 durability.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".engorged", "Hold 1 large unbreakable disc.");
      builder.add(AmariteItems.AMARITE_DISC.getTranslationKey() + ".lazy", "Hold 3 discs with 3 durability that fly at half the normal speed.");
      builder.add(AmariteItems.AMARITE_MIRROR.getTranslationKey(), "Amarite Pocket Mirror");
      builder.add(AmariteItems.AMARITE_MIRROR.getTranslationKey() + ".empty", "Crouch use on a beacon or conduit to extract its effects.");
      builder.add(AmariteItems.AMARITE_MIRROR.getTranslationKey() + ".cooldown", "Has a cooldown of %s and lasts for 15 seconds.");
      builder.add("item.amarite.amarite_focus", "Amarite Focus");
      builder.add("item.amarite.amarite_totem_mirror", "Amarite Totem Mirror");
      builder.add("item.amarite.amarite_totem_mirror.charges", "%s charges");
      builder.add(AmariteItems.AXOLOTL_MASK.getTranslationKey(), "Axolotl Mask");
      builder.add(AmariteItems.BUNNY_MASK.getTranslationKey(), "Bunny Mask");
      builder.add(AmariteItems.BUTTERFLY_MASK.getTranslationKey(), "Butterfly Mask");
      builder.add(AmariteItems.DEMON_MASK.getTranslationKey(), "Demon Mask");
      builder.add(AmariteItems.FOX_MASK.getTranslationKey(), "Fox Mask");
      builder.add(AmariteItems.GUARDIAN_MASK.getTranslationKey(), "Guardian Mask");
      builder.add(AmariteItems.MOON_MASK.getTranslationKey(), "Moon Mask");
      builder.add(AmariteItems.ONI_MASK.getTranslationKey(), "Oni Mask");
      builder.add(AmariteItems.UNICORN_MASK.getTranslationKey(), "Unicorn Mask");
      builder.add(AmariteItems.WARDEN_MASK.getTranslationKey(), "Warden Mask");
      builder.add(AmariteItems.WINSWEEP_MASK.getTranslationKey(), "Winsweep Mask");
      builder.add(AmariteItems.AMARITE_CROWN.getTranslationKey(), "Amarite Crown");
      builder.add("item.amarite.breeze_mask", "Windy Mask");
      builder.add("item.amarite.mysterious_mask", "Mysterious Mask");
      builder.add("item.amarite.shade_mask", "Otherworldly Mask");
      builder.add("item.amarite.blot_mask", "Inkblot Mask");
      builder.add("item.amarite.elysium_mask", "Broken CRT");
      builder.add("item.amarite.mask.anonymity", "Hides your name from kill messages.");
      builder.add("item.amarite.mask.concealment", "Conceals your name tag.");
      builder.add("item.amarite.mask.attunement", "");
      builder.add("item.amarite.mask.unstable", "Teleport randomly upon taking damage from a player.");
      builder.add("item.amarite.mask.camouflage", "Grants pure invisibility in range of an Amarite Spark.");
      builder.add("item.amarite.mask.haunting", "Causes other players to see fake copies of you in random places.");
      builder.add("item.amarite.mask.offset_1", "Model can be offset with %s.");
      builder.add("item.amarite.mask.offset_2", "Current offset: %s.");
      builder.add(AmariteItems.WIN_COOKIE.getTranslationKey(), "Win Cookie");
      builder.add(AmariteItems.WIN_COOKIE.getTranslationKey() + ".desc", "This is what the point of the mask is.");
      builder.add("item.amarite.amarite_cookie", "Crystal Cookie");
      builder.add("item.amarite.amy_cookie", "Witchy Cookie");
      builder.add(AmariteItems.AMARITE_SHARD.getTranslationKey(), "Amarite Shard");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_DAMAGE), "Disc hits Entity");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_HIT), "Disc hits Block");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_THROW), "Disc Thrown");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_PICKUP_1), "Disc Picked Up");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_PICKUP_2), "Disc Picked Up");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_PICKUP_3), "Disc Picked Up");
      builder.add(provider.getSoundKey(AmariteSoundEvents.DISC_REBOUND), "Disc Rebounds");
      builder.add(provider.getSoundKey(AmariteSoundEvents.MIRROR_EXTRACT), "Mirror Extracts Effects");
      builder.add(provider.getSoundKey(AmariteSoundEvents.MIRROR_USE), "Mirror Used");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SWORD_CHARGE), "Longsword Charged");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SWORD_BLOCK), "Longsword Absorbs Damage");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SWORD_DASH), "Longsword Dashes");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SWORD_ACCUMULATE), "Longsword Accumulates");
      builder.add(provider.getSoundKey(AmariteSoundEvents.MASK_OFFSET), "Mask Offsets");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SPARK_ACTIVATE), "Spark Activates");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SPARK_AMBIENT), "Spark Ambience");
      builder.add(provider.getSoundKey(AmariteSoundEvents.SPARK_DEACTIVATE), "Spark Deactivates");
      builder.add(provider.getSoundKey(AmariteSoundEvents.AMARITE_FORMS), "Amarite Forms");
      builder.add(provider.getSoundKey(AmariteSoundEvents.AMARITE_DECAYS), "Amarite Decays");
      builder.add(provider.getSoundKey(AmariteSoundEvents.PACIFICUS), "StarbornNebula - Pacificus");
   }

   @Override
   protected void generateBlockStateModels(MDataGen.MModelProvider provider, @NotNull BlockStateModelGenerator generator) {
      generator.registerAxisRotated(AmariteBlocks.AMETHYST_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
      generator.method_25650(AmariteBlocks.AMETHYST_BRICKS).method_33522(AMETHYST_BRICK);
      generator.method_25650(AmariteBlocks.CHISELED_AMETHYST).method_33522(CHISELED_AMETHYST);
      generator.method_25545(AmariteBlocks.YELLOW_CARNATION, AmariteBlocks.POTTED_YELLOW_CARNATION, class_4913.field_22840);
      generator.method_25540(AmariteBlocks.POTTED_YELLOW_CARNATION);
      generator.method_25621(AmariteBlocks.YELLOW_CARNATION_BOUQUET, class_4913.field_22840);
      generator.method_32229(AmariteBlocks.AMARITE_CLUSTER);
      generator.method_32229(AmariteBlocks.PARTIAL_AMARITE_BUD);
      generator.method_32229(AmariteBlocks.FRESH_AMARITE_BUD);
   }

   @Override
   protected void generateItemModels(MDataGen.MModelProvider provider, @NotNull ItemModelGenerator generator) {
      generator.register(AmariteItems.AMARITE_SHARD, class_4943.field_22938);
      generator.register(AmariteItems.WIN_COOKIE, class_4943.field_22938);
   }

   @Override
   protected void generateRecipes(MDataGen.MRecipeProvider provider, Consumer<RecipeJsonProvider> exporter) {
      class_2447.method_10437(AmariteItems.AMARITE_LONGSWORD)
         .method_10434('A', AmariteItems.AMARITE_SHARD)
         .method_10434('G', Items.field_8494)
         .method_10434('N', Items.field_22022)
         .method_10434('B', AmariteBlocks.AMARITE_BLOCK)
         .method_10439(" AA")
         .method_10439("GBA")
         .method_10439("NG ")
         .method_10429("shard", class_2446.conditionsFromItem(AmariteItems.AMARITE_SHARD))
         .method_10431(exporter);
      class_2447.method_10437(AmariteItems.AMARITE_DISC)
         .method_10434('L', Items.field_8745)
         .method_10434('G', Items.GOLD_INGOT)
         .method_10434('A', AmariteItems.AMARITE_SHARD)
         .method_10434('B', AmariteBlocks.AMARITE_BLOCK)
         .method_10439("BGA")
         .method_10439("G G")
         .method_10439("LGB")
         .method_10429("shard", class_2446.conditionsFromItem(AmariteItems.AMARITE_SHARD))
         .method_10431(exporter);
      class_2447.method_10437(AmariteItems.AMARITE_MIRROR)
         .method_10434('G', Items.GOLD_INGOT)
         .method_10434('N', Items.field_8397)
         .method_10434('B', Items.field_8494)
         .method_10434('A', AmariteBlocks.AMARITE_BLOCK)
         .method_10439(" GN")
         .method_10439("GAG")
         .method_10439("BG ")
         .method_10429("shard", class_2446.conditionsFromItem(AmariteItems.AMARITE_SHARD))
         .method_10431(exporter);
      class_2450.method_10448(AmariteItems.AMARITE_SHARD, 4)
         .method_10454(AmariteBlocks.AMARITE_BLOCK)
         .method_10442("block", class_2446.conditionsFromItem(AmariteBlocks.AMARITE_BLOCK))
         .method_10431(exporter);
      class_2446.method_33715(exporter, AmariteItems.WIN_COOKIE, Items.field_8423, 1);
      offerMaskRecipe(exporter, Items.field_8158, AmariteItems.AXOLOTL_MASK);
      offerMaskRecipe(exporter, Items.field_8245, AmariteItems.BUNNY_MASK);
      offerMaskRecipe(exporter, Ingredient.method_8106(class_3489.field_20344), AmariteItems.BUTTERFLY_MASK);
      offerMaskRecipe(exporter, Items.field_23843, AmariteItems.DEMON_MASK);
      offerMaskRecipe(exporter, Items.field_16998, AmariteItems.FOX_MASK);
      offerMaskRecipe(exporter, Items.field_8662, AmariteItems.GUARDIAN_MASK);
      offerMaskRecipe(exporter, Items.field_8614, AmariteItems.MOON_MASK);
      offerMaskRecipe(exporter, Items.field_8729, AmariteItems.ONI_MASK);
      offerMaskRecipe(exporter, Items.field_27063, AmariteItems.UNICORN_MASK);
      offerMaskRecipe(exporter, Items.field_37523, AmariteItems.WARDEN_MASK);
      offerMaskRecipe(exporter, AmariteItems.WIN_COOKIE, AmariteItems.WINSWEEP_MASK);
      offerMaskRecipe(exporter, AmariteItems.AMARITE_SHARD, AmariteItems.AMARITE_CROWN);
      class_2447.method_10436(AmariteBlocks.AMETHYST_BRICKS, 4)
         .method_10434('A', Items.field_27064)
         .method_10439("AA")
         .method_10439("AA")
         .method_10429("block", class_2446.conditionsFromItem(Items.field_27064))
         .method_10431(exporter);
      class_2447.method_10436(AmariteBlocks.AMETHYST_BRICK_WALL, 6)
         .method_10434('A', AmariteBlocks.AMETHYST_BRICKS)
         .method_10439("AAA")
         .method_10439("AAA")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.AMETHYST_BRICKS))
         .method_10431(exporter);
      class_2447.method_10436(AmariteBlocks.AMETHYST_BRICK_STAIRS, 6)
         .method_10434('A', AmariteBlocks.AMETHYST_BRICKS)
         .method_10439("A  ")
         .method_10439("AA ")
         .method_10439("AAA")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.AMETHYST_BRICKS))
         .method_10431(exporter);
      class_2447.method_10436(AmariteBlocks.AMETHYST_BRICK_SLAB, 6)
         .method_10434('A', AmariteBlocks.AMETHYST_BRICKS)
         .method_10439("AAA")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.AMETHYST_BRICKS))
         .method_10431(exporter);
      class_2447.method_10436(AmariteBlocks.AMETHYST_PILLAR, 2)
         .method_10434('A', AmariteBlocks.AMETHYST_BRICKS)
         .method_10439("A")
         .method_10439("A")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.AMETHYST_BRICKS))
         .method_10431(exporter);
      class_2447.method_10436(AmariteBlocks.CHISELED_AMETHYST, 4)
         .method_10434('A', AmariteBlocks.AMETHYST_BRICKS)
         .method_10439("AA")
         .method_10439("AA")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.AMETHYST_BRICKS))
         .method_10431(exporter);
      class_2447.method_10437(AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE)
         .method_10434('A', AmariteBlocks.CHISELED_AMETHYST)
         .method_10439("AA")
         .method_10429("block", class_2446.conditionsFromItem(AmariteBlocks.CHISELED_AMETHYST))
         .method_10431(exporter);
      class_2450.method_10447(AmariteBlocks.CHISELED_AMETHYST_BUTTON)
         .method_10454(AmariteBlocks.CHISELED_AMETHYST)
         .method_10442("block", class_2446.conditionsFromItem(AmariteBlocks.CHISELED_AMETHYST))
         .method_10431(exporter);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICKS, Items.field_27064, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_WALL, Items.field_27064, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_STAIRS, Items.field_27064, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_SLAB, Items.field_27064, 2);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_PILLAR, Items.field_27064, 1);
      class_2446.method_33715(exporter, AmariteBlocks.CHISELED_AMETHYST, Items.field_27064, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_WALL, AmariteBlocks.AMETHYST_BRICKS, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_STAIRS, AmariteBlocks.AMETHYST_BRICKS, 1);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_BRICK_SLAB, AmariteBlocks.AMETHYST_BRICKS, 2);
      class_2446.method_33715(exporter, AmariteBlocks.AMETHYST_PILLAR, AmariteBlocks.AMETHYST_BRICKS, 1);
      class_2446.method_33715(exporter, AmariteBlocks.CHISELED_AMETHYST, AmariteBlocks.AMETHYST_BRICKS, 1);
      class_2447.method_10437(AmariteBlocks.AMARITE_BLOCK)
         .method_10434('A', AmariteItems.AMARITE_SHARD)
         .method_10439("AA")
         .method_10439("AA")
         .method_10429("shard", class_2446.conditionsFromItem(AmariteItems.AMARITE_SHARD))
         .method_10431(exporter);
      class_2447.method_10437(AmariteBlocks.AMARITE_SPARK)
         .method_10434('A', AmariteItems.AMARITE_SHARD)
         .method_10434('G', Items.field_8801)
         .method_10434('B', AmariteItems.AMARITE_SHARD)
         .method_10439(" A ")
         .method_10439("AGA")
         .method_10439(" B ")
         .method_10429("shard", class_2446.conditionsFromItem(AmariteItems.AMARITE_SHARD))
         .method_10431(exporter);
      class_2450.method_10448(AmariteBlocks.YELLOW_CARNATION, 2)
         .method_10454(AmariteBlocks.YELLOW_CARNATION_BOUQUET)
         .method_10442("bouquet", class_2446.conditionsFromItem(AmariteBlocks.YELLOW_CARNATION_BOUQUET))
         .method_10431(exporter);
      class_2450.method_10448(Items.field_8192, 1)
         .method_10454(AmariteBlocks.YELLOW_CARNATION)
         .method_10442("carnation", class_2446.conditionsFromItem(AmariteBlocks.YELLOW_CARNATION))
         .method_10431(exporter);
   }

   private static void offerMaskRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible material, ItemConvertible output) {
      offerMaskRecipe(exporter, Ingredient.ofItems(new ItemConvertible[]{material}), output);
   }

   private static void offerMaskRecipe(Consumer<RecipeJsonProvider> exporter, Ingredient material, ItemConvertible output) {
      class_2447.method_10437(output)
         .method_10433('W', class_3489.field_15544)
         .method_10434('S', Items.field_8276)
         .method_10434('L', Items.field_8745)
         .method_10428('A', material)
         .method_10439(" W ")
         .method_10439("SAS")
         .method_10439(" L ")
         .method_10429("string", class_2446.conditionsFromItem(Items.field_8276))
         .method_10431(exporter);
   }

   @Override
   protected void generateBlockTags(@NotNull MDataGen.MBlockTagProvider provider) {
      provider.getOrCreateTagBuilder(class_3481.field_33715)
         .add(
            new Block[]{
               AmariteBlocks.AMETHYST_BRICKS,
               AmariteBlocks.AMETHYST_BRICK_WALL,
               AmariteBlocks.AMETHYST_BRICK_STAIRS,
               AmariteBlocks.AMETHYST_BRICK_SLAB,
               AmariteBlocks.AMETHYST_PILLAR,
               AmariteBlocks.CHISELED_AMETHYST,
               AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE,
               AmariteBlocks.CHISELED_AMETHYST_BUTTON,
               AmariteBlocks.AMARITE_BLOCK,
               AmariteBlocks.BUDDING_AMARITE,
               AmariteBlocks.AMARITE_CLUSTER,
               AmariteBlocks.PARTIAL_AMARITE_BUD,
               AmariteBlocks.FRESH_AMARITE_BUD,
               AmariteBlocks.AMARITE_SPARK,
               AmariteBlocks.POTTED_YELLOW_CARNATION
            }
         );
   }

   @Override
   protected void generateItemTags(@NotNull MDataGen.MItemTagProvider provider) {
      provider.getOrCreateTagBuilder(class_3489.field_15543).add(AmariteBlocks.YELLOW_CARNATION.asItem());
   }

   public abstract static class MirrorCooldownProvider implements class_2405 {
      protected final FabricDataGenerator dataGenerator;

      protected MirrorCooldownProvider(FabricDataGenerator dataGenerator) {
         this.dataGenerator = dataGenerator;
      }

      public abstract void generateCooldowns(BiConsumer<Identifier, Integer> var1);

      public void method_10319(class_7403 writer) throws IOException {
         HashMap<Identifier, Integer> cooldowns = new HashMap<>();
         this.generateCooldowns(cooldowns::put);

         for (Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
            Identifier id = entry.getKey();
            JsonObject cooldownEntry = new JsonObject();
            cooldownEntry.addProperty("cooldown", entry.getValue());
            class_2405.method_10320(
               writer,
               cooldownEntry,
               this.dataGenerator.method_10313().resolve("data/%s/amarite_mirror/%s.json".formatted(id.getNamespace(), id.getPath()))
            );
         }
      }

      public String method_10321() {
         return "Mirror Cooldowns";
      }
   }*/
}
