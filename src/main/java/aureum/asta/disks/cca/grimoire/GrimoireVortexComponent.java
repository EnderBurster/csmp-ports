package aureum.asta.disks.cca.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.VortexProjectileEntity;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.custom.GrimoireItem;
import aureum.asta.disks.sound.AstaSounds;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GrimoireVortexComponent implements AutoSyncedComponent, CommonTickingComponent, GrimoireItem.GrimoireMode {
    private final PlayerEntity player;

    public final List<Integer> vortexDurations = new ArrayList<>();
    public final List<Byte> vortexOnCooldown = new ArrayList<>();

    public static final int VORTEX_COOLDOWN = 180;
    public static final int VORTEX_COUNT = 3;
    public static final int MODE_COLOR = ColorHelper.Argb.getArgb(255,0,255,83);

    public GrimoireVortexComponent(PlayerEntity player) {
        this.player = player;
    }

    public byte vortexOnCooldown(int ID) {
        return ID >= 0 && ID < this.vortexOnCooldown.size() ? this.vortexOnCooldown.get(ID) : 1;
    }

    @Override
    public void tick() {
        for (int i = 0; i < this.vortexDurations.size(); i++)
        {
            if(vortexOnCooldown(i) == 1)
            {
                int updatedDuration = this.vortexDurations.get(i);
                updatedDuration = MathHelper.clamp(updatedDuration+2, 0, VORTEX_COOLDOWN);
                this.vortexDurations.set(i, updatedDuration);
            }
            else if (vortexDurations.get(i) > 0){
                int updatedDuration = this.vortexDurations.get(i);
                updatedDuration--;
                this.vortexDurations.set(i, updatedDuration);
            }
            else
            {
                this.vortexOnCooldown.set(i, (byte)1);
            }
        }

        this.sync();
    }

    @Override
    public void useAbility() {
        if (this.vortexDurations.size() < VORTEX_COUNT)
        {
            spawnVortex();
            vortexDurations.add(VORTEX_COOLDOWN);
            vortexOnCooldown.add((byte) 0);
        }
        else for (int i = 0; i < VORTEX_COUNT; i++)
        {
            if (this.vortexDurations.get(i) >= VORTEX_COOLDOWN)
            {
                spawnVortex();
                if(vortexOnCooldown.size() < VORTEX_COUNT)
                {
                    vortexOnCooldown.add((byte)0);
                }
                vortexOnCooldown.set(i, (byte)0);
                break;
            }
        }

        this.sync();
    }

    public void spawnVortex()
    {
        this.player.world.playSound(null, player.getBlockPos(), AstaSounds.GRIMOIRE_ALT_2, SoundCategory.PLAYERS, 1f, 1f);

        VortexProjectileEntity vortexProjectile = new VortexProjectileEntity(this.player.world, player);
        vortexProjectile.setOwner(player);
        vortexProjectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 2.0F, 1.0F);
        vortexProjectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        vortexProjectile.owner = this.player.getUuid();
        vortexProjectile.maxLifespawn = VORTEX_COOLDOWN;

        this.player.world.spawnEntity(vortexProjectile);
        this.player.getItemCooldownManager().set(this.player.getMainHandStack().getItem(), 10);
        this.sync();
    }

    @Override
    public int getModeColor() {
        return MODE_COLOR;
    }

    @Override
    public float getChargeProgress() {
        return 0;
    }

    @Override
    public String getTranslationKey() {
        return AstaItems.GRIMOIRE.getTranslationKey() + ".pull";
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.vortexDurations.clear();
        NbtList duration = tag.getList("VortexDuration", NbtElement.INT_TYPE);

        for (int i = 0; i < duration.size(); i++) {
            this.vortexDurations.add(duration.getInt(i));
        }

        this.vortexOnCooldown.clear();
        NbtList cooldowns = tag.getList("VortexCooldowns", NbtElement.INT_TYPE);

        for (int i = 0; i < cooldowns.size(); i++) {
            this.vortexOnCooldown.add((byte) cooldowns.getInt(i));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList duration = new NbtList();

        for (Integer integer : this.vortexDurations) {
            duration.add(NbtInt.of(integer));
        }
        tag.put("VortexDuration", duration);

        NbtList cooldowns = new NbtList();
        for (Byte bool : this.vortexOnCooldown) {
            cooldowns.add(NbtInt.of((int)bool));
        }
        tag.put("VortexCooldowns", cooldowns);
    }

    public int getVortexDuration(int ID) {
        return ID >= 0 && ID < this.vortexDurations.size() ? (int) Math.floor(this.vortexDurations.get(ID)/60f) : 3;
    }

    private void sync() {
        AureumAstaDisks.VORTEX.sync(this.player);
    }
}
