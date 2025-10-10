package aureum.asta.disks.mixin.ports.enchancement;

import aureum.asta.disks.ports.enchancement.MEnchantingTableScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HandledScreen.class)
public class HandledScreenMixin implements MEnchantingTableScreen {
}
