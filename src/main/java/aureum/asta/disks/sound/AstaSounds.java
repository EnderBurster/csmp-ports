package aureum.asta.disks.sound;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.client.sound.Sound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class AstaSounds {
    /*public static SoundEvent THE_VEIL = registerSoundEvent("the_veil");
    public static SoundEvent PARTY_LIFETIME = registerSoundEvent("party_lifetime");
    public static SoundEvent HEADLOCK = registerSoundEvent("headlock");
    public static SoundEvent ME_GUSTAS = registerSoundEvent("me_gustas");
    public static SoundEvent YOUR_SISTER = registerSoundEvent("your_sister");
    public static SoundEvent MINE_YOURS = registerSoundEvent("mine_yours");
    public static SoundEvent NEW_CHINA = registerSoundEvent("new_china");
    public static SoundEvent MISSES = registerSoundEvent("misses");
    public static SoundEvent BORED_YET = registerSoundEvent("bored_yet");*/

    public static SoundEvent ENTITY_GENERIC_ZAP = registerSoundEvent("entity.generic.zap");
    public static SoundEvent ENTITY_GENERIC_PING = registerSoundEvent("entity.generic.ping");
    public static SoundEvent ITEM_GENERIC_WHOOSH = registerSoundEvent("item.generic.whoosh");
    public static SoundEvent ENTITY_GENERIC_ERUPT = registerSoundEvent("entity.generic.erupt");
    public static SoundEvent ENTITY_GENERIC_SPARK = registerSoundEvent("entity.generic.spark");

    public static SoundEvent GRIMOIRE_FIRE = registerSoundEvent("grimoire_fire");
    public static SoundEvent GRIMOIRE_PAGE = registerSoundEvent("grimoire_page");
    public static SoundEvent GRIMOIRE_ALT_1 = registerSoundEvent("grimoire_alt_1");
    public static SoundEvent GRIMOIRE_ALT_2 = registerSoundEvent("grimoire_alt_2");
    public static SoundEvent GRIMOIRE_ALT_3 = registerSoundEvent("grimoire_alt_3");
    public static SoundEvent GRIMOIRE_IMPACT = registerSoundEvent("grimoire_impact");

    public static SoundEvent BARRIER_ACTIVATE = registerSoundEvent("barrier_activate");

    public static SoundEvent BARRIER_VOCALS = registerSoundEvent("barrier_vocals");
    public static SoundEvent BARRIER_VOCALS_1 = registerSoundEvent("barrier_vocals_1");
    public static SoundEvent BARRIER_VOCALS_2 = registerSoundEvent("barrier_vocals_2");
    public static SoundEvent BARRIER_VOCALS_3 = registerSoundEvent("barrier_vocals_3");

    public static SoundEvent GRIMOIRE_WHISPERS = registerSoundEvent("grimoire_whispers");

    public static SoundEvent registerSoundEvent(String name)
    {
        Identifier id = new Identifier(AureumAstaDisks.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {}
}
