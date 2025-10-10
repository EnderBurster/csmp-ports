package aureum.asta.disks.mixin.ports.tweaks;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({PhantomSpawner.class})
public class PhantomSpawnerMixin {
    @Shadow
    private int cooldown;

    @Inject(
            method = {"spawn"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
        if (!spawnMonsters) {
            cir.setReturnValue(0);
        } else if (!world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
            cir.setReturnValue(0);
        } else {
            Random random = world.random;
            this.cooldown--;
            if (this.cooldown > 0) {
                cir.setReturnValue(0);
            } else {
                this.cooldown = this.cooldown + (60 + random.nextInt(60)) * 20;
                if (world.getAmbientDarkness() < 5 && world.getDimension().hasSkyLight()) {
                    cir.setReturnValue(0);
                } else {
                    int i = 0;

                    for (PlayerEntity playerEntity : world.getPlayers()) {
                        if (!playerEntity.isSpectator()) {
                            BlockPos blockPos = playerEntity.getBlockPos();
                            BlockPos blockPos2;
                            LocalDifficulty localDifficulty;
                            if (!(playerEntity.getY() < 168.0)
                                    && (!world.getDimension().hasSkyLight() || blockPos.getY() >= world.getSeaLevel() && world.isSkyVisible(blockPos))
                                    && (localDifficulty = world.getLocalDifficulty(blockPos)).isHarderThan(random.nextFloat() * 3.0F)
                                    && SpawnHelper.isClearForSpawn(
                                    world,
                                    blockPos2 = blockPos.up(20 + random.nextInt(15))
                                            .east(-10 + random.nextInt(21))
                                            .south(-10 + random.nextInt(21)),
                                    world.getBlockState(blockPos2),
                                    world.getFluidState(blockPos2),
                                    EntityType.PHANTOM
                            )) {
                                blockPos2 = blockPos.up(20 + random.nextInt(15));
                                EntityData entityData = null;
                                int l = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);

                                for (int m = 0; m < l; m++) {
                                    PhantomEntity phantomEntity = (PhantomEntity)EntityType.PHANTOM.create(world);
                                    phantomEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
                                    entityData = phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, entityData, null);
                                    world.spawnEntityAndPassengers(phantomEntity);
                                }

                                i += l;
                            }
                        }
                    }

                    cir.setReturnValue(i);
                }
            }
        }
    }
}
