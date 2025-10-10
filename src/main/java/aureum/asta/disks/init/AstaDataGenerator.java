package aureum.asta.disks.init;

import aureum.asta.disks.data.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AstaDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(AstaLootTableGenerator::new);
		pack.addProvider(AstaModelProvider::new);
		pack.addProvider(AstaRecipeGenerator::new);
		pack.addProvider(AstaTagGenerator::new);
		pack.addProvider(AstaEnglishLangGenerator::new);
	}
}
