package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import java.util.List;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MBeaconBeamSegment;

@Mixin({BeaconBlockEntityRenderer.class})
public class BeaconBlockEntityRendererMixin {
   @Inject(
      method = {"render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/block/entity/BeaconBlockEntityRenderer;renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJII[F)V"
      )},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void amarite$consumedBeacon(
      BeaconBlockEntity beaconBlockEntity,
      float f,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int i,
      int j,
      CallbackInfo ci,
      long l,
      List<BeamSegment> list,
      int k,
      int m,
      @NotNull BeamSegment beamSegment
   ) {
      if (((MBeaconBeamSegment)beamSegment).mialib$isHidden()) {
         ci.cancel();
      }
   }
}
