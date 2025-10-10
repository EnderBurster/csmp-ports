package aureum.asta.disks.cca.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.PageEntity;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.custom.GrimoireItem;
import aureum.asta.disks.sound.AstaSounds;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GrimoirePageComponent implements AutoSyncedComponent, CommonTickingComponent, GrimoireItem.GrimoireMode {
    private final PlayerEntity player;

    public static final int DEFAULT_COOLDOWN = 5;
    public static final int MODE_COLOR = ColorHelper.Argb.getArgb(255, 117, 124, 200);

    private int defaultCharge = DEFAULT_COOLDOWN;

    public GrimoirePageComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean defaultOnCooldown() {
        return this.defaultCharge < DEFAULT_COOLDOWN;
    }

    @Override
    public void tick() {
        if (this.defaultOnCooldown()) {
            this.defaultCharge++;
            if (this.defaultCharge == DEFAULT_COOLDOWN) {
                this.sync();
            }
        }
    }

    @Override
    public void useAbility() {
        if (this.defaultCharge >= DEFAULT_COOLDOWN || this.player.isCreative()) {
            if (!this.player.isCreative()) {
                this.defaultCharge -= DEFAULT_COOLDOWN;
            }

            this.player.world.playSound(null, player.getBlockPos(), AstaSounds.GRIMOIRE_FIRE, SoundCategory.PLAYERS, 0.3f, 1f);
            PageEntity page = new PageEntity(this.player.world, player);
            page.setOwner(player);
            page.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 2.0F, 1.0F);
            page.setDamage(1.0f);
            page.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

            this.player.world.spawnEntity(page);

            this.sync();
        }
    }

    @Override
    public int getModeColor() {
        return MODE_COLOR;
    }

    @Override
    public float getChargeProgress() {
        return (float) this.defaultCharge/DEFAULT_COOLDOWN;
    }

    @Override
    public String getTranslationKey() {
        return AstaItems.GRIMOIRE.getTranslationKey() + ".default";
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.defaultCharge = tag.getInt("defaultCharge");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.defaultCharge != 0) {
            tag.putInt("defaultCharge", this.defaultCharge);
        }
    }

    private void sync() {
        AureumAstaDisks.PAGE.sync(this.player);
    }
}
