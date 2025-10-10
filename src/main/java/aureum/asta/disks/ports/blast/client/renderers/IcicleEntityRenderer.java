package aureum.asta.disks.ports.blast.client.renderers;

import aureum.asta.disks.ports.blast.common.Blast;
import aureum.asta.disks.ports.blast.common.entity.projectiles.AmethystShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class IcicleEntityRenderer extends ProjectileEntityRenderer<AmethystShardEntity> {
    public static final Identifier TEXTURE = new Identifier(Blast.MODID, "textures/entity/projectiles/icicle.png");

    public IcicleEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(AmethystShardEntity amethystShardEntity) {
        return TEXTURE;
    }
}
