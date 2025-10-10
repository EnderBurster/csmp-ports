package aureum.asta.disks.ports.elysium;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;

public class ElysiumSounds {
   public static final SoundEvent ELECTRODE_ZAP = register("block.electrode.zap");
   public static final SoundEvent ELYSIUM_BREAK = register("block.elysium.break");
   public static final SoundEvent ELYSIUM_PLACE = register("block.elysium.place");
   public static final SoundEvent ELYSIUM_STEP = register("block.elysium.step");
   public static final SoundEvent ELYSIUM_HIT = register("block.elysium.hit");
   public static final SoundEvent ELYSIUM_FALL = register("block.elysium.fall");
   public static final SoundEvent ELYSIUM_MACHINE_PLACE = register("block.elysium_machine.place");
   public static final SoundEvent ELYSIUM_PRISM_PLACE = register("block.elysium_prism.place");
   public static final SoundEvent ELYSIUM_PRISM_BREAK = register("block.elysium_prism.break");
   public static final SoundEvent ELYSIUM_PRISM_LOOP = register("block.elysium_prism.loop");
   public static final SoundEvent CHEIROSIPHON_DEACTIVATE = register("item.cheirosiphon.deactivate");
   public static final SoundEvent CHEIROSIPHON_LOOP = register("item.cheirosiphon.loop");
   public static final SoundEvent CHEIROSIPHON_BLAST = register("item.cheirosiphon.blast");
   public static final SoundEvent CHEIROSIPHON_GHASTLY_BLAST = register("item.cheirosiphon.ghastly_blast");
   public static final SoundEvent PARRY = register("entity.ghastly_fireball.parry");
   public static final BlockSoundGroup ELYSIUM = new BlockSoundGroup(1.0F, 1.0F, ELYSIUM_BREAK, ELYSIUM_STEP, ELYSIUM_PLACE, ELYSIUM_HIT, ELYSIUM_FALL);
   public static final BlockSoundGroup ELYSIUM_PRISM = new BlockSoundGroup(
      1.0F, 1.0F, ELYSIUM_PRISM_BREAK, ELYSIUM_STEP, ELYSIUM_PRISM_PLACE, ELYSIUM_HIT, ELYSIUM_FALL
   );

   private static SoundEvent register(String name) {
      return (SoundEvent) Registry.register(Registries.SOUND_EVENT, Elysium.id(name), SoundEvent.of(Elysium.id(name)));
   }

   public static String getSubtitleKey(SoundEvent event) {
      return Util.createTranslationKey("subtitles", event.getId());
   }

   public static void init() {
   }
}
