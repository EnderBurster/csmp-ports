package aureum.asta.disks.cca.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.AquabladeEntity;
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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GrimoireAquabladeComponent implements AutoSyncedComponent, CommonTickingComponent, GrimoireItem.GrimoireMode {
    private final PlayerEntity player;

    public static final int AQUABLADE_COOLDOWN = 400;
    public static final int MODE_COLOR = ColorHelper.Argb.getArgb(255,0,255,228);

    private int aquabladeCharge = AQUABLADE_COOLDOWN;
    private boolean aquabladeActive = false;

    public GrimoireAquabladeComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean aquabladeOnCooldown() {
        return this.aquabladeCharge < AQUABLADE_COOLDOWN;
    }

    @Override
    public void tick() {
        if (!this.aquabladeActive  && aquabladeOnCooldown()) {
            this.aquabladeCharge += 2;
            if (this.aquabladeCharge == AQUABLADE_COOLDOWN) {
                this.sync();
            }
        }
        else if (this.aquabladeActive)
        {
            this.aquabladeCharge--;
            if (this.aquabladeCharge <= 0)
            {
                this.aquabladeCharge = 0;
                this.aquabladeActive = false;
                this.sync();
            }
        }
    }

    @Override
    public void useAbility() {
        if(this.aquabladeCharge >= AQUABLADE_COOLDOWN)
        {
            this.aquabladeActive = true;
            player.world.playSound(null, player.getBlockPos(), AstaSounds.GRIMOIRE_ALT_1, SoundCategory.PLAYERS, 1f, 1f);
            for (int i = 0; i < 3; i++) {
                AquabladeEntity aquablade = new AquabladeEntity(player.world, player);
                aquablade.setOwner(player);
                aquablade.setMaxAge(AQUABLADE_COOLDOWN);

                aquablade.refreshPositionAndAngles(player.getX(), player.getEyeY() - 0.1, player.getZ(), player.getYaw(), player.getPitch());

                aquablade.setDamage(aquablade.getDamage());
                aquablade.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

                aquablade.setOrbitAngle(((2 * MathHelper.PI) / 3) * i);
                player.world.spawnEntity(aquablade);
            }
        }
    }

    @Override
    public int getModeColor() {
        return MODE_COLOR;
    }

    @Override
    public float getChargeProgress() {
        return (float) this.aquabladeCharge/AQUABLADE_COOLDOWN;
    }

    @Override
    public String getTranslationKey() {
        return AstaItems.GRIMOIRE.getTranslationKey() + ".aquablades";
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.aquabladeCharge = tag.getInt("aquabladeCharge");
        this.aquabladeActive = tag.getBoolean("aquabladeActive");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.aquabladeCharge != 0) {
            tag.putInt("aquabladeCharge", this.aquabladeCharge);
        }
        if(this.aquabladeActive)
        {
            tag.putBoolean("aquabladeActive", true);
        }
    }

    private void sync() {
        AureumAstaDisks.AQUABLADE.sync(this.player);
    }
}
