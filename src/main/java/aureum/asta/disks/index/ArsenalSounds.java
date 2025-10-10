package aureum.asta.disks.index;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalSounds {
    Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();
    SoundEvent ITEM_SCYTHE_HIT = createSoundEvent("item.scythe.hit");
    SoundEvent ITEM_SCYTHE_SPEWING = createSoundEvent("item.scythe.spewing");
    SoundEvent ENTITY_BLOOD_SCYTHE_HIT = createSoundEvent("entity.blood_scythe.hit");

    static void initialize() {
        SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registries.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
    }

    private static SoundEvent createSoundEvent(String path) {
        SoundEvent soundEvent = SoundEvent.of(new Identifier("aureum-asta-disks", path));
        SOUND_EVENTS.put(soundEvent, new Identifier("aureum-asta-disks", path));
        return soundEvent;
    }
}
