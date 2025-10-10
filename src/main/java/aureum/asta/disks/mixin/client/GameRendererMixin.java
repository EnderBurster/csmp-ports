package aureum.asta.disks.mixin.client;

import aureum.asta.disks.ports.other.ReachEntityAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({GameRenderer.class})
abstract class GameRendererMixin implements SynchronousResourceReloader {
    @Shadow
    @Final
    MinecraftClient client;

    @ModifyConstant(
            method = {"updateTargetedEntity(F)V"},
            require = 1,
            allow = 1,
            constant = {@Constant(
                    doubleValue = 6.0
            )}
    )
    private double getActualReachDistance(final double reachDistance) {
        return this.client.player != null ? ReachEntityAttributes.getReachDistance(this.client.player, reachDistance) : reachDistance;
    }

    @ModifyConstant(
            method = {"updateTargetedEntity(F)V"},
            constant = {@Constant(
                    doubleValue = 3.0
            )}
    )
    private double getActualAttackRange0(final double attackRange) {
        return this.client.player != null ? ReachEntityAttributes.getAttackRange(this.client.player, attackRange) : attackRange;
    }

    @ModifyConstant(
            method = {"updateTargetedEntity(F)V"},
            constant = {@Constant(
                    doubleValue = 9.0
            )}
    )
    private double getActualAttackRange1(final double attackRange) {
        return this.client.player != null ? ReachEntityAttributes.getSquaredAttackRange(this.client.player, attackRange) : attackRange;
    }
}