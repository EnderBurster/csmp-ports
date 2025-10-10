package aureum.asta.disks.mixin.ports.charter;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinProvider.SkinTextureAvailableCallback;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({PlayerSkinProvider.class})
public interface PlayerSkinProviderAccessor {
   @Invoker
   Identifier invokeLoadSkin(MinecraftProfileTexture var1, Type var2, SkinTextureAvailableCallback var3);
}
