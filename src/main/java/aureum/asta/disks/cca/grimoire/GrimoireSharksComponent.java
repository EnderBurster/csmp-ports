package aureum.asta.disks.cca.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.custom.GrimoireItem;
import aureum.asta.disks.sound.AstaSounds;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

public class GrimoireSharksComponent implements AutoSyncedComponent, CommonTickingComponent, GrimoireItem.GrimoireMode {
    public final static int SHARK_COUNT = 3;
    private final static int MAX_SHARK_HEALTH = 60;

    private final PlayerEntity player;

    public final List<Float> sharkHealth = new ArrayList<>();

    public static final int SHARK_COOLDOWN = 600;
    public static final int MODE_COLOR = ColorHelper.Argb.getArgb(255,255,0,103);

    private int sharkCharge = SHARK_COOLDOWN;
    private boolean sharksActive = false;

    public GrimoireSharksComponent(PlayerEntity player) {
        this.player = player;
        for (int i = 0; i < SHARK_COUNT; i++) {
            this.sharkHealth.add((float) MAX_SHARK_HEALTH);
        }
    }

    public boolean sharkOnCooldown() {
        return this.sharkCharge < SHARK_COOLDOWN;
    }

    @Override
    public void tick() {
        if (!this.sharksActive  && sharkOnCooldown()) {
            this.sharkCharge += 6;
            for (int i = 0; i < SHARK_COUNT; i++)
            {
                float newHealth = this.sharkHealth.get(i) >= MAX_SHARK_HEALTH ? MAX_SHARK_HEALTH : this.sharkHealth.get(i)+1;
                this.sharkHealth.set(i, newHealth);
            }
            if (this.sharkCharge == SHARK_COOLDOWN) {
                this.sync();
            }
        }
        else if (this.sharksActive)
        {
            this.sharkCharge--;
            if (this.sharkCharge <= 0)
            {
                this.sharkCharge = 0;
                this.sharksActive = false;
                this.sync();
            }
        }
    }

    @Override
    public void useAbility() {
        if(this.sharkCharge >= SHARK_COOLDOWN)
        {
            this.sharksActive = true;
            player.world.playSound(null, player.getBlockPos(), AstaSounds.GRIMOIRE_ALT_3, SoundCategory.PLAYERS, 1f, 1f);

            for (int i = 0; i < SHARK_COUNT; i++) {
                if (this.sharkHealth.size() < i+1)
                {
                    this.sharkHealth.add((float) MAX_SHARK_HEALTH);
                }

                SharkEntity shark = this.setShark(player, i);
                shark.getWorld().spawnEntity(shark);
            }

            this.sync();
        }
    }

    private @NotNull SharkEntity setShark(PlayerEntity player, int i) {
        double angle = Math.toRadians(i * 120);

        double offsetX = 5.0 * Math.cos(angle);
        double offsetZ = 5.0 * Math.sin(angle);

        SharkEntity shark = new SharkEntity(AstaEntities.GRIMOIRE_SHARK, player.getWorld(), MAX_SHARK_HEALTH, i);
        shark.refreshPositionAndAngles(player.getX() + offsetX, player.getEyeY() + 0.1, player.getZ() + offsetZ, 0.0F, 0.0F);
        shark.setVelocity(0.0f, 0.1f, 0.0f);

        shark.setOwner(player);
        shark.setLifetime(SHARK_COOLDOWN);
        shark.setNum((byte) i);

        return shark;
    }

    public void setSharkHealth(float health, int ID)
    {
        this.sharkHealth.set(ID, health);
        this.sync();
    }

    public int getSharkHealth(int ID)
    {
        return ID >= 0 && ID < this.sharkHealth.size() ? (int) Math.ceil(this.sharkHealth.get(ID) / 20) : 3;
    }

    @Override
    public int getModeColor() {
        return MODE_COLOR;
    }

    @Override
    public float getChargeProgress() {
        return (float) this.sharkCharge/SHARK_COOLDOWN;
    }

    @Override
    public String getTranslationKey() {
        return AstaItems.GRIMOIRE.getTranslationKey() + ".summon";
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.sharkCharge = tag.getInt("sharkCharge");
        this.sharksActive = tag.getBoolean("sharkActive");

        this.sharkHealth.clear();
        NbtList health = tag.getList("SharkHealth", NbtElement.FLOAT_TYPE);

        for (int i = 0; i < health.size(); i++) {
            this.sharkHealth.add(health.getFloat(i));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.sharkCharge != 0) {
            tag.putInt("sharkCharge", this.sharkCharge);
        }

        if (this.sharksActive) {
            tag.putBoolean("sharkActive", true);
        }


        NbtList health = new NbtList();

        for (Float floatingPoint : this.sharkHealth) {
            health.add(NbtFloat.of(floatingPoint));
        }
        tag.put("SharkHealth", health);
    }

    private void sync() {
        AureumAstaDisks.SHARKS.sync(this.player);
    }
}
