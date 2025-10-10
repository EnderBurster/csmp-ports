package aureum.asta.disks.ports.elysium.machine;

import aureum.asta.disks.ports.elysium.util.ElysiumDataReloadListener;
import aureum.asta.disks.ports.elysium.util.OptionalMap;
import aureum.asta.disks.ports.elysium.util.PrismPowerLoader;
import aureum.asta.disks.ports.elysium.util.PrismPowerReloadListener;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.machine.electrode.ElectrodeBlock;
import aureum.asta.disks.ports.elysium.machine.electrode.ElectrodeBlockEntity;
import aureum.asta.disks.ports.elysium.machine.electrode.ElectrodeBlockItem;
import aureum.asta.disks.ports.elysium.machine.gravitator.GravitatorBlock;
import aureum.asta.disks.ports.elysium.machine.gravitator.GravitatorBlockEntity;
import aureum.asta.disks.ports.elysium.machine.prism.ElysiumPrismBlock;
import aureum.asta.disks.ports.elysium.machine.prism.ElysiumPrismBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ElysiumMachines {
   public static final TagKey<Block> LIGHTNING_RODS = TagKey.of(Registries.BLOCK.getKey(), Elysium.id("lightning_rods"));
   public static final OptionalMap<Block, Integer> PRISM_POWERS = new OptionalMap<>();
   public static final OptionalMap<Item, Double> ITEM_MAGNETISM = new OptionalMap<>();
   public static final OptionalMap<Item, Double> ITEM_CONDUCTIVITY = new OptionalMap<>();
   public static final OptionalMap<EntityType<?>, Double> ENTITY_MAGNETISM = new OptionalMap<>();
   public static final OptionalMap<EntityType<?>, Double> ENTITY_CONDUCTIVITY = new OptionalMap<>();


   /*public static final RegistryEntryAttachment<Block, Integer> PRISM_POWERS = RegistryEntryAttachment.builder(
                   Registries.BLOCK, Elysium.id("prism_powers"), Integer.class, Codec.INT
      )
      .build();
   public static final RegistryEntryAttachment<Item, Double> ITEM_MAGNETISM = RegistryEntryAttachment.builder(
                   Registries.ITEM, Elysium.id("magnetism"), Double.class, Codec.DOUBLE
      )
      .build();
   public static final RegistryEntryAttachment<Item, Double> ITEM_CONDUCTIVITY = RegistryEntryAttachment.builder(
                   Registries.ITEM, Elysium.id("conductivity"), Double.class, Codec.DOUBLE
      )
      .build();
   public static final RegistryEntryAttachment<EntityType<?>, Double> ENTITY_MAGNETISM = RegistryEntryAttachment.builder(
                   Registries.ENTITY_TYPE, Elysium.id("magnetism"), Double.class, Codec.DOUBLE
      )
      .build();
   public static final RegistryEntryAttachment<EntityType<?>, Double> ENTITY_CONDUCTIVITY = RegistryEntryAttachment.builder(
                   Registries.ENTITY_TYPE, Elysium.id("conductivity"), Double.class, Codec.DOUBLE
      )
      .build();*/


   public static Property<Integer> ELYSIUM_POWER = IntProperty.of("elysium_power", 0, 4);
   public static Pair<Block, Item> ELYSIUM_PRISM;
   public static BlockEntityType<ElysiumPrismBlockEntity> ELYSIUM_PRISM_BLOCK_ENTITY;
   public static Pair<Block, Item> GRAVITATOR;
   public static Pair<Block, Item> REPULSOR;
   public static BlockEntityType<GravitatorBlockEntity> GRAVITATOR_BE;
   public static Pair<Block, Item> ELECTRODE;
   public static BlockEntityType<ElectrodeBlockEntity> ELECTRODE_BE;

   public static void registerAll() {
      ELYSIUM_PRISM = Elysium.registerBlockWithItem(
              Elysium.id("elysium_prism"),
              new ElysiumPrismBlock(
                      Settings.copy((AbstractBlock)Elysium.ELYSIUM_BLOCK.getFirst()).sounds(ElysiumSounds.ELYSIUM_PRISM).luminance(ElysiumPrismBlock::lightLevel)
              ),
              new Item.Settings()
      );
      ELYSIUM_PRISM_BLOCK_ENTITY = (BlockEntityType<ElysiumPrismBlockEntity>) Registry.register(
              Registries.BLOCK_ENTITY_TYPE,
              Elysium.id("elysium_prism"),
              FabricBlockEntityTypeBuilder.create(ElysiumPrismBlockEntity::new, new Block[0]).addBlock((Block)ELYSIUM_PRISM.getFirst()).build()
      );
      GRAVITATOR = Elysium.registerBlockWithItem(
              Elysium.id("gravitator"),
              new GravitatorBlock(Settings.copy((AbstractBlock)Elysium.ELYSIUM_BLOCK.getFirst()).sounds(ElysiumSounds.ELYSIUM), false),
              new Item.Settings()
      );
      REPULSOR = Elysium.registerBlockWithItem(
              Elysium.id("repulsor"),
              new GravitatorBlock(Settings.copy((AbstractBlock)Elysium.ELYSIUM_BLOCK.getFirst()).sounds(ElysiumSounds.ELYSIUM), true),
              new Item.Settings()
      );
      GRAVITATOR_BE = (BlockEntityType<GravitatorBlockEntity>)Registry.register(
              Registries.BLOCK_ENTITY_TYPE,
              Elysium.id("gravitator"),
              FabricBlockEntityTypeBuilder.create(GravitatorBlockEntity::new, new Block[]{(Block)GRAVITATOR.getFirst(), (Block)REPULSOR.getFirst()}).build()
      );
      ELECTRODE = Elysium.registerBlockWithItem(
              Elysium.id("electrode"),
              new ElectrodeBlock(Settings.copy((AbstractBlock)Elysium.ELYSIUM_BLOCK.getFirst()).sounds(ElysiumSounds.ELYSIUM)),
              b -> new ElectrodeBlockItem(b, new Item.Settings())
      );
      ELECTRODE_BE = (BlockEntityType<ElectrodeBlockEntity>)Registry.register(
              Registries.BLOCK_ENTITY_TYPE,
              Elysium.id("electrode"),
              FabricBlockEntityTypeBuilder.create(ElectrodeBlockEntity::new, new Block[]{(Block)ELECTRODE.getFirst()}).build()
      );
   }

   public static void init() {
      registerAll();
      ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ElysiumDataReloadListener());
      //ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PrismPowerReloadListener());
   }

   private ElysiumMachines() {
   }
}
