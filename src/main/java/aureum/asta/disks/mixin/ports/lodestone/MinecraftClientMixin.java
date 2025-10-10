package aureum.asta.disks.mixin.ports.lodestone;

import aureum.asta.disks.api.lodestone.handlers.ScreenParticleHandler;
import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.setup.LodestoneScreenParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static aureum.asta.disks.api.lodestone.LodestoneLib.RANDOM;

@Mixin(MinecraftClient.class)
final class MinecraftClientMixin {

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V", ordinal = 17))
	private void lodestone$registerParticleFactories(RunArgs runArgs, CallbackInfo ci) {
		LodestoneParticles.registerFactories();
		LodestoneScreenParticles.registerParticleFactories();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void lodestone$clientTick(CallbackInfo ci) {
		ScreenParticleHandler.clientTick();
		ScreenshakeHandler.clientTick(MinecraftClient.getInstance().gameRenderer.getCamera(), RANDOM);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 4, shift = At.Shift.AFTER))
	private void lodestone$renderTickThingamajig(boolean tick, CallbackInfo ci) {
		ScreenParticleHandler.renderParticles();
	}
}
