package aureum.asta.disks.ports.mace;

import aureum.asta.disks.ports.mace.enchantments.MaceEnchants;
import aureum.asta.disks.ports.mace.entity.ModEntities;
import aureum.asta.disks.ports.mace.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaithfulMace implements ModInitializer {
   public static final String MODID = "aureum-asta-disks";
   public static final Logger MOGGER = LoggerFactory.getLogger("aureum-asta-disks");
   public static final RegistryKey<DamageType> MACE_SMASH;
   public static final RegistryKey<DamageType> WIND_CHARGE;
   public static final Identifier ITEM_MACE_SMASH_AIR;
   public static final Identifier ITEM_MACE_SMASH_GROUND;
   public static final Identifier ITEM_MACE_SMASH_GROUND_HEAVY;
   public static final Identifier ENTITY_WIND_CHARGE_THROW;
   public static final Identifier ENTITY_WIND_CHARGE_WIND_BURST;
   public static SoundEvent ITEM_MACE_SMASH_AIR_SOUND_EVENT;
   public static SoundEvent ITEM_MACE_SMASH_GROUND_SOUND_EVENT;
   public static SoundEvent ITEM_MACE_SMASH_GROUND_HEAVY_SOUND_EVENT;
   public static final SoundEvent ENTITY_WIND_CHARGE_THROW_SOUND_EVENT;
   public static final SoundEvent ENTITY_WIND_CHARGE_WIND_BURST_SOUND_EVENT;
   public static final int MACE_SMASH_WORLD_EVENT_ID = 2013;
   public static final ParticleType<BlockStateParticleEffect> DUST_PILLAR;
   public static final Model HANDHELD_MACE;
   public static final DefaultParticleType GUST_EMITTER_SMALL;
   public static final DefaultParticleType GUST_EMITTER_LARGE;
   public static final DefaultParticleType GUST;
   public static final Identifier EXPLOSION_WIND_CHARGE_S2C_PACKET_ID;

   public FaithfulMace() {
   }

   public static boolean superfluousLogging() {
      return false;
   }

   public void onInitialize() {
      MOGGER.info("Bob's Surprise Initialized.");
      ModItems.initialize();
      ModEntities.initialize();
      Registry.register(Registries.SOUND_EVENT, ITEM_MACE_SMASH_AIR, ITEM_MACE_SMASH_AIR_SOUND_EVENT);
      Registry.register(Registries.SOUND_EVENT, ITEM_MACE_SMASH_GROUND, ITEM_MACE_SMASH_GROUND_SOUND_EVENT);
      Registry.register(Registries.SOUND_EVENT, ITEM_MACE_SMASH_GROUND_HEAVY, ITEM_MACE_SMASH_GROUND_HEAVY_SOUND_EVENT);
      Registry.register(Registries.PARTICLE_TYPE, new Identifier("aureum-asta-disks", "gust_emitter_small"), GUST_EMITTER_SMALL);
      Registry.register(Registries.PARTICLE_TYPE, new Identifier("aureum-asta-disks", "gust_emitter_large"), GUST_EMITTER_LARGE);
      Registry.register(Registries.PARTICLE_TYPE, new Identifier("aureum-asta-disks", "gust"), GUST);
      MaceEnchants.initialize();
   }

   public static DamageSource getMaceSmashDamageSource(World world) {
      return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(MACE_SMASH));
   }

   static {
      MACE_SMASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "mace_smash"));
      WIND_CHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "wind_charge"));
      ITEM_MACE_SMASH_AIR = Identifier.of("aureum-asta-disks", "item.mace.smash_air");
      ITEM_MACE_SMASH_GROUND = Identifier.of("aureum-asta-disks", "item.mace.smash_ground");
      ITEM_MACE_SMASH_GROUND_HEAVY = Identifier.of("aureum-asta-disks", "item.mace.smash_ground_heavy");
      ENTITY_WIND_CHARGE_THROW = Identifier.of("aureum-asta-disks", "entity.wind_charge.throw");
      ENTITY_WIND_CHARGE_WIND_BURST = Identifier.of("aureum-asta-disks", "entity.wind_charge.wind_burst");
      ITEM_MACE_SMASH_AIR_SOUND_EVENT = SoundEvent.of(ITEM_MACE_SMASH_AIR);
      ITEM_MACE_SMASH_GROUND_SOUND_EVENT = SoundEvent.of(ITEM_MACE_SMASH_GROUND);
      ITEM_MACE_SMASH_GROUND_HEAVY_SOUND_EVENT = SoundEvent.of(ITEM_MACE_SMASH_GROUND_HEAVY);
      ENTITY_WIND_CHARGE_THROW_SOUND_EVENT = SoundEvent.of(ENTITY_WIND_CHARGE_THROW);
      ENTITY_WIND_CHARGE_WIND_BURST_SOUND_EVENT = SoundEvent.of(ENTITY_WIND_CHARGE_WIND_BURST);
      DUST_PILLAR = ParticleTypes.register("dust_pillar", false, BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::createCodec);
      HANDHELD_MACE = Models.item("handheld_mace", new TextureKey[]{TextureKey.LAYER0});
      GUST_EMITTER_SMALL = FabricParticleTypes.simple(true);
      GUST_EMITTER_LARGE = FabricParticleTypes.simple(true);
      GUST = FabricParticleTypes.simple(true);
      EXPLOSION_WIND_CHARGE_S2C_PACKET_ID = new Identifier("aureum-asta-disks", "wind_charge_explosion_s2c_packet");
   }
}
