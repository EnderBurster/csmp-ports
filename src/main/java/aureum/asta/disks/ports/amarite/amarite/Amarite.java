package aureum.asta.disks.ports.amarite.amarite;

import aureum.asta.disks.ports.amarite.amarite.cca.*;
import aureum.asta.disks.ports.amarite.amarite.registry.*;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aureum.asta.disks.ports.amarite.amarite.recipes.AmariteFoodRecipe;

public class Amarite implements ModInitializer, BlockComponentInitializer, EntityComponentInitializer {
   public static final String MOD_ID = "aureum-asta-disks";
   public static ItemGroup MIRROR_GROUP;
   public static final Logger LOGGER = LoggerFactory.getLogger("amarite");
   public static final ComponentKey<BeaconComponent> BEACON = ComponentRegistry.getOrCreate(id("beacon"), BeaconComponent.class);
   public static final ComponentKey<LongswordDashComponent> DASH = ComponentRegistry.getOrCreate(id("dash"), LongswordDashComponent.class);
   public static final ComponentKey<LongswordDoubleDashComponent> DOUBLE_DASH = ComponentRegistry.getOrCreate(
      id("double_dash"), LongswordDoubleDashComponent.class
   );
   public static final ComponentKey<LongswordAccumulateComponent> ACCUMULATE = ComponentRegistry.getOrCreate(
      id("accumulate"), LongswordAccumulateComponent.class
   );
   public static final ComponentKey<LongswordMalignancyComponent> MALIGNANCY = ComponentRegistry.getOrCreate(
           id("malignancy"), LongswordMalignancyComponent.class
   );
   public static final ComponentKey<DiscComponent> DISC = ComponentRegistry.getOrCreate(id("disc"), DiscComponent.class);
   public static final ComponentKey<DiscPylonComponent> PYLON = ComponentRegistry.getOrCreate(id("pylon"), DiscPylonComponent.class);
   public static final ComponentKey<BuddedComponent> BUDDED = ComponentRegistry.getOrCreate(id("budded"), BuddedComponent.class);

   private static void addToItemGroup()
   {
      ItemGroupEvents.modifyEntriesEvent(MIRROR_GROUP).register(list -> {
         list.addAll(AmariteItems.SPACED_AMARITE_MIRRORS);
      });
   }

   public void onInitialize() {

      AmariteDamageTypes.init();
      AmariteEnchantments.init();
      AmariteEntities.init();

      AmariteItems.init();

      AmariteBlocks.init();

      AmariteParticles.init();
      AmariteSoundEvents.init();
      //AmariteArsenalCompat.init();

      Amarite.LOGGER.info("Amarite Initialized");

      Registry.register(Registries.RECIPE_TYPE, id("amarite_food"), AmariteFoodRecipe.AmariteFoodRecipeType.INSTANCE);
      Registry.register(Registries.RECIPE_SERIALIZER, id("amarite_food"), new SpecialRecipeSerializer(AmariteFoodRecipe::new));


      MIRROR_GROUP = FabricItemGroup.builder(id("mirror_group")).icon(AmariteItems::createMirrorStack).build().mialib$setConstantIcon(AmariteItems::createMirrorStack);
      List<StatusEffect> defaultMirrors = List.of(
         StatusEffects.SPEED,
         StatusEffects.HASTE,
         StatusEffects.RESISTANCE,
         StatusEffects.JUMP_BOOST,
         StatusEffects.STRENGTH,
         StatusEffects.REGENERATION,
         StatusEffects.CONDUIT_POWER
      );

      AmariteItems.SPACED_AMARITE_MIRRORS.clear();
      //AmariteItems.SPACED_AMARITE_MIRRORS.add(ItemStack.EMPTY);

      for (StatusEffect primary : defaultMirrors) {
         ItemStack stack = new ItemStack(AmariteItems.AMARITE_MIRROR);
         stack.getOrCreateNbt().putInt("Primary", StatusEffect.getRawId(primary));
         AmariteItems.SPACED_AMARITE_MIRRORS.add(stack);
      }

      //AmariteItems.SPACED_AMARITE_MIRRORS.add(ItemStack.EMPTY);

      for (StatusEffect primary : defaultMirrors) {
         int i = 1;
         //AmariteItems.SPACED_AMARITE_MIRRORS.add(ItemStack.EMPTY);

         for (StatusEffect secondary : defaultMirrors) {
            ItemStack stack = new ItemStack(AmariteItems.AMARITE_MIRROR);
            stack.getOrCreateNbt().putInt("Primary", StatusEffect.getRawId(primary));
            stack.getOrCreateNbt().putInt("Secondary", StatusEffect.getRawId(secondary));
            AmariteItems.SPACED_AMARITE_MIRRORS.add(stack);
            i++;
         }

         while (i < 9) {
            //AmariteItems.SPACED_AMARITE_MIRRORS.add(ItemStack.EMPTY);
            i++;
         }
      }

      AmariteItems.AMARITE_MIRRORS.clear();
      AmariteItems.SPACED_AMARITE_MIRRORS.forEach(stackx -> {
         if (!stackx.isEmpty()) {
            AmariteItems.AMARITE_MIRRORS.add(stackx);
         }
      });

      addToItemGroup();
   }

   public void registerBlockComponentFactories(@NotNull BlockComponentFactoryRegistry registry) {
      registry.beginRegistration(BeaconBlockEntity.class, BEACON).impl(BeaconComponent.class).end(BeaconComponent::new);
   }

   public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
      registry.beginRegistration(PlayerEntity.class, DASH).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(LongswordDashComponent::new);
      registry.beginRegistration(PlayerEntity.class, DOUBLE_DASH).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(LongswordDoubleDashComponent::new);
      registry.beginRegistration(PlayerEntity.class, ACCUMULATE).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(LongswordAccumulateComponent::new);
      registry.beginRegistration(PlayerEntity.class, MALIGNANCY).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(LongswordMalignancyComponent::new);
      registry.beginRegistration(PlayerEntity.class, DISC).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(DiscComponent::new);
      registry.beginRegistration(Entity.class, PYLON).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(DiscPylonComponent::new);
      registry.beginRegistration(PlayerEntity.class, BUDDED).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(BuddedComponent::new);
   }

   @NotNull
   public static Identifier id(String name) {
      return new Identifier("aureum-asta-disks", name);
   }
}
