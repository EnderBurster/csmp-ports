package aureum.asta.disks.ports.elysium;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonFlame;
import aureum.asta.disks.ports.elysium.cheirosiphon.GhastlyFireball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

public class ElysiumDamageSources {

   public static final RegistryKey<DamageType> PRISM_BEAM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("prism_beam"));
   public static final RegistryKey<DamageType> ELECTRODE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("electrode"));
   public static final RegistryKey<DamageType> CHEIROSIPHON = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("cheirosiphon"));
   public static final RegistryKey<DamageType> CHEIROSIPHON_OVERHEAT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("cheirosiphon_overheat"));
   public static final RegistryKey<DamageType> CHEIROSIPHON_OVERHEAT_BYPASSES = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("cheirosiphon_overheat_bypass"));
   public static final RegistryKey<DamageType> CHEIROSIPHON_BLAST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("cheirosiphon_blast"));
   public static final RegistryKey<DamageType> CHEIROSIPHON_GHASTLY_FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("cheirosiphon_ghastly_fireball"));
   public static final RegistryKey<DamageType> ELYSIUM_ARMOUR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("elysium_armour"));
   public static final RegistryKey<DamageType> ELYSIUM_ARMOUR_BYPASS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("elysium_armour_bypass"));
   public static final RegistryKey<DamageType> VULNERABILITY_WASH_AWAY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Elysium.id("vulnerability_wash_away"));

   private final DamageSource prism_beam;
   private final DamageSource electrode;
   private final DamageSource cheirosiphon;
   private final DamageSource cheirosiphon_overheat;
   private final DamageSource cheirosiphon_overheat_bypasses;
   private final DamageSource cheirosiphon_blast;
   private final DamageSource cheirosiphon_ghastly_fireball;
   private final DamageSource elysium_armour;
   private final DamageSource elysium_armour_bypass;
   private final DamageSource vulnerability_wash_away;

   public static final String CHEIROSIPHON_MSG_ID = "cheirosiphon";
   public static final String CHEIROSIPHON_BLAST_MSG_ID = "cheirosiphon_blast";
   public static final String ELYSIUM_ARMOUR_MSG_ID = "elysium_armour";
   public static final String GHASTLY_FIREBALL_MSG_ID = "cheirosiphon_ghastly_fireball";

   public ElysiumDamageSources(DamageSources damageSources) {
      this.prism_beam = damageSources.create(PRISM_BEAM);
      this.electrode = damageSources.create(ELECTRODE);
      this.cheirosiphon = damageSources.create(CHEIROSIPHON);
      this.cheirosiphon_overheat = damageSources.create(CHEIROSIPHON_OVERHEAT);
      this.cheirosiphon_overheat_bypasses = damageSources.create(CHEIROSIPHON_OVERHEAT_BYPASSES);
      this.cheirosiphon_blast = damageSources.create(CHEIROSIPHON_BLAST);
      this.cheirosiphon_ghastly_fireball = damageSources.create(CHEIROSIPHON_GHASTLY_FIREBALL);
      this.elysium_armour = damageSources.create(ELYSIUM_ARMOUR);
      this.vulnerability_wash_away = damageSources.create(VULNERABILITY_WASH_AWAY);
      this.elysium_armour_bypass = damageSources.create(ELYSIUM_ARMOUR_BYPASS);
   }

   public DamageSource prism_beam() {
      return prism_beam;
   }

   public DamageSource electrode() {
      return electrode;
   }

   public DamageSource cheirosiphon() {
      return cheirosiphon;
   }

   public DamageSource cheirosiphon_overheat() {
      return cheirosiphon_overheat;
   }

   public DamageSource cheirosiphon_overheat_bypasses() {
      return cheirosiphon_overheat_bypasses;
   }

   public DamageSource cheirosiphon_blast() {
      return cheirosiphon_blast;
   }

   public DamageSource cheirosiphon_ghastly_fireball() {
      return cheirosiphon_ghastly_fireball;
   }

   public DamageSource elysium_armour() {
      return elysium_armour;
   }

   public DamageSource elysium_armour_bypass() {
      return elysium_armour_bypass;
   }

   public DamageSource vulnerability_wash_away() {
      return vulnerability_wash_away;
   }

   /*public static final DamageSource PRISM_BEAM = new DamageSource("prism_beam").setFire();
   public static final DamageSource CHEIROSIPHON_OVERHEAT = new DamageSource("cheirosiphon_overheat");
   public static final DamageSource ELECTRODE = new DamageSource("electrode");
   public static final DamageSource VULNERABILITY_WASH_AWAY = new DamageSource("vulnerability_wash_away").setBypassesArmor();
   public static final String CHEIROSIPHON_MSG_ID = "cheirosiphon";
   public static final String CHEIROSIPHON_BLAST_MSG_ID = "cheirosiphon_blast";
   public static final String ELYSIUM_ARMOUR_MSG_ID = "elysium_armour";
   public static final String GHASTLY_FIREBALL_MSG_ID = "cheirosiphon_ghastly_fireball";*/

   /*public static DamageSource cheirosiphon(CheirosiphonFlame flame, @Nullable Entity source) {
      return new ProjectileDamageSource("cheirosiphon", flame, source);
   }

   public static DamageSource cheirosiphonBlast(Entity source) {
      return new EntityDamageSource("cheirosiphon_blast", source).setBypassesArmor();
   }

   public static DamageSource elysiumArmour(Entity source) {
      return new EntityDamageSource("elysium_armour", source);
   }

   public static DamageSource ghastlyFireball(GhastlyFireball fireball, @Nullable Entity owner) {
      return new ProjectileDamageSource("cheirosiphon_ghastly_fireball", fireball, owner).setProjectile();
   }*/
}
