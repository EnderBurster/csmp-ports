package aureum.asta.disks.ports.bundle;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

/**
 * Based on <a href="https://github.com/zacharybarbanell/bundle-scroll">Bundle Tweaks</a> by
 * <a href="https://github.com/zacharybarbanell">zacharybarnabell</a> (MIT License)
 */
public class BundleBackportishNetworking {
    public static final Identifier SCROLL_PACKET_ID = new Identifier(BundleBackportish.MOD_ID, "scrollbundle");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SCROLL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            int revision = buf.readInt();
            int i = buf.readInt();
            int amt = buf.readInt();

            server.execute( () -> {
                player.updateLastActionTime();
                ScreenHandler screenHandler = player.currentScreenHandler;

                if (screenHandler.syncId != syncId) {
                    return;
                }
                if (player.isSpectator()) {
                    screenHandler.syncState();
                    return;
                }
                if (!screenHandler.canUse(player)) {
                    BundleBackportish.LOGGER.debug("Player {} interacted with invalid menu {}", player, screenHandler);
                    return;
                }
                if (!screenHandler.isValid(i)) {
                    BundleBackportish.LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", player.getName(), i, screenHandler.slots.size());
                    return;
                }
                boolean flag = revision == player.currentScreenHandler.getRevision();
                screenHandler.disableSyncing();
                Slot slot = screenHandler.getSlot(i);
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof BundleItem) {
                    BundleSelection.add(stack, amt);
                }
                screenHandler.enableSyncing();
                if (flag) {
                    screenHandler.updateToClient();
                } else {
                    screenHandler.sendContentUpdates();
                }
            });
        });
    }
}
