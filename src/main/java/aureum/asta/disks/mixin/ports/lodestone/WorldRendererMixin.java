package aureum.asta.disks.mixin.ports.lodestone;

import aureum.asta.disks.api.lodestone.handlers.PostProcessHandler;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	/*@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V", ordinal = 1))
	public void lodestone$injectionBeforeTransparencyChainProcess(CallbackInfo ci) {
		PostProcessHandler.copyDepthBuffer();
	}*/

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal = 1))
	public void lodestone$injectionBeforeTransparencyChainProcess(CallbackInfo ci) {
		PostProcessHandler.copyDepthBuffer();
	}

}
