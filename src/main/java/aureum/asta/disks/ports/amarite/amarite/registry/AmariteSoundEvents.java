package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.sound.SoundEvent;

public interface AmariteSoundEvents {
   SoundEvent DISC_DAMAGE = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_damage");
   SoundEvent DISC_HIT = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_hit");
   SoundEvent DISC_THROW = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_throw");
   SoundEvent DISC_PICKUP_1 = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_pickup1");
   SoundEvent DISC_PICKUP_2 = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_pickup2");
   SoundEvent DISC_PICKUP_3 = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_pickup3");
   SoundEvent DISC_REBOUND = AureumAstaDisks.REGISTRY.registerSound("item.amarite.disc_rebound");
   SoundEvent MIRROR_EXTRACT = AureumAstaDisks.REGISTRY.registerSound("item.amarite.mirror_extract");
   SoundEvent MIRROR_USE = AureumAstaDisks.REGISTRY.registerSound("item.amarite.mirror_use");
   SoundEvent SWORD_CHARGE = AureumAstaDisks.REGISTRY.registerSound("item.amarite.sword_charge");
   SoundEvent SWORD_BLOCK = AureumAstaDisks.REGISTRY.registerSound("item.amarite.sword_block");
   SoundEvent SWORD_DASH = AureumAstaDisks.REGISTRY.registerSound("item.amarite.sword_dash");
   SoundEvent SWORD_ACCUMULATE = AureumAstaDisks.REGISTRY.registerSound("item.amarite.sword_accumulate");
   SoundEvent MASK_OFFSET = AureumAstaDisks.REGISTRY.registerSound("item.amarite.mask_offset");
   SoundEvent SPARK_ACTIVATE = AureumAstaDisks.REGISTRY.registerSound("block.amarite.spark_activate");
   SoundEvent SPARK_AMBIENT = AureumAstaDisks.REGISTRY.registerSound("block.amarite.spark_ambient");
   SoundEvent SPARK_DEACTIVATE = AureumAstaDisks.REGISTRY.registerSound("block.amarite.spark_deactivate");
   SoundEvent AMARITE_FORMS = AureumAstaDisks.REGISTRY.registerSound("block.amarite.amarite_forms");
   SoundEvent AMARITE_DECAYS = AureumAstaDisks.REGISTRY.registerSound("block.amarite.amarite_decays");
   SoundEvent INFINITE_TOGGLE = AureumAstaDisks.REGISTRY.registerSound("block.amarite.infinite_toggle");
   SoundEvent PACIFICUS = AureumAstaDisks.REGISTRY.registerSound("amarite.pacificus");

   static void init() {
   }

   static SoundEvent getPickupSound(int i) {
      return switch (i) {
         case 2 -> DISC_PICKUP_2;
         case 3 -> DISC_PICKUP_3;
         default -> DISC_PICKUP_1;
      };
   }
}
