package aureum.asta.disks.mixin.client;

import aureum.asta.disks.AureumAstaDisks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Final
    @Shadow
    private Map<ParticleTextureSheet, Queue<Particle>> particles;

    @Inject(method = "renderParticles",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    public void renderCustomParticles(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci)
    {
        Queue<Particle> iterable = (Queue<Particle>)this.particles.get(ParticleTextureSheet.CUSTOM);

        if (iterable == null)
        {
            return;
        }

        /*RenderSystem.setShader(GameRenderer::getParticleProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        ParticleTextureSheet.CUSTOM.begin(bufferBuilder, ((ParticleManager)(Object)this).textureManager);
        AureumAstaDisks.LOGGER.info(String.valueOf(((ParticleManager)(Object)this).textureManager));*/

        for (Particle particle : iterable) {
            try {
                particle.renderCustom(new MatrixStack(), vertexConsumers, camera, tickDelta);
            } catch (Throwable var10) {
                CrashReport crashReport = CrashReport.create(var10, "Rendering Particle");
                CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                crashReportSection.add("Particle", particle::toString);
                crashReportSection.add("Particle Type", "Custom");
                throw new CrashException(crashReport);
            }
        }

        //ParticleTextureSheet.CUSTOM.draw(tessellator);
        vertexConsumers.draw();
    }
}
