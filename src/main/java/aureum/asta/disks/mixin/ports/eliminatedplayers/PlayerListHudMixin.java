package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    /*@ModifyVariable(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"), index = 6)
    private List<PlayerListEntry> eplayers$modifyDeathMessage(List<PlayerListEntry> list) {
        list.removeIf((entry) -> Charter.bannedUuids.contains(entry.getProfile().getId()));
        return list;
    }*/

    @ModifyReturnValue(method = "collectPlayerEntries", at = @At(value = "RETURN"))
    private List<PlayerListEntry> eplayers$modifyDeathMessage(List<PlayerListEntry> list) {
        // Copy into a mutable list
        List<PlayerListEntry> mutable = new ArrayList<>(list);

        // Filter out banned UUIDs
        mutable.removeIf(entry -> Charter.bannedUuids.contains(entry.getProfile().getId()));

        // Return the new filtered list
        return mutable;
    }
}