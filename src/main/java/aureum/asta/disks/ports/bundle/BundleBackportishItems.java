package aureum.asta.disks.ports.bundle;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BundleBackportishItems {
    private static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, BundleBackportish.id(path), item);
    }

    private static Item registerBundle(String color) {
        return register(color + "_bundle", new BundleItem(new FabricItemSettings().maxCount(1)));
    }

    public static final Item WHITE_BUNDLE = registerBundle("white");
    public static final Item ORANGE_BUNDLE = registerBundle("orange");
    public static final Item MAGENTA_BUNDLE = registerBundle("magenta");
    public static final Item LIGHT_BLUE_BUNDLE = registerBundle("light_blue");
    public static final Item YELLOW_BUNDLE = registerBundle("yellow");
    public static final Item LIME_BUNDLE = registerBundle("lime");
    public static final Item PINK_BUNDLE = registerBundle("pink");
    public static final Item GRAY_BUNDLE = registerBundle("gray");
    public static final Item LIGHT_GRAY_BUNDLE = registerBundle("light_gray");
    public static final Item CYAN_BUNDLE = registerBundle("cyan");
    public static final Item PURPLE_BUNDLE = registerBundle("purple");
    public static final Item BLUE_BUNDLE = registerBundle("blue");
    public static final Item BROWN_BUNDLE = registerBundle("brown");
    public static final Item GREEN_BUNDLE = registerBundle("green");
    public static final Item RED_BUNDLE = registerBundle("red");
    public static final Item BLACK_BUNDLE = registerBundle("black");

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries ->
                fabricItemGroupEntries.addAfter(Items.BUNDLE,
                        WHITE_BUNDLE,
                        LIGHT_GRAY_BUNDLE,
                        GRAY_BUNDLE,
                        BLACK_BUNDLE,
                        BROWN_BUNDLE,
                        RED_BUNDLE,
                        ORANGE_BUNDLE,
                        YELLOW_BUNDLE,
                        LIME_BUNDLE,
                        GREEN_BUNDLE,
                        CYAN_BUNDLE,
                        LIGHT_BLUE_BUNDLE,
                        BLUE_BUNDLE,
                        PURPLE_BUNDLE,
                        MAGENTA_BUNDLE,
                        PINK_BUNDLE
                )
        );
    }
}
