package aureum.asta.disks.ports.charter.common.init;

import aureum.asta.disks.item.ModItemGroup;
import aureum.asta.disks.ports.charter.common.item.*;

import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public interface CharterItems {
   Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
   Item CONTRACT = create("contract", new ContractItem(new FabricItemSettings().maxCount(3)));
   Item HAND = create("arm", new HandItem(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
   Item HOARDER_MAW = create("hoarder_maw", new HoarderMawItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)));
   Item MERCHANT_EFFIGY = create("merchant_effigy", new EffigyItem(new FabricItemSettings()));
   Item ADVANCE_GAUNTLET = create("advance_gauntlet", new GauntletItem(new FabricItemSettings(), true));
   Item BASTION_GAUNTLET = create("bastion_gauntlet", new GauntletItem(new FabricItemSettings(), false));
   Item DUSK_EPITAPH = create("dusk_epitaph", new DuskEpitaph(ToolMaterials.NETHERITE, 5, -2.7f, new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)));
   Item LESSER_DIVINITY = create("lesser_divinity", new Item(new FabricItemSettings().maxDamage(3).rarity(Rarity.UNCOMMON)));
   Item BROKEN_LESSER_DIVINITY = create("broken_lesser_divinity", new Item(new FabricItemSettings().maxDamage(3).rarity(Rarity.UNCOMMON)));
   Item LESSER_DIVINITY_SHARD = create("lesser_divinity_shard", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));

   private static <T extends Item> T create(String name, T item) {
      ITEMS.put(item, new Identifier("charter", name));
      return item;
   }

   static void init() {
      ITEMS.keySet().forEach(item -> Registry.register(Registries.ITEM, ITEMS.get(item), item));

      ItemGroupEvents.modifyEntriesEvent(ModItemGroup.Charter).register((content) -> {
         ITEMS.keySet().forEach((item) -> {
            content.add(item.getDefaultStack());
         });
         /*content.add(CONTRACT.getDefaultStack());
         content.add(HAND.getDefaultStack());
         content.add(ADVANCE_GAUNTLET.getDefaultStack());
         content.add(BASTION_GAUNTLET.getDefaultStack());
         content.add(HOARDER_MAW.getDefaultStack());
         content.add(MERCHANT_EFFIGY.getDefaultStack());*/
      });
   }
}
