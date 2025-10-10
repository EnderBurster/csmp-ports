package aureum.asta.disks.init;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.enchantment.HoeEnchantment;
import aureum.asta.disks.enchantment.MaceEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AstaEnchantments {
    public static final Enchantment APEX;
    public static final Enchantment METEOR;
    public static final Enchantment THUNDERSTRUCK;


    public static void init()
    {
        Registry.register(Registries.ENCHANTMENT, AureumAstaDisks.id("apex"), APEX);
        Registry.register(Registries.ENCHANTMENT, AureumAstaDisks.id("meteor"), METEOR);
        Registry.register(Registries.ENCHANTMENT, AureumAstaDisks.id("thunderstruck"), THUNDERSTRUCK);
    }

    static
    {
        APEX = new HoeEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND);
        METEOR = new MaceEnchantment(2, Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
        THUNDERSTRUCK = new MaceEnchantment(2, Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    }
}
