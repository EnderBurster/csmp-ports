package aureum.asta.disks.ports.elysium;

import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.ModItemGroup;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmour;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmourComponent;
import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonFlame;
import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonItem;
import aureum.asta.disks.ports.elysium.cheirosiphon.GhastlyEnchantment;
import aureum.asta.disks.ports.elysium.cheirosiphon.GhastlyFireball;
import aureum.asta.disks.ports.elysium.cheirosiphon.HeatingItemsComponent;
import aureum.asta.disks.ports.elysium.cheirosiphon.JetEnchantment;
import aureum.asta.disks.ports.elysium.fire.ElysiumFireBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import aureum.asta.disks.ports.elysium.particles.ArcParticleOption;
import aureum.asta.disks.ports.elysium.particles.MagneticWaveParticleOption;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elysium implements ModInitializer, EntityComponentInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("Elysium");
   public static final ItemGroup ELYSIUM_TAB = FabricItemGroup.builder(id("elysium")).displayName(Text.literal("Elysium")).icon(() -> new ItemStack(Elysium.ELYSIUM_INGOT)).build();
   public static final TagKey<Block> ELYSIUM_FIRE_BASE_BLOCKS = TagKey.of(Registries.BLOCK.getKey(), id("elysium_fire_base_blocks"));
   public static Block ELYSIUM_FIRE;
   public static Item ELYSIUM_INGOT;
   public static Pair<Block, Item> ELYSIUM_BLOCK;
   public static Pair<Block, Item> DIM_SELFLIT_REDSTONE_LAMP;
   public static Pair<Block, Item> SELFLIT_REDSTONE_LAMP;
   public static Pair<Block, Item> BRILLIANT_SELFLIT_REDSTONE_LAMP;
   public static CheirosiphonItem CHEIROSIPHON;
   public static EntityType<CheirosiphonFlame> CHEIROSIPHON_FLAME;
   public static Item GHASTLY_FIREBALL_ITEM;
   public static EntityType<GhastlyFireball> GHASTLY_FIREBALL;
   public static JetEnchantment JET_ENCHANTMENT;
   public static GhastlyEnchantment GHASTLY_ENCHANTMENT;
   public static DefaultParticleType ELYSIUM_FLAME_PARTICLE;
   public static ParticleType<MagneticWaveParticleOption> MAGNETIC_WAVE_PARTICLE;
   public static DefaultParticleType MAGNETIC_PULSE_PARTICLE;
   public static ParticleType<ArcParticleOption> ARC_PARTICLE;

   public static void registerAll() {
      ELYSIUM_FIRE = (Block) Registry.register(
              Registries.BLOCK,
              id("elysium_fire"),
              new ElysiumFireBlock(Settings.of(Material.FIRE, MapColor.LIGHT_BLUE).noCollision().breakInstantly().luminance($ -> 10).sounds(BlockSoundGroup.WOOL))
      );
      ELYSIUM_INGOT = (Item)Registry.register(
              Registries.ITEM, id("elysium_ingot"), new Item(new Item.Settings())
      );
      ELYSIUM_BLOCK = registerBlockWithItem(
              id("elysium_block"),
              new Block(Settings.of(Material.METAL, MapColor.BROWN).requiresTool().strength(28.0F, 1200.0F).sounds(ElysiumSounds.ELYSIUM)),
              new Item.Settings()
      );
      DIM_SELFLIT_REDSTONE_LAMP = registerBlockWithItem(
              id("dim_selflit_redstone_lamp"),
              new Block(Settings.copy(Blocks.REDSTONE_LAMP).luminance($ -> 5)),
              new Item.Settings()
      );
      SELFLIT_REDSTONE_LAMP = registerBlockWithItem(
              id("selflit_redstone_lamp"), new Block(Settings.copy(Blocks.REDSTONE_LAMP).luminance($ -> 13)), new Item.Settings()
      );
      BRILLIANT_SELFLIT_REDSTONE_LAMP = registerBlockWithItem(
              id("brilliant_selflit_redstone_lamp"),
              new Block(Settings.copy(Blocks.REDSTONE_LAMP).luminance($ -> 15)),
              new Item.Settings()
      );
      CHEIROSIPHON = (CheirosiphonItem)Registry.register(
              Registries.ITEM, id("cheirosiphon"), new CheirosiphonItem(new Item.Settings().maxCount(1))
      );
      CHEIROSIPHON_FLAME = (EntityType<CheirosiphonFlame>)Registry.register(
              Registries.ENTITY_TYPE,
              id("cheirosiphon_flame"),
              FabricEntityTypeBuilder.<CheirosiphonFlame>create(SpawnGroup.MISC, CheirosiphonFlame::new)
                      .dimensions(EntityDimensions.changing(0.25F, 1.0F))
                      .trackRangeChunks(8)
                      .trackedUpdateRate(20)
                      .build()
      );

      GHASTLY_FIREBALL_ITEM = (Item)Registry.register(
              Registries.ITEM, id("ghastly_fireball"), new Item(new Item.Settings().maxCount(16))
      );

      GHASTLY_FIREBALL = (EntityType<GhastlyFireball>)Registry.register(
              Registries.ENTITY_TYPE,
              id("ghastly_fireball"),
              FabricEntityTypeBuilder.<GhastlyFireball>create(SpawnGroup.MISC, GhastlyFireball::new)
                      .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
                      .trackRangeChunks(8)
                      .trackedUpdateRate(10)
                      .build()
      );
      JET_ENCHANTMENT = (JetEnchantment)Registry.register(Registries.ENCHANTMENT, id("jet"), new JetEnchantment());
      GHASTLY_ENCHANTMENT = (GhastlyEnchantment)Registry.register(
              Registries.ENCHANTMENT, id("ghastly"), new GhastlyEnchantment()
      );
      ELYSIUM_FLAME_PARTICLE = (DefaultParticleType)Registry.register(
              Registries.PARTICLE_TYPE, id("elysium_flame"), new DefaultParticleType(false) {
              }
      );
      MAGNETIC_WAVE_PARTICLE = (ParticleType<MagneticWaveParticleOption>)Registry.register(
              Registries.PARTICLE_TYPE, id("magnetic_wave"), new ParticleType<MagneticWaveParticleOption>(true, MagneticWaveParticleOption.DESERIALIZER) {
                 public Codec<MagneticWaveParticleOption> getCodec() {
                    return MagneticWaveParticleOption.CODEC;
                 }
              }
      );
      MAGNETIC_PULSE_PARTICLE = (DefaultParticleType)Registry.register(
              Registries.PARTICLE_TYPE, id("magnetic_pulse"), new DefaultParticleType(false) {
              }
      );
      ARC_PARTICLE = (ParticleType<ArcParticleOption>)Registry.register(
              Registries.PARTICLE_TYPE, id("arc"), new ParticleType<ArcParticleOption>(false, ArcParticleOption.DESERIALIZER) {
                 public Codec<ArcParticleOption> getCodec() {
                    return ArcParticleOption.CODEC;
                 }
              }
      );
   }

   public static Identifier id(String path) {
      return new Identifier("aureum-asta-disks", path);
   }

   public static Pair<Block, Item> registerBlockWithItem(Identifier id, Block block, Item.Settings itemProperties) {
      return Pair.of((Block)Registry.register(Registries.BLOCK, id, block), (Item)Registry.register(Registries.ITEM, id, new BlockItem(block, itemProperties)));
   }

   public static Pair<Block, Item> registerBlockWithItem(Identifier id, Block block, Function<Block, Item> itemFunc) {
      return Pair.of((Block)Registry.register(Registries.BLOCK, id, block), (Item)Registry.register(Registries.ITEM, id, itemFunc.apply(block)));
   }

   private static void addToItemGroup()
   {
      ItemGroupEvents.modifyEntriesEvent(ELYSIUM_TAB).register(list -> {
         list.add(ELYSIUM_INGOT.getDefaultStack());
         list.add(ELYSIUM_BLOCK.getSecond().getDefaultStack());
         list.add(DIM_SELFLIT_REDSTONE_LAMP.getSecond().getDefaultStack());
         list.add(SELFLIT_REDSTONE_LAMP.getSecond().getDefaultStack());
         list.add(BRILLIANT_SELFLIT_REDSTONE_LAMP.getSecond().getDefaultStack());
         list.add(CHEIROSIPHON.getDefaultStack());
         list.add(GHASTLY_FIREBALL_ITEM.getDefaultStack());
         list.add(ElysiumArmour.ELYSIUM_HELMET.getDefaultStack());
         list.add(ElysiumArmour.ELYSIUM_CHESTPLATE.getDefaultStack());
         list.add(ElysiumArmour.ELYSIUM_LEGGINGS.getDefaultStack());
         list.add(ElysiumArmour.ELYSIUM_BOOTS.getDefaultStack());
         list.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_HELMET.getDefaultStack());
         list.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_CHESTPLATE.getDefaultStack());
         list.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_LEGGINGS.getDefaultStack());
         list.add(ElysiumArmour.EXPERIMENTAL_ELYSIUM_BOOTS.getDefaultStack());
         list.add(ElysiumMachines.ELYSIUM_PRISM.getSecond().getDefaultStack());
         list.add(ElysiumMachines.GRAVITATOR.getSecond().getDefaultStack());
         list.add(ElysiumMachines.REPULSOR.getSecond().getDefaultStack());
         list.add(ElysiumMachines.ELECTRODE.getSecond().getDefaultStack());
      });
   }

   public void onInitialize() {
      registerAll();
      CheirosiphonItem.ServerboundAirblastPacket.init();
      ElysiumMachines.init();
      ElysiumArmour.init();
      ElysiumSounds.init();
      addToItemGroup();
   }

   public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
      registry.registerForPlayers(HeatingItemsComponent.KEY, HeatingItemsComponent::new, RespawnCopyStrategy.INVENTORY);
      registry.registerForPlayers(ElysiumArmourComponent.KEY, ElysiumArmourComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
      registry.registerFor(LivingEntity.class, ElysiumArmourComponent.KEY, ElysiumArmourComponent::new);
   }
}
