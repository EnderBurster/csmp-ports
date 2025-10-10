package aureum.asta.disks.ports.mason;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import aureum.asta.disks.ports.mason.util.GlaivePacket;
import aureum.asta.disks.ports.mason.util.UpdatePressingUpDownPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.SpawnGroup;
import software.bernie.geckolib.GeckoLib;

public class MasonDecor implements ModInitializer {
	public static final String MODID = "aureum-asta-disks";

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		MasonObjects.init();
		ServerPlayNetworking.registerGlobalReceiver(GlaivePacket.ID, GlaivePacket::handle);
		BiomeModifications.addSpawn(biome -> biome.hasTag(ConventionalBiomeTags.FOREST), SpawnGroup.CREATURE, MasonObjects.RAVEN, 8000, 2, 5);
		//BiomeModifications.addSpawn(BiomeSelectors.isIn(BiomeTags.FOREST), SpawnGroup.CREATURE, MasonObjects.RAVEN, 8, 2, 5);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePressingUpDownPacket.ID, UpdatePressingUpDownPacket::handle);
	}
}
