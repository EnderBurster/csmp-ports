package aureum.asta.disks.ports.blast.client.renderers;

import aureum.asta.disks.ports.blast.common.Blast;
import aureum.asta.disks.ports.blast.common.entity.projectiles.AmethystShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class AmethystShardEntityRenderer extends ProjectileEntityRenderer<AmethystShardEntity> {
    public static final Identifier TEXTURE = new Identifier(Blast.MODID, "textures/entity/projectiles/amethyst_shard.png");

    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(AmethystShardEntity amethystShardEntity) {
        return TEXTURE;
    }
}
