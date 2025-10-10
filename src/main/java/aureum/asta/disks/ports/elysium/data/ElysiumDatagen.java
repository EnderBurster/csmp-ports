package aureum.asta.disks.ports.elysium.data;

import com.mojang.datafixers.util.Pair;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmour;
import aureum.asta.disks.ports.elysium.armour.ElysiumUpgradeRecipe;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import aureum.asta.disks.ports.elysium.machine.electrode.ElectrodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.Advancement.Builder;
import net.minecraft.advancement.criterion.InventoryChangedCriterion.Conditions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.client.VariantSettings.Rotation;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.function.TriFunction;

public class ElysiumDatagen {
   /*public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
      fabricDataGenerator.addProvider(ElysiumModels::new);
      fabricDataGenerator.addProvider(g -> new ElysiumBlockTags(g, Registry.BLOCK));
      fabricDataGenerator.addProvider(g -> new ElysiumItemTags(g, Registry.ITEM));
      fabricDataGenerator.addProvider(ElysiumRecipes::new);
      fabricDataGenerator.addProvider(ElysiumLang::new);
      fabricDataGenerator.addProvider(ElysiumLootTables::new);
      fabricDataGenerator.addProvider(ElysiumAdvancements::new);
   }

   private static class ElysiumAdvancements extends FabricAdvancementProvider {
      protected ElysiumAdvancements(FabricDataGenerator dataGenerator) {
         super(dataGenerator);
      }

      public void generateAdvancement(Consumer<Advancement> consumer) {
         Advancement armorAdvancement = Builder.create()
            .criterion(
               "elysium_armor",
               Conditions.items(
                  new ItemConvertible[]{
                     ElysiumArmour.ELYSIUM_HELMET, ElysiumArmour.ELYSIUM_CHESTPLATE, ElysiumArmour.ELYSIUM_LEGGINGS, ElysiumArmour.ELYSIUM_BOOTS
                  }
               )
            )
            .display(
               ElysiumArmour.ELYSIUM_CHESTPLATE,
               Text.translatable("advancements.elysium.elysium_armor.title"),
               Text.translatable("advancements.elysium.elysium_armor.description"),
               null,
               AdvancementFrame.CHALLENGE,
               true,
               true,
               false
            )
            .parent(new Advancement(new Identifier("minecraft:nether/obtain_ancient_debris"), null, null, AdvancementRewards.NONE, Map.of(), new String[0][]))
            .rewards(AdvancementRewards.Builder.experience(100))
            .requirements(new String[][]{{"elysium_armor"}})
            .build(consumer, Elysium.id("elysium_armor").toString());
         Advancement cheirosiphonAdvancement = Builder.create()
            .criterion("cheirosiphon", Conditions.items(new ItemConvertible[]{Elysium.CHEIROSIPHON}))
            .display(
               Elysium.CHEIROSIPHON,
               Text.translatable("advancements.elysium.cheirosiphon.title"),
               Text.translatable("advancements.elysium.cheirosiphon.description"),
               null,
               AdvancementFrame.TASK,
               true,
               true,
               false
            )
            .parent(new Advancement(new Identifier("minecraft:adventure/ol_betsy"), null, null, AdvancementRewards.NONE, Map.of(), new String[0][]))
            .requirements(new String[][]{{"cheirosiphon"}})
            .build(consumer, Elysium.id("cheirosiphon").toString());
         Advancement ghastAdvancement = Builder.create()
            .criterion(
               "killed_ghast",
               net.minecraft.advancement.criterion.OnKilledCriterion.Conditions.createPlayerKilledEntity(
                  net.minecraft.predicate.entity.EntityPredicate.Builder.create().type(EntityType.GHAST),
                  net.minecraft.predicate.entity.DamageSourcePredicate.Builder.create()
                     .projectile(true)
                     .directEntity(net.minecraft.predicate.entity.EntityPredicate.Builder.create().type(EntityTypePredicate.create(Elysium.GHASTLY_FIREBALL)))
               )
            )
            .display(
               Elysium.CHEIROSIPHON,
               Text.translatable("advancements.elysium.cheirosiphon_kill_ghast.title"),
               Text.translatable("advancements.elysium.cheirosiphon_kill_ghast.description"),
               null,
               AdvancementFrame.CHALLENGE,
               true,
               true,
               false
            )
            .parent(cheirosiphonAdvancement)
            .rewards(AdvancementRewards.Builder.experience(50))
            .requirements(new String[][]{{"killed_ghast"}})
            .build(consumer, Elysium.id("cheirosiphon_kill_ghast").toString());
      }
   }

   private static class ElysiumBlockTags extends FabricTagProvider<Block> {
      public ElysiumBlockTags(FabricDataGenerator dataGenerator, Registry<Block> registry) {
         super(dataGenerator, registry);
      }

      protected void generateTags() {
         this.getOrCreateTagBuilder(Elysium.ELYSIUM_FIRE_BASE_BLOCKS).add((Block)Elysium.ELYSIUM_BLOCK.getFirst());
         this.getOrCreateTagBuilder(ElysiumMachines.LIGHTNING_RODS).add(Blocks.LIGHTNING_ROD).addOptionalTag(new Identifier("unvotedandshelved", "lightning_rods"));
         this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
            .add(
               new Block[]{
                  (Block)Elysium.ELYSIUM_BLOCK.getFirst(),
                  (Block)Elysium.BRILLIANT_SELFLIT_REDSTONE_LAMP.getFirst(),
                  (Block)Elysium.SELFLIT_REDSTONE_LAMP.getFirst(),
                  (Block)Elysium.DIM_SELFLIT_REDSTONE_LAMP.getFirst(),
                  (Block)ElysiumMachines.ELYSIUM_PRISM.getFirst(),
                  (Block)ElysiumMachines.REPULSOR.getFirst(),
                  (Block)ElysiumMachines.GRAVITATOR.getFirst(),
                  (Block)ElysiumMachines.ELECTRODE.getFirst()
               }
            );
         this.getOrCreateTagBuilder(BlockTags.FIRE).add(Elysium.ELYSIUM_FIRE);
      }
   }

   private static class ElysiumItemTags extends FabricTagProvider<Item> {
      public ElysiumItemTags(FabricDataGenerator dataGenerator, Registry<Item> registry) {
         super(dataGenerator, registry);
      }

      protected void generateTags() {
         this.getOrCreateTagBuilder(ElysiumArmour.ELYSIUM_ARMOUR_TAG)
            .add(new Item[]{ElysiumArmour.ELYSIUM_HELMET, ElysiumArmour.ELYSIUM_CHESTPLATE, ElysiumArmour.ELYSIUM_LEGGINGS, ElysiumArmour.ELYSIUM_BOOTS});
         this.getOrCreateTagBuilder(ElysiumArmour.EXPERIMENTAL_ELYSIUM_ARMOUR_TAG)
            .add(
               new Item[]{
                  ElysiumArmour.EXPERIMENTAL_ELYSIUM_HELMET,
                  ElysiumArmour.EXPERIMENTAL_ELYSIUM_CHESTPLATE,
                  ElysiumArmour.EXPERIMENTAL_ELYSIUM_LEGGINGS,
                  ElysiumArmour.EXPERIMENTAL_ELYSIUM_BOOTS
               }
            );
      }
   }

   private static class ElysiumLang extends FabricLanguageProvider {
      protected ElysiumLang(FabricDataGenerator dataGenerator) {
         super(dataGenerator);
      }

      public void generateTranslations(TranslationBuilder translationBuilder) {
         translationBuilder.add(Elysium.ELYSIUM_TAB, "Elysium");
         translationBuilder.add(Elysium.ELYSIUM_INGOT, "Elysium Ingot");
         translationBuilder.add((Block)Elysium.ELYSIUM_BLOCK.getFirst(), "Elysium Block");
         translationBuilder.add((Block)Elysium.SELFLIT_REDSTONE_LAMP.getFirst(), "Selflit Redstone Lamp");
         translationBuilder.add((Block)Elysium.BRILLIANT_SELFLIT_REDSTONE_LAMP.getFirst(), "Brilliant Selflit Redstone Lamp");
         translationBuilder.add((Block)Elysium.DIM_SELFLIT_REDSTONE_LAMP.getFirst(), "Dim Selflit Redstone Lamp");
         translationBuilder.add((Block)ElysiumMachines.ELYSIUM_PRISM.getFirst(), "Elysium Prism");
         translationBuilder.add(Elysium.ELYSIUM_FIRE, "Elysium Fire");
         translationBuilder.add(Elysium.CHEIROSIPHON, "Cheirosiphon");
         translationBuilder.add(Elysium.CHEIROSIPHON_FLAME, "Cheirosiphon Flame");
         translationBuilder.add(Elysium.GHASTLY_FIREBALL_ITEM, "Cheirosiphon Ghastly Fireball");
         translationBuilder.add(Elysium.GHASTLY_FIREBALL, "Cheirosiphon Ghastly Fireball");
         translationBuilder.add((Block)ElysiumMachines.GRAVITATOR.getFirst(), "Elysium Gravitator");
         translationBuilder.add((Block)ElysiumMachines.REPULSOR.getFirst(), "Elysium Repulsor");
         translationBuilder.add((Block)ElysiumMachines.ELECTRODE.getFirst(), "Electrode");
         translationBuilder.add(ElysiumArmour.ELYSIUM_HELMET, "Elysium Helmet");
         translationBuilder.add(ElysiumArmour.ELYSIUM_CHESTPLATE, "Elysium Chestplate");
         translationBuilder.add(ElysiumArmour.ELYSIUM_LEGGINGS, "Elysium Leggings");
         translationBuilder.add(ElysiumArmour.ELYSIUM_BOOTS, "Elysium Boots");
         translationBuilder.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_HELMET, "Experimental Elysium Helmet");
         translationBuilder.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_CHESTPLATE, "Experimental Elysium Chestplate");
         translationBuilder.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_LEGGINGS, "Experimental Elysium Leggings");
         translationBuilder.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_BOOTS, "Experimental Elysium Boots");
         translationBuilder.add(ElysiumArmour.ELYSIUM_VULNERABILITY, "Voltage");
         translationBuilder.add("container.elysium.electrode", "Electrode");
         translationBuilder.add("death.attack." + ElysiumDamageSources.PRISM_BEAM.getName(), "%1$s was beamed up");
         translationBuilder.add("death.attack." + ElysiumDamageSources.PRISM_BEAM.getName() + ".player", "%1$s was beamed up whilst fighting %2$s");
         translationBuilder.add("death.attack.cheirosiphon", "%1$s was siphoned away");
         translationBuilder.add("death.attack.cheirosiphon.player", "%1$s was siphoned away by %2$s");
         translationBuilder.add("death.attack.cheirosiphon.item", "%1$s was siphoned away by %2$s wielding %3$s");
         translationBuilder.add("death.attack." + ElysiumDamageSources.CHEIROSIPHON_OVERHEAT.getName(), "%1$s didn't expect their Cheirosiphon to get that hot");
         translationBuilder.add(
            "death.attack." + ElysiumDamageSources.CHEIROSIPHON_OVERHEAT.getName() + ".player",
            "%1$s didn't expect their Cheirosiphon to get that hot whilst fighting %2$s"
         );
         translationBuilder.add("death.attack.cheirosiphon_blast", "%1$s was blown away");
         translationBuilder.add("death.attack.cheirosiphon_blast.player", "%1$s was blown away by %2$s");
         translationBuilder.add("death.attack.cheirosiphon_blast.item", "%1$s was blown away by %2$s wielding %3$s");
         translationBuilder.add("death.attack." + ElysiumDamageSources.ELECTRODE.getName(), "%1$s was too conductive");
         translationBuilder.add("death.attack." + ElysiumDamageSources.ELECTRODE.getName() + ".player", "%1$s was too conductive whilst fighting %2$s");
         translationBuilder.add("death.attack.elysium_armour", "%1$s got zapped");
         translationBuilder.add("death.attack.elysium_armour.player", "%1$s got zapped by %2$s");
         translationBuilder.add("death.attack.elysium_armour.item", "%1$s got zapped by %2$s wielding %3$s");
         translationBuilder.add("death.attack." + ElysiumDamageSources.VULNERABILITY_WASH_AWAY.getName(), "%1$s grounded themself");
         translationBuilder.add(
            "death.attack." + ElysiumDamageSources.VULNERABILITY_WASH_AWAY.getName() + ".player", "%1$s grounded themself whilst fighting %2$s"
         );
         translationBuilder.add("death.attack.cheirosiphon_ghastly_fireball", "%1$s was blasted");
         translationBuilder.add("death.attack.cheirosiphon_ghastly_fireball.player", "%1$s was blasted by %2$s");
         translationBuilder.add("death.attack.cheirosiphon_ghastly_fireball.item", "%1$s was blasted by %2$s wielding %3$s");
         translationBuilder.add("advancements.elysium.elysium_armor.title", "Cover Me in Electromagnets");
         translationBuilder.add("advancements.elysium.elysium_armor.description", "Obtain a full set of Elysium armor");
         translationBuilder.add("advancements.elysium.cheirosiphon.title", "Mmph Hudda mph!");
         translationBuilder.add("advancements.elysium.cheirosiphon.description", "Obtain a Cheirosiphon");
         translationBuilder.add("advancements.elysium.cheirosiphon_kill_ghast.title", "Ghaster Blaster");
         translationBuilder.add("advancements.elysium.cheirosiphon_kill_ghast.description", "Kill a Ghast with a Cheirosiphon enchanted with Ghastly");
         translationBuilder.add(Elysium.JET_ENCHANTMENT, "Jet");
         translationBuilder.add(Elysium.JET_ENCHANTMENT.getTranslationKey() + ".desc", "Concentrates the Cheirosiphon's flames.");
         translationBuilder.add(Elysium.GHASTLY_ENCHANTMENT, "Ghastly");
         translationBuilder.add(Elysium.GHASTLY_ENCHANTMENT.getTranslationKey() + ".desc", "Replaces the Cheirosiphon's magnetic blast with a fireball.");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.ELYSIUM_FALL), "Elysium block lands");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.ELYSIUM_PRISM_LOOP), "Elysium Prism shimmers");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.ELECTRODE_ZAP), "Electrode zaps");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.CHEIROSIPHON_DEACTIVATE), "Cheirosiphon deactivates");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.CHEIROSIPHON_LOOP), "Cheirosiphon burns");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.CHEIROSIPHON_BLAST), "Cheirosiphon blasts");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.CHEIROSIPHON_GHASTLY_BLAST), "Ghastly ball fired");
         translationBuilder.add(ElysiumSounds.getSubtitleKey(ElysiumSounds.PARRY), "Ghastly ball parried");
      }
   }

   private static class ElysiumLootTables extends FabricBlockLootTableProvider {
      protected ElysiumLootTables(FabricDataGenerator dataGenerator) {
         super(dataGenerator);
      }

      protected void generateBlockLootTables() {
         this.addDrop((Block)ElysiumMachines.ELYSIUM_PRISM.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)ElysiumMachines.REPULSOR.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)ElysiumMachines.GRAVITATOR.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)ElysiumMachines.ELECTRODE.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)Elysium.ELYSIUM_BLOCK.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)Elysium.BRILLIANT_SELFLIT_REDSTONE_LAMP.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)Elysium.SELFLIT_REDSTONE_LAMP.getFirst(), BlockLootTableGenerator::drops);
         this.addDrop((Block)Elysium.DIM_SELFLIT_REDSTONE_LAMP.getFirst(), BlockLootTableGenerator::drops);
      }
   }

   private static class ElysiumModels extends FabricModelProvider {
      private static final Model BUILTIN_TEMPLATE = new Model(Optional.of(new Identifier("minecraft:builtin/entity")), Optional.empty(), new TextureKey[0]);

      public ElysiumModels(FabricDataGenerator dataGenerator) {
         super(dataGenerator);
      }

      public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
         this.createElysiumFire(blockStateModelGenerator);
         blockStateModelGenerator.registerSimpleCubeAll((Block)Elysium.ELYSIUM_BLOCK.getFirst());
         blockStateModelGenerator.registerSimpleCubeAll((Block)Elysium.SELFLIT_REDSTONE_LAMP.getFirst());
         blockStateModelGenerator.registerSimpleCubeAll((Block)Elysium.BRILLIANT_SELFLIT_REDSTONE_LAMP.getFirst());
         blockStateModelGenerator.registerSimpleCubeAll((Block)Elysium.DIM_SELFLIT_REDSTONE_LAMP.getFirst());
         this.createDirectionalBlock(blockStateModelGenerator, (Block)ElysiumMachines.GRAVITATOR.getFirst());
         this.createDirectionalBlock(blockStateModelGenerator, (Block)ElysiumMachines.REPULSOR.getFirst());
         this.createElysiumPrism(blockStateModelGenerator, (Block)ElysiumMachines.ELYSIUM_PRISM.getFirst());
         this.createElectrode(blockStateModelGenerator, (Block)ElysiumMachines.ELECTRODE.getFirst());
      }

      private void createElysiumFire(BlockStateModelGenerator generator) {
         List<Identifier> floorModels = generator.getFireFloorModels(Elysium.ELYSIUM_FIRE);
         List<Identifier> sideModels = generator.getFireSideModels(Elysium.ELYSIUM_FIRE);
         generator.blockStateCollector
            .accept(
               MultipartBlockStateSupplier.create(Elysium.ELYSIUM_FIRE)
                  .with(BlockStateModelGenerator.buildBlockStateVariants(floorModels, variant -> variant))
                  .with(BlockStateModelGenerator.buildBlockStateVariants(sideModels, variant -> variant))
                  .with(BlockStateModelGenerator.buildBlockStateVariants(sideModels, variant -> variant.put(VariantSettings.Y, Rotation.R90)))
                  .with(BlockStateModelGenerator.buildBlockStateVariants(sideModels, variant -> variant.put(VariantSettings.Y, Rotation.R180)))
                  .with(BlockStateModelGenerator.buildBlockStateVariants(sideModels, variant -> variant.put(VariantSettings.Y, Rotation.R270)))
            );
      }

      private void createElysiumPrism(BlockStateModelGenerator generator, Block block) {
         Identifier top = TextureMap.getSubId(block, "_top");
         Identifier side = TextureMap.getSubId(block, "_side");
         TextureMap textureMapping = new TextureMap()
            .put(TextureKey.DOWN, side)
            .put(TextureKey.WEST, side)
            .put(TextureKey.EAST, side)
            .put(TextureKey.PARTICLE, top)
            .put(TextureKey.NORTH, top)
            .put(TextureKey.SOUTH, side)
            .put(TextureKey.UP, side);
         Identifier resourceLocation5 = Models.CUBE_DIRECTIONAL.upload(block, textureMapping, generator.modelCollector);
         generator.blockStateCollector
            .accept(
               VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, resourceLocation5))
                  .coordinate(createPowerDispatch())
                  .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
            );
         generator.registerParentedItemModel(block, resourceLocation5);
      }

      private void createElectrode(BlockStateModelGenerator generator, Block block) {
         Identifier back = TextureMap.getSubId(block, "_back");
         TriFunction<Boolean, Integer, Boolean, TextureMap> textureFunc = (hasPower, charges, hasRod) -> new TextureMap()
               .put(TextureKey.TOP, TextureMap.getSubId(block, "_front_" + charges))
               .put(TextureKey.BOTTOM, back)
               .put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_" + (hasRod ? "withrod" : (hasPower ? "powered" : "unpowered"))));
         Map<String, Identifier> models = new HashMap<>();
         TriFunction<Integer, Integer, Boolean, Identifier> createModel = (power, charges, hasRod) -> {
            boolean isPowered = power > 0;
            String key = "_" + (isPowered ? "powered" : "unpowered") + "_" + charges + "_" + (hasRod ? "withrod" : "norod");
            if (models.containsKey(key)) {
               return models.get(key);
            } else {
               Identifier modelId = Models.CUBE_BOTTOM_TOP
                  .upload(block, key, (TextureMap)textureFunc.apply(isPowered, charges, hasRod), generator.modelCollector);
               models.put(key, modelId);
               return modelId;
            }
         };
         generator.blockStateCollector
            .accept(
               VariantsBlockStateSupplier.create(block)
                  .coordinate(createAlternateFacingDispatch())
                  .coordinate(
                     BlockStateVariantMap.create(ElysiumMachines.ELYSIUM_POWER, ElectrodeBlock.CHARGES, ElectrodeBlock.HAS_ROD)
                        .register((i, c, rod) -> BlockStateVariant.create().put(VariantSettings.MODEL, (Identifier)createModel.apply(i, c, rod)))
                  )
            );
         generator.registerParentedItemModel(block, (Identifier)createModel.apply(0, 0, false));
      }

      private static BlockStateVariantMap createPowerDispatch() {
         return BlockStateVariantMap.create(ElysiumMachines.ELYSIUM_POWER)
            .register(0, BlockStateVariant.create())
            .register(1, BlockStateVariant.create())
            .register(2, BlockStateVariant.create())
            .register(3, BlockStateVariant.create())
            .register(4, BlockStateVariant.create());
      }

      public static BlockStateVariantMap createAlternateFacingDispatch() {
         return BlockStateVariantMap.create(Properties.FACING)
            .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, Rotation.R180))
            .register(Direction.UP, BlockStateVariant.create())
            .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, Rotation.R90))
            .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, Rotation.R90).put(VariantSettings.Y, Rotation.R180))
            .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, Rotation.R90).put(VariantSettings.Y, Rotation.R270))
            .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, Rotation.R90).put(VariantSettings.Y, Rotation.R90));
      }

      private void createDirectionalBlock(BlockStateModelGenerator generator, Block block) {
         Identifier top = Elysium.id("block/gravitator_top");
         Identifier bottom = Elysium.id("block/gravitator_bottom");
         Identifier eastSide = Elysium.id("block/gravitator_east_side");
         Identifier westSide = Elysium.id("block/gravitator_west_side");
         Identifier back = Elysium.id("block/gravitator_back");
         Identifier verticalBack = Elysium.id("block/electrode_back");
         List<Pair<Integer, Identifier>> front = IntStream.of(0, 1, 2, 3).mapToObj(i -> Pair.of(i, TextureMap.getSubId(block, "_front_" + i))).toList();
         List<Pair<Integer, TextureMap>> horizTextures = front.stream()
            .map(
               p -> p.mapSecond(
                     f -> new TextureMap()
                           .put(TextureKey.UP, top)
                           .put(TextureKey.DOWN, bottom)
                           .put(TextureKey.EAST, eastSide)
                           .put(TextureKey.WEST, westSide)
                           .put(TextureKey.NORTH, f)
                           .put(TextureKey.SOUTH, back)
                           .put(TextureKey.PARTICLE, f)
                  )
            )
            .toList();
         List<Pair<Integer, TextureMap>> vertTextures = front.stream()
            .map(
               p -> p.mapSecond(
                     f -> new TextureMap()
                           .put(TextureKey.UP, f)
                           .put(TextureKey.DOWN, verticalBack)
                           .put(TextureKey.EAST, bottom)
                           .put(TextureKey.WEST, bottom)
                           .put(TextureKey.NORTH, bottom)
                           .put(TextureKey.SOUTH, bottom)
                           .put(TextureKey.PARTICLE, f)
                  )
            )
            .toList();
         ArrayList<Identifier> horizModels = new ArrayList<>(
            horizTextures.stream()
               .map(p -> Models.CUBE_DIRECTIONAL.upload(block, "_" + p.getFirst(), (TextureMap)p.getSecond(), generator.modelCollector))
               .toList()
         );
         ArrayList<Identifier> vertModels = new ArrayList<>(
            vertTextures.stream().map(p -> Models.CUBE.upload(block, "_vert_" + p.getFirst(), (TextureMap)p.getSecond(), generator.modelCollector)).toList()
         );
         horizModels.add(horizModels.get(horizModels.size() - 1));
         vertModels.add(vertModels.get(vertModels.size() - 1));
         generator.blockStateCollector
            .accept(
               VariantsBlockStateSupplier.create(block)
                  .coordinate(BlockStateVariantMap.create(Properties.FACING, ElysiumMachines.ELYSIUM_POWER).register((dir, i) -> {
                     return switch (dir) {
                        case DOWN -> BlockStateVariant.create().put(VariantSettings.MODEL, vertModels.get(i)).put(VariantSettings.X, Rotation.R180);
                        case UP -> BlockStateVariant.create().put(VariantSettings.MODEL, vertModels.get(i));
                        case NORTH -> BlockStateVariant.create().put(VariantSettings.MODEL, horizModels.get(i));
                        case EAST -> BlockStateVariant.create().put(VariantSettings.MODEL, horizModels.get(i)).put(VariantSettings.Y, Rotation.R90);
                        case SOUTH -> BlockStateVariant.create().put(VariantSettings.MODEL, horizModels.get(i)).put(VariantSettings.Y, Rotation.R180);
                        case WEST -> BlockStateVariant.create().put(VariantSettings.MODEL, horizModels.get(i)).put(VariantSettings.Y, Rotation.R270);
                        default -> throw new IncompatibleClassChangeError();
                     };
                  }))
            );
         generator.registerParentedItemModel(block, horizModels.get(0));
      }

      public void generateItemModels(ItemModelGenerator itemModelGenerator) {
         itemModelGenerator.register(Elysium.CHEIROSIPHON, "_gui", Models.HANDHELD);
         itemModelGenerator.register(Elysium.GHASTLY_FIREBALL_ITEM, Models.GENERATED);
         itemModelGenerator.register(Elysium.ELYSIUM_INGOT, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.ELYSIUM_HELMET, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.ELYSIUM_CHESTPLATE, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.ELYSIUM_LEGGINGS, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.ELYSIUM_BOOTS, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.EXPERIMENTAL_ELYSIUM_HELMET, ElysiumArmour.ELYSIUM_HELMET, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.EXPERIMENTAL_ELYSIUM_CHESTPLATE, ElysiumArmour.ELYSIUM_CHESTPLATE, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.EXPERIMENTAL_ELYSIUM_LEGGINGS, ElysiumArmour.ELYSIUM_LEGGINGS, Models.GENERATED);
         itemModelGenerator.register(ElysiumArmour.EXPERIMENTAL_ELYSIUM_BOOTS, ElysiumArmour.ELYSIUM_BOOTS, Models.GENERATED);
      }
   }

   private static class ElysiumRecipes extends FabricRecipeProvider {
      public ElysiumRecipes(FabricDataGenerator dataGenerator) {
         super(dataGenerator);
      }

      protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
         ShapelessRecipeJsonBuilder.create(Elysium.ELYSIUM_INGOT)
            .input(Items.NETHERITE_SCRAP)
            .input(Items.COPPER_INGOT)
            .criterion("has_netherite_scrap", conditionsFromItem(Items.NETHERITE_SCRAP))
            .offerTo(exporter);
         ShapedRecipeJsonBuilder.create((ItemConvertible)Elysium.ELYSIUM_BLOCK.getSecond())
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', Elysium.ELYSIUM_INGOT)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create(Elysium.ELYSIUM_INGOT, 9)
            .input((ItemConvertible)Elysium.ELYSIUM_BLOCK.getSecond())
            .criterion("has_elysium_block", conditionsFromItem((ItemConvertible)Elysium.ELYSIUM_BLOCK.getFirst()))
            .offerTo(exporter, Elysium.id("elysium_ingots_from_block"));
         ShapelessRecipeJsonBuilder.create((ItemConvertible)Elysium.SELFLIT_REDSTONE_LAMP.getSecond())
            .input(Items.REDSTONE_LAMP)
            .input(Items.REDSTONE_TORCH)
            .criterion("has_redstone_lamp", conditionsFromItem(Items.REDSTONE_LAMP))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create((ItemConvertible)Elysium.BRILLIANT_SELFLIT_REDSTONE_LAMP.getSecond())
            .input((ItemConvertible)Elysium.SELFLIT_REDSTONE_LAMP.getSecond())
            .input(Items.GLOWSTONE_DUST)
            .criterion("has_redstone_lamp", conditionsFromItem(Items.REDSTONE_LAMP))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create((ItemConvertible)Elysium.DIM_SELFLIT_REDSTONE_LAMP.getSecond())
            .input((ItemConvertible)Elysium.SELFLIT_REDSTONE_LAMP.getSecond())
            .input(Items.TINTED_GLASS)
            .criterion("has_redstone_lamp", conditionsFromItem(Items.REDSTONE_LAMP))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create(ElysiumArmour.EXPERIMENTAL_ELYSIUM_HELMET)
            .input(Items.ORANGE_DYE)
            .input(Items.IRON_HELMET)
            .criterion("has_orange_dye", conditionsFromItem(Items.ORANGE_DYE))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create(ElysiumArmour.EXPERIMENTAL_ELYSIUM_CHESTPLATE)
            .input(Items.ORANGE_DYE)
            .input(Items.IRON_CHESTPLATE)
            .criterion("has_orange_dye", conditionsFromItem(Items.ORANGE_DYE))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create(ElysiumArmour.EXPERIMENTAL_ELYSIUM_LEGGINGS)
            .input(Items.ORANGE_DYE)
            .input(Items.IRON_LEGGINGS)
            .criterion("has_orange_dye", conditionsFromItem(Items.ORANGE_DYE))
            .offerTo(exporter);
         ShapelessRecipeJsonBuilder.create(ElysiumArmour.EXPERIMENTAL_ELYSIUM_BOOTS)
            .input(Items.ORANGE_DYE)
            .input(Items.IRON_BOOTS)
            .criterion("has_orange_dye", conditionsFromItem(Items.ORANGE_DYE))
            .offerTo(exporter);
         ElysiumUpgradeRecipe.Builder.smithing(
               Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND_HELMET}),
               Ingredient.ofItems(new ItemConvertible[]{Elysium.ELYSIUM_INGOT}),
               ElysiumArmour.ELYSIUM_HELMET,
               4
            )
            .unlocks("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .save(exporter, Elysium.id("elysium_helmet"));
         ElysiumUpgradeRecipe.Builder.smithing(
               Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND_CHESTPLATE}),
               Ingredient.ofItems(new ItemConvertible[]{Elysium.ELYSIUM_INGOT}),
               ElysiumArmour.ELYSIUM_CHESTPLATE,
               4
            )
            .unlocks("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .save(exporter, Elysium.id("elysium_chestplate"));
         ElysiumUpgradeRecipe.Builder.smithing(
               Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND_LEGGINGS}),
               Ingredient.ofItems(new ItemConvertible[]{Elysium.ELYSIUM_INGOT}),
               ElysiumArmour.ELYSIUM_LEGGINGS,
               4
            )
            .unlocks("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .save(exporter, Elysium.id("elysium_leggings"));
         ElysiumUpgradeRecipe.Builder.smithing(
               Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND_BOOTS}),
               Ingredient.ofItems(new ItemConvertible[]{Elysium.ELYSIUM_INGOT}),
               ElysiumArmour.ELYSIUM_BOOTS,
               4
            )
            .unlocks("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .save(exporter, Elysium.id("elysium_boots"));
         ShapedRecipeJsonBuilder.create((ItemConvertible)ElysiumMachines.REPULSOR.getSecond())
            .pattern("###")
            .pattern("#X#")
            .pattern("QRE")
            .input('#', ItemTags.PLANKS)
            .input('X', Items.REPEATER)
            .input('Q', Items.QUARTZ)
            .input('R', Items.REDSTONE)
            .input('E', Elysium.ELYSIUM_INGOT)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
         ShapedRecipeJsonBuilder.create((ItemConvertible)ElysiumMachines.GRAVITATOR.getSecond())
            .pattern("###")
            .pattern("#X#")
            .pattern("QRE")
            .input('#', ItemTags.PLANKS)
            .input('X', Items.COMPARATOR)
            .input('Q', Items.QUARTZ)
            .input('R', Items.REDSTONE)
            .input('E', Elysium.ELYSIUM_INGOT)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
         ShapedRecipeJsonBuilder.create((ItemConvertible)ElysiumMachines.ELECTRODE.getSecond())
            .pattern("CCC")
            .pattern("CQC")
            .pattern("EEE")
            .input('C', Items.COPPER_INGOT)
            .input('Q', Items.QUARTZ)
            .input('E', Elysium.ELYSIUM_INGOT)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
         ShapedRecipeJsonBuilder.create((ItemConvertible)ElysiumMachines.ELYSIUM_PRISM.getSecond())
            .pattern("EGE")
            .pattern("GAG")
            .pattern("EGE")
            .input('G', Items.TINTED_GLASS)
            .input('A', Items.AMETHYST_SHARD)
            .input('E', Elysium.ELYSIUM_INGOT)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
         ShapedRecipeJsonBuilder.create(Elysium.CHEIROSIPHON)
            .pattern("B E")
            .pattern(" R ")
            .pattern("H L")
            .input('B', Items.BUCKET)
            .input('E', Elysium.ELYSIUM_INGOT)
            .input('R', (ItemConvertible)ElysiumMachines.REPULSOR.getSecond())
            .input('H', Items.HOPPER)
            .input('L', Items.LEVER)
            .criterion("has_elysium_ingot", conditionsFromItem(Elysium.ELYSIUM_INGOT))
            .offerTo(exporter);
      }
   }*/
}
