package dev.denimred.blockmod;

import dev.denimred.blockmod.config.BlockModConfig;
import dev.denimred.blockmod.network.BlockModNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod(BlockMod.MOD_ID)
public final class BlockMod {
    public static final String MOD_ID = "blockmod";

    public BlockMod() {
        BlockModConfig.register();
        BlockModNetwork.register();
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}