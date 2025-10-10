package aureum.asta.disks.ports.amarite.mialib.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;

public abstract class MDataGen implements DataGeneratorEntrypoint {
   /*public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
      generator.addProvider(gen -> new MDataGen.MBlockLootTableProvider(this, gen));
      generator.addProvider(gen -> new MDataGen.MLanguageProvider(this, gen));
      generator.addProvider(gen -> new MDataGen.MModelProvider(this, gen));
      generator.addProvider(gen -> new MDataGen.MRecipeProvider(this, gen));
      generator.addProvider(gen -> new MDataGen.MBlockTagProvider(this, gen));
      generator.addProvider(gen -> new MDataGen.MItemTagProvider(this, gen));
   }

   protected void generateBlockLootTables(MDataGen.MBlockLootTableProvider provider) {
   }

   protected void generateTranslations(MDataGen.MLanguageProvider provider, TranslationBuilder builder) {
   }

   protected void generateBlockStateModels(MDataGen.MModelProvider provider, BlockStateModelGenerator generator) {
   }

   protected void generateItemModels(MDataGen.MModelProvider provider, ItemModelGenerator generator) {
   }

   protected void generateRecipes(MDataGen.MRecipeProvider provider, Consumer<RecipeJsonProvider> exporter) {
   }

   protected void generateBlockTags(MDataGen.MBlockTagProvider provider) {
   }

   protected void generateItemTags(MDataGen.MItemTagProvider provider) {
   }

   protected static class MBlockLootTableProvider extends FabricBlockLootTableProvider {
      private final MDataGen dataGen;

      public MBlockLootTableProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator);
         this.dataGen = gen;
      }

      protected void generateBlockLootTables() {
         this.dataGen.generateBlockLootTables(this);
      }

      public static Builder makeItemWithRange(ItemConvertible item, int min, int max) {
         return LootTable.builder()
            .pool(
               LootPool.builder()
                  .rolls(ConstantLootNumberProvider.create(1.0F))
                  .with(ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create((float)min, (float)max))))
            );
      }
   }

   protected static class MBlockTagProvider extends BlockTagProvider {
      private final MDataGen dataGen;

      public MBlockTagProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator);
         this.dataGen = gen;
      }

      protected void generateTags() {
         this.dataGen.generateBlockTags(this);
      }

      public FabricTagProvider<Block>.FabricTagBuilder<Block> getOrCreateTagBuilder(TagKey<Block> tag) {
         return super.getOrCreateTagBuilder(tag);
      }
   }

   protected static class MItemTagProvider extends ItemTagProvider {
      private final MDataGen dataGen;

      public MItemTagProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator);
         this.dataGen = gen;
      }

      protected void generateTags() {
         this.dataGen.generateItemTags(this);
      }

      public FabricTagProvider<Item>.FabricTagBuilder<Item> getOrCreateTagBuilder(TagKey<Item> tag) {
         return super.getOrCreateTagBuilder(tag);
      }
   }

   protected static class MLanguageProvider extends FabricLanguageProvider {
      private final MDataGen dataGen;

      public MLanguageProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator, "en_us");
         this.dataGen = gen;
      }

      public void generateTranslations(TranslationBuilder builder) {
         this.dataGen.generateTranslations(this, builder);
      }

      public String getTagTranslationKey(@NotNull TagKey<?> tag) {
         return "tag." + tag.id().getNamespace() + "." + tag.id().getPath();
      }

      public String getSoundKey(@NotNull SoundEvent event) {
         return "subtitles." + event.getId().getPath();
      }

      public String[] getDamageKeys(String damageName) {
         return new String[]{"death.attack." + damageName, "death.attack." + damageName + ".player", "death.attack." + damageName + ".item"};
      }
   }

   protected static class MModelProvider extends FabricModelProvider {
      private final MDataGen dataGen;

      public MModelProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator);
         this.dataGen = gen;
      }

      public void generateBlockStateModels(BlockStateModelGenerator generator) {
         this.dataGen.generateBlockStateModels(this, generator);
      }

      public void generateItemModels(ItemModelGenerator generator) {
         this.dataGen.generateItemModels(this, generator);
      }
   }

   protected static class MRecipeProvider extends FabricRecipeProvider {
      private final MDataGen dataGen;

      public MRecipeProvider(MDataGen gen, FabricDataGenerator generator) {
         super(generator);
         this.dataGen = gen;
      }

      protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
         this.dataGen.generateRecipes(this, exporter);
      }
   }*/
}
