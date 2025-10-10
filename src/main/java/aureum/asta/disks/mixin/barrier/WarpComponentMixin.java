package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import moriyashiine.enchancement.common.component.entity.WarpComponent;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WarpComponent.class)
public class WarpComponentMixin {

    @Shadow @Final private TridentEntity obj;

    @Inject(method = "hasWarp", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void removeWarp(CallbackInfoReturnable<Boolean> cir)
    {
        if(this.obj != null && this.obj.world != null)
        {
            for(WaterBarrier bar : this.obj.world.getComponent(AureumAstaDisks.KYRATOS).barriers)
            {
                if(bar.shouldDisableEnchantments(this.obj.getPos()) && !this.obj.world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(obj.getOwner()))
                {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
