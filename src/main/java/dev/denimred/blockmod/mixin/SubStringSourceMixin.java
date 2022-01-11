package dev.denimred.blockmod.mixin;

import dev.denimred.blockmod.BlockHelper;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.SubStringSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(SubStringSource.class)
public class SubStringSourceMixin {
    @ModifyArg(method = "Lnet/minecraft/network/chat/SubStringSource;create(Lnet/minecraft/network/chat/FormattedText;Lit/unimi/dsi/fastutil/ints/Int2IntFunction;Ljava/util/function/UnaryOperator;)Lnet/minecraft/network/chat/SubStringSource;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/SubStringSource;<init>(Ljava/lang/String;Ljava/util/List;Lit/unimi/dsi/fastutil/ints/Int2IntFunction;)V"))
    private static List<Style> blockmod$init(String string, List<Style> list, Int2IntFunction reverseCharModifier) {
        return BlockHelper.blockStyles(string, list);
    }
}
