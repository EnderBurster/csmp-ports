package aureum.asta.disks.ports.charter.client.model;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Arrays;

public class EpitaphChainsEntityModel extends EntityModel<EpitaphChainsEntity> {
    private final ModelPart root;
    private final ModelPart[] chains = new ModelPart[2];

    public EpitaphChainsEntityModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.root = root.getChild("root");
        Arrays.setAll(this.chains, index -> root.getChild("root").getChild(getRootName(index)).getChild(getChainName(index)));
    }

    private static String getChainName(int index) {
        return "chain" + (index + 1);
    }
    private static String getRootName(int index) {
        return "root" + (index + 1);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData rootPart = data.getRoot();
        ModelPartData root = rootPart.addChild("root", ModelPartBuilder.create(), ModelTransform.of(0.0F, 15.0f, 0.0F, 0.0f, 0.0f, 0.0f));
        ModelPartData root1 = root.addChild("root1", ModelPartBuilder.create(), ModelTransform.rotation((float)Math.PI / 8.0F, 0.0f, 0.0f));
        ModelPartData root2 = root.addChild("root2", ModelPartBuilder.create(), ModelTransform.rotation((float)Math.PI / -8.0F, 0.0f, 0.0f));
        ModelPartData chain1 = root1.addChild(
                "chain1",
                ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0f, 0.0F)
        );
        ModelPartData chain2 = root2.addChild(
                "chain2",
                ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0f, 0.0F)
        );
        return TexturedModelData.of(data, 64, 64);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public void setAngles(EpitaphChainsEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float yawRad = (float) Math.toRadians(entity.getYaw());
        float pitchRad = (float) Math.toRadians(entity.getPitch());

        this.root.setAngles(pitchRad, yawRad, 0.0f);
    }

    public void animateModel(EpitaphChainsEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        this.root.yaw = (float) Math.toRadians(entity.getYaw() - 90f);

        float time = (entity.age + tickDelta) * 0.06F;
        this.chains[0].yaw = -time;
        this.chains[1].yaw = time;
    }
}
