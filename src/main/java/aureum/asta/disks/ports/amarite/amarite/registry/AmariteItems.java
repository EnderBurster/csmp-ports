package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.FoodComponent.Builder;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordAccumulateComponent;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteDiscItem;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteMirrorItem;
import aureum.asta.disks.ports.amarite.amarite.items.CustomDescriptionItem;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;

public interface AmariteItems {
   ItemGroup AMARITE_GROUP = FabricItemGroup.builder(Amarite.id("amarite_group")).icon(AmariteItems::createIconStack).build().mialib$setConstantIcon(AmariteItems::createIconStack);

   DefaultedList<ItemStack> AMARITE_ITEMS = DefaultedList.of();
   DefaultedList<ItemStack> AMARITE_MIRRORS = DefaultedList.of();
   DefaultedList<ItemStack> SPACED_AMARITE_MIRRORS = DefaultedList.of();

   Item AMARITE_SHARD = AureumAstaDisks.REGISTRY.register("amarite_shard", new Item(new FabricItemSettings()));

   Item AMARITE_LONGSWORD = AureumAstaDisks.REGISTRY.register("amarite_longsword", new AmariteLongswordItem(new FabricItemSettings().rarity(Rarity.EPIC)));

   Item AMARITE_DISC = AureumAstaDisks.REGISTRY.register("amarite_disc", new AmariteDiscItem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1)));

   Item AMARITE_MIRROR = AureumAstaDisks.REGISTRY.register("amarite_mirror", new AmariteMirrorItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

   Item WIN_COOKIE = AureumAstaDisks.REGISTRY.register(
         "win_cookie",
         new CustomDescriptionItem(
            -3706511,
            Text.translatable("item.aureum-asta-disks.win_cookie.desc").formatted(Formatting.GRAY),
            new FabricItemSettings()
               .food(
                  new Builder()
                     .hunger(2)
                     .saturationModifier(0.2F)
                     .alwaysEdible()
                     .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 1200, 0, true, false, true), 1.0F)
                     .build()
               )
               .rarity(Rarity.RARE)
         )
      );

   Item END_COOKIE = AureumAstaDisks.REGISTRY.register(
           "end_cookie",
           new CustomDescriptionItem(
                   ColorHelper.Argb.getArgb(255, 127, 0, 255),
                   Text.translatable("item.aureum-asta-disks.end_cookie.desc").formatted(Formatting.GRAY),
                   new FabricItemSettings()
                           .food(
                                   new Builder()
                                           .hunger(2)
                                           .saturationModifier(0.2F)
                                           .alwaysEdible()
                                           .statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 60, 0, true, false, true), 1.0F)
                                           .build()
                           )
                           .rarity(Rarity.RARE)
           )
   );
   Item AXOLOTL_MASK = AureumAstaDisks.REGISTRY
      .register(
         "axolotl_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/axolotl.png"), 13538338, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item BUNNY_MASK = AureumAstaDisks.REGISTRY
      .register(
         "bunny_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/bunny.png"), 4276551, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item BUTTERFLY_MASK = AureumAstaDisks.REGISTRY
      .register(
         "butterfly_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/butterfly.png"), 1791664, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item DEMON_MASK = AureumAstaDisks.REGISTRY
      .register(
         "demon_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/demon.png"), 3883334, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item FOX_MASK = AureumAstaDisks.REGISTRY
      .register(
         "fox_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/fox.png"), 14643506, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item GUARDIAN_MASK = AureumAstaDisks.REGISTRY
      .register(
         "guardian_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/guardian.png"), 5813148, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item MOON_MASK = AureumAstaDisks.REGISTRY
      .register(
         "moon_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/moon.png"), 12302788, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item ONI_MASK = AureumAstaDisks.REGISTRY
      .register(
         "oni_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/oni.png"), 9313305, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item UNICORN_MASK = AureumAstaDisks.REGISTRY
      .register(
         "unicorn_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/unicorn.png"), 13231849, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item WARDEN_MASK = AureumAstaDisks.REGISTRY
      .register(
         "warden_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/warden.png"), 288381, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item WINSWEEP_MASK = AureumAstaDisks.REGISTRY
      .register(
         "winsweep_mask",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/winsweep.png"), 13070705, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );
   Item AMARITE_CROWN = AureumAstaDisks.REGISTRY
      .register(
         "amarite_crown",
         new MaskItem(
            Amarite.id("textures/item/mask_worn/crown.png"), 11297760, new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)
         )
      );

   private static void addToItemGroup()
   {
      ItemGroupEvents.modifyEntriesEvent(AmariteItems.AMARITE_GROUP).register(list -> {
         list.add(AmariteItems.AMARITE_LONGSWORD.getDefaultStack());
         list.add(AmariteItems.AMARITE_LONGSWORD.getDefaultStack().mialib$enchantStack(AmariteEnchantments.DOUBLE_DASH, 1));
         list.add(AmariteItems.AMARITE_LONGSWORD.getDefaultStack().mialib$enchantStack(AmariteEnchantments.ACCUMULATE, 1));
         //list.add(AmariteItems.AMARITE_LONGSWORD.getDefaultStack().mialib$enchantStack(AmariteEnchantments.MALIGNANCY, 1));
         list.add(AmariteItems.AMARITE_DISC.getDefaultStack());
         list.add(AmariteItems.AMARITE_DISC.getDefaultStack().mialib$enchantStack(AmariteEnchantments.REBOUND, 1));
         /*list.add(AmariteItems.AMARITE_DISC.getDefaultStack().mialib$enchantStack(AmariteEnchantments.ORBIT, 1));
         list.add(AmariteItems.AMARITE_DISC.getDefaultStack().mialib$enchantStack(AmariteEnchantments.PYLON, 1));*/
         list.add(AmariteItems.AMARITE_MIRROR.getDefaultStack());
         list.add(AmariteItems.WIN_COOKIE.getDefaultStack());
         list.add(AmariteItems.AXOLOTL_MASK.getDefaultStack());
         list.add(AmariteItems.BUNNY_MASK.getDefaultStack());
         list.add(AmariteItems.BUTTERFLY_MASK.getDefaultStack());
         list.add(AmariteItems.DEMON_MASK.getDefaultStack());
         list.add(AmariteItems.FOX_MASK.getDefaultStack());
         list.add(AmariteItems.GUARDIAN_MASK.getDefaultStack());
         list.add(AmariteItems.MOON_MASK.getDefaultStack());
         list.add(AmariteItems.ONI_MASK.getDefaultStack());
         list.add(AmariteItems.UNICORN_MASK.getDefaultStack());
         list.add(AmariteItems.WARDEN_MASK.getDefaultStack());
         list.add(AmariteItems.WINSWEEP_MASK.getDefaultStack());
         list.add(AmariteItems.AMARITE_CROWN.getDefaultStack());
         list.add(AmariteBlocks.AMETHYST_BRICKS.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMETHYST_BRICK_WALL.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMETHYST_BRICK_STAIRS.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMETHYST_BRICK_SLAB.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMETHYST_PILLAR.asItem().getDefaultStack());
         list.add(AmariteBlocks.CHISELED_AMETHYST.asItem().getDefaultStack());
         list.add(AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE.asItem().getDefaultStack());
         list.add(AmariteBlocks.CHISELED_AMETHYST_BUTTON.asItem().getDefaultStack());
         list.add(AmariteItems.AMARITE_SHARD.getDefaultStack());
         list.add(AmariteBlocks.AMARITE_BLOCK.asItem().getDefaultStack());
         list.add(AmariteBlocks.BUDDING_AMARITE.asItem().getDefaultStack());
         list.add(AmariteBlocks.FRESH_AMARITE_BUD.asItem().getDefaultStack());
         list.add(AmariteBlocks.PARTIAL_AMARITE_BUD.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMARITE_CLUSTER.asItem().getDefaultStack());
         list.add(AmariteBlocks.AMARITE_SPARK.asItem().getDefaultStack());
         list.add(AmariteBlocks.YELLOW_CARNATION_BOUQUET.asItem().getDefaultStack());
         list.add(AmariteBlocks.YELLOW_CARNATION.asItem().getDefaultStack());
         list.add(AmariteBlocks.POTTED_YELLOW_CARNATION.asItem().getDefaultStack());
      });
   }

   static void init() {
      addToItemGroup();
      ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AmariteItems.MirrorCooldownReloader());
      AMARITE_ITEMS.add(new ItemStack(AMARITE_LONGSWORD));
      AMARITE_ITEMS.add(new ItemStack(AMARITE_DISC));
   }

   @Environment(EnvType.CLIENT)
   static void initClient() {

      ClientPlayNetworking.registerGlobalReceiver(Amarite.id("mirror_cooldown"), (client, handler, buf, responseSender) -> {
         int[] cooldowns = buf.readIntArray();
         client.execute(() -> {
            AmariteMirrorItem.MIRROR_COOLDOWNS.clear();

            for (int i = 0; i < cooldowns.length && cooldowns.length > i + 1; i += 2) {
               AmariteMirrorItem.MIRROR_COOLDOWNS.put(StatusEffect.byRawId(cooldowns[i]), cooldowns[i + 1]);
            }
         });
      });

      ModelPredicateProviderRegistry.register(AMARITE_LONGSWORD, Amarite.id("blocking"), (stack, world, entity, seed) -> {
         if (entity instanceof PlayerEntity player && entity.getMainHandStack() == stack && player.mialib$isUsing()) {
            return 1.0F;
         }

         return 0.0F;
      });
      ModelPredicateProviderRegistry.register(AMARITE_LONGSWORD, Amarite.id("accumulate"), (stack, world, entity, seed) -> {
         if (entity instanceof PlayerEntity player && ((LongswordAccumulateComponent)Amarite.ACCUMULATE.get(player)).accumulateActive) {
            return 1.0F;
         }

         return 0.0F;
      });
      ModelPredicateProviderRegistry.register(
         AMARITE_LONGSWORD, Amarite.id("malignant"), (stack, world, entity, seed) -> stack.getOrCreateNbt().contains("malignant") ? 1.0F : 0.0F
      );
   }

   @NotNull
   static ItemStack createIconStack() {
      return !AMARITE_ITEMS.isEmpty()
         ? (ItemStack)AMARITE_ITEMS.get(Math.abs((int)System.currentTimeMillis() / 800) % AMARITE_ITEMS.size())
         : Items.DIAMOND.getDefaultStack();
   }

   @NotNull
   static ItemStack createMirrorStack() {
      return !AMARITE_MIRRORS.isEmpty()
         ? (ItemStack)AMARITE_MIRRORS.get(Math.abs((int)System.currentTimeMillis() / 800) % AMARITE_MIRRORS.size())
         : Items.DIAMOND.getDefaultStack();
   }

   public static class MirrorCooldownReloader implements SimpleSynchronousResourceReloadListener {
      @NotNull
      public Identifier getFabricId() {
         return Amarite.id("mirror_effects");
      }

      public void reload(@NotNull ResourceManager manager) {
         AmariteMirrorItem.MIRROR_COOLDOWNS.clear();
         manager.findAllResources("amarite_mirror", path -> path.getPath().endsWith(".json"))
            .forEach(
               (identifier, resources) -> {
                  for (Resource resource : resources) {
                     try (InputStream stream = resource.getInputStream()) {
                        JsonObject json = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream))).getAsJsonObject();
                        Identifier effectId = new Identifier(
                           identifier.getNamespace(), identifier.getPath().substring(15, identifier.getPath().length() - 5)
                        );
                        StatusEffect effect = (StatusEffect) Registries.STATUS_EFFECT.get(effectId);
                        if (effect != null) {
                           AmariteMirrorItem.MIRROR_COOLDOWNS.put(effect, json.get("cooldown").getAsInt());
                        }
                     } catch (Exception var10) {
                     }
                  }
               }
            );

         // Networking sync to all players
         MinecraftServer server = AureumAstaDisks.getServer();
         if (server != null) {
            int i = 0;
            int[] array = new int[AmariteMirrorItem.MIRROR_COOLDOWNS.size() * 2];

            for (Map.Entry<StatusEffect, Integer> entry : AmariteMirrorItem.MIRROR_COOLDOWNS.entrySet()) {
               array[i * 2] = StatusEffect.getRawId(entry.getKey());
               array[i * 2 + 1] = entry.getValue();
               i++;
            }

            PacketByteBuf buf = PacketByteBufs.create().writeIntArray(array);
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
               ServerPlayNetworking.send(player, Amarite.id("mirror_cooldown"), buf);
            }
         }
      }
   }
}
