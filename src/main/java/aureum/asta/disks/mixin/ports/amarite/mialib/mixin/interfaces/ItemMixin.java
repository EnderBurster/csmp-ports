package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.interfaces;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MItem;

@Mixin({Item.class})
public class ItemMixin implements MItem {
}
