package dev.denimred.blockmod.mixin;

import dev.denimred.blockmod.BlockHelper;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public class FontMixin {
    @ModifyVariable(method = "Lnet/minecraft/client/gui/Font;renderText(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)F", at = @At("LOAD"))
    private String blockmod$renderText(String text) {
        return BlockHelper.blockRaw(text);
    }
}
