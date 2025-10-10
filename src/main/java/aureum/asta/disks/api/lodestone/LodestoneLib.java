package aureum.asta.disks.api.lodestone;

import aureum.asta.disks.api.lodestone.helpers.OrtTestItem;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class LodestoneLib implements ModInitializer {
   public static final Logger LOGGER = LogManager.getLogger("LodestoneLib");
   public static final String MODID = "aureum-asta-disks";

   public static final Random RANDOM = Random.create();

   public void onInitialize() {
      LOGGER.info("Lodestone Lib Initialized");

      LodestoneParticles.init();

      Registry.register(
              Registries.ITEM, id("ort"), new OrtTestItem(new FabricItemSettings().rarity(Rarity.EPIC))
      );
   }

   public static Identifier id(String path) {
      return new Identifier(MODID, path);
   }
}
