package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import net.minecraft.client.gl.ShaderStage;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage.Type;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ShaderProgram.class})
public class ShaderMixin {
   @Mutable
   @Shadow
   @Final
   private String name;
   @Unique
   private static String nameCache;
   @Unique
   private static Type typeCache;

   @ModifyVariable(
      method = {"<init>"},
      index = 2,
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/gl/ShaderProgram;vertexShader:Lnet/minecraft/client/gl/ShaderStage;"
      ),
      argsOnly = true
   )
   public String help$removeString(String value) {
      return "";
   }

   @ModifyVariable(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/resource/ResourceFactory;openAsReader(Lnet/minecraft/util/Identifier;)Ljava/io/BufferedReader;",
         shift = Shift.BEFORE
      ),
      index = 4
   )
   public Identifier help$changeIdentifier(Identifier location) {
      Identifier id = new Identifier(this.name);
      this.name = "minecraft".equals(id.getNamespace()) ? id.getPath() : id.toString();
      return new Identifier(id.getNamespace(), "shaders/core/" + id.getPath() + ".json");
   }

   @Inject(
      method = {"loadShader"},
      at = {@At("HEAD")}
   )
   private static void help$grab(ResourceFactory resourceProvider, Type type, String string, CallbackInfoReturnable<ShaderStage> cir) {
      nameCache = string;
      typeCache = type;
   }

   @Inject(
      method = {"loadShader"},
      at = {@At("TAIL")}
   )
   private static void help$clear(ResourceFactory resourceProvider, Type type, String string, CallbackInfoReturnable<ShaderStage> cir) {
      nameCache = null;
      typeCache = null;
   }

   @ModifyVariable(
      method = {"loadShader"},
      at = @At(
         value = "NEW",
         target = "(Ljava/lang/String;)Lnet/minecraft/util/Identifier;",
         ordinal = 0,
         shift = Shift.BEFORE
      ),
      ordinal = 1
   )
   @NotNull
   private static String help$change(String value) {
      Identifier id = new Identifier(nameCache);
      return id.getNamespace() + ":shaders/core/" + id.getPath() + typeCache.getFileExtension();
   }
}
