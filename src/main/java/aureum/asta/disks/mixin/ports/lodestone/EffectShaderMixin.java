package aureum.asta.disks.mixin.ports.lodestone;

import aureum.asta.disks.api.lodestone.systems.postprocess.LodestoneGlslPreprocessor;
import net.minecraft.client.gl.EffectShaderStage;
import net.minecraft.client.gl.GlImportProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EffectShaderStage.class)
public class EffectShaderMixin {
	@ModifyArg(method = "createFromResource", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/EffectShaderStage;load(Lnet/minecraft/client/gl/ShaderStage$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lnet/minecraft/client/gl/GlImportProcessor;)I"), index = 4)
	private static GlImportProcessor useCustomPreprocessor(GlImportProcessor org) {
		return new LodestoneGlslPreprocessor();
	}
}
