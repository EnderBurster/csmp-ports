package aureum.asta.disks.ports.mace.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MaceEnchants {
   public static Enchantment DENSITY = new DensityEnchantment();
   public static Enchantment BREACH = new BreachEnchantment();
   public static Enchantment WIND_BURST = new WindBurstEnchantment();

   public MaceEnchants() {
   }

   public static void initialize() {
      Registry.register(Registries.ENCHANTMENT, new Identifier("aureum-asta-disks", "density"), DENSITY);
      Registry.register(Registries.ENCHANTMENT, new Identifier("aureum-asta-disks", "breach"), BREACH);
      Registry.register(Registries.ENCHANTMENT, new Identifier("aureum-asta-disks", "wind_burst"), WIND_BURST);
   }
}
