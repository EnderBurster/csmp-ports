package aureum.asta.disks.ports.enchancement;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.client.AureumAstaDisksClient;
import moriyashiine.enchancement.client.screen.EnchantingTableScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncEnchantingTableBookshelfCountPayload {
    public static final Identifier ID = AureumAstaDisks.id("sync_enchanting_table_bookshelf_count");

    private final int bookshelfCount;

    public SyncEnchantingTableBookshelfCountPayload(int bookshelfCount) {
        this.bookshelfCount = bookshelfCount;
    }

    public int getBookshelfCount() {
        return bookshelfCount;
    }

    // Sending the packet
    public static void send(ServerPlayerEntity player, int bookshelfCount) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(bookshelfCount);
        ServerPlayNetworking.send(player, ID, buf);
    }

    // Register the client-side handler
    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            int bookshelfCount = buf.readVarInt();
            client.execute(() -> {
                ClientScreenDataCache.pendingBookshelfCount = bookshelfCount;
            });
        });
    }
}