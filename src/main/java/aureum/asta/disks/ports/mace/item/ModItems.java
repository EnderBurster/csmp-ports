package aureum.asta.disks.ports.mace.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
   public static final Item MACE;
   public static final Item WIND_CHARGE;

   public ModItems() {
   }

   public static Item register(Item item, String id) {
      Identifier itemID = new Identifier("aureum-asta-disks", id);
      Item registeredItem = (Item) Registry.register(Registries.ITEM, itemID, item);
      return registeredItem;
   }

   public static void initialize() {
      //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((ModifyEntries)(itemGroup) -> itemGroup.add(MACE));
      //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((ModifyEntries)(itemGroup) -> itemGroup.add(WIND_CHARGE));
   }

   static {
      MACE = register(new MaceItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(500)), "mace");
      WIND_CHARGE = register(new WindChargeItem(new Item.Settings()), "wind_charge");
   }
}
