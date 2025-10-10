package aureum.asta.disks.mixin.ports.mace.client;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import aureum.asta.disks.ports.mace.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ItemModelGenerator.class})
public abstract class MaceModelRegisterMixin implements PlayerEntityMaceInterface {
   public MaceModelRegisterMixin() {
   }

   @Shadow
   public abstract void register(Item var1, Model var2);

   @Inject(
           method = {"register()V"},
           at = {@At("HEAD")}
   )
   private void registerMaceModel(CallbackInfo ci) {
      this.register(ModItems.MACE, FaithfulMace.HANDHELD_MACE);
   }
}
