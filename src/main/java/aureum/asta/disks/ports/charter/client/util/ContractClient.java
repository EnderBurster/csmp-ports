package aureum.asta.disks.ports.charter.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ContractClient {
    public static void openContractScreen(ItemStack book, PlayerEntity user) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(new BookScreen(new BookScreen.WrittenBookContents(book))));
    }
}
