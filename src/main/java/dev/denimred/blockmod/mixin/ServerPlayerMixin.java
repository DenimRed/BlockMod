package dev.denimred.blockmod.mixin;

import dev.denimred.blockmod.BlockHelper;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "Lnet/minecraft/server/level/ServerPlayer;allowsListing()Z", at = @At("HEAD"), cancellable = true)
    private void blockmod$allowsListing(CallbackInfoReturnable<Boolean> ci) {
        if (BlockHelper.isBlocked((ServerPlayer) (Object) this)) ci.setReturnValue(false);
    }
}
