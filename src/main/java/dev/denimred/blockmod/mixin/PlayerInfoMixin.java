package dev.denimred.blockmod.mixin;

import dev.denimred.blockmod.BlockHelper;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin {
    @Inject(method = "Lnet/minecraft/client/multiplayer/PlayerInfo;getSkinLocation()Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void blockmod$getSkinLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        final ResourceLocation blockSkin = BlockHelper.getSkinTexture((PlayerInfo) (Object) this);
        if (blockSkin != null) ci.setReturnValue(blockSkin);
    }
}
