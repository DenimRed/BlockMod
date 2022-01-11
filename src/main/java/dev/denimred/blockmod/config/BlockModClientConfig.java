package dev.denimred.blockmod.config;

import dev.denimred.blockmod.BlockHelper.RenderStyle;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public final class BlockModClientConfig extends BlockModConfig {
    public final BooleanValue enabled;
    public final EnumValue<RenderStyle> renderStyle;
    public final BooleanValue allowSelfBlock;

    BlockModClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = builder.comment("If true, BlockMod will be enabled on the client.").define("enabled", true);
        renderStyle = builder.comment("Defines the style that blocked players should render with.", "BILLBOARD is a simple flat texture, SKIN replaces the players' skin, HIDE makes blocked players invisible.").defineEnum("renderStyle", RenderStyle.SKIN);
        allowSelfBlock = builder.comment("If true, you'll be able to block yourself.").define("allowSelfBlock", false);
    }
}
