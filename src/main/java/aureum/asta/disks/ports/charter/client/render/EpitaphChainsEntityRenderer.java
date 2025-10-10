package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.ports.charter.CharterClient;
import aureum.asta.disks.ports.charter.client.model.EpitaphChainsEntityModel;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EpitaphChainsEntityRenderer extends EntityRenderer<EpitaphChainsEntity> {
    public static final Identifier TEXTURE = new Identifier("charter", "textures/entity/chains.png");
    public EpitaphChainsEntityModel model;

    public EpitaphChainsEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.shadowOpacity = 0.0F;
        this.model = new EpitaphChainsEntityModel(context.getPart(CharterClient.EPITAPH_CHAINS_MODEL_LAYER));
    }

    public void render(EpitaphChainsEntity chains, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int i) {
        matrixStack.push();
        this.model.animateModel(chains, 0.0F, 0.0F, tickDelta);
        matrixStack.translate(0.0, 0.15F, 0.0);
        float h = MathHelper.clamp(0.9F + 1.0F / (((float)chains.age + tickDelta + 0.01F) * 0.2F), 0.0F, 5.0F);
        matrixStack.scale(h, h, h);
        //this.model.render(matrixStack, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE)), 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.2f);
        this.model.render(matrixStack, vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE)), 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(chains, f, tickDelta, matrixStack, vertexConsumers, i);
    }

    public Identifier getTexture(EpitaphChainsEntity entity) {
        return TEXTURE;
    }
}
