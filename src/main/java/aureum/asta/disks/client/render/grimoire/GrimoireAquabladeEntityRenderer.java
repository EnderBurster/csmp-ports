package aureum.asta.disks.client.render.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.AquabladeEntity;
import aureum.asta.disks.entity.grimoire.PageEntity;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class GrimoireAquabladeEntityRenderer extends EntityRenderer<AquabladeEntity> {
    private static final ItemStack stack = AstaItems.GRIMOIRE_AQUABLADE_ITEM.getDefaultStack();
    private final ItemRenderer itemRenderer;

    public GrimoireAquabladeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    public void render(@NotNull AquabladeEntity disc, float yaw, float tickDelta, @NotNull MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(180.0F /*+ MathHelper.lerp(tickDelta, disc.prevYaw, disc.getYaw())*/));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternionf(90.0F /*+ MathHelper.lerp(tickDelta, disc.prevPitch, disc.getPitch())*/));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(((float)disc.age + tickDelta) * -75.0F));
        matrices.scale(2.0F, 2.0F, 2.0F);

        this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, disc.getId());
        matrices.pop();
        super.render(disc, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(AquabladeEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
