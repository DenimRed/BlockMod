package dev.denimred.blockmod.config;

import dev.denimred.blockmod.BlockHelper;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public final class BlockModList extends BlockModConfig {
    public final ConfigValue<List<? extends String>> names;

    BlockModList(Builder builder) {
        names = builder.comment("The names of players that are on this list.").defineList("names", new ArrayList<>(), o -> o instanceof String s && !s.isBlank());
    }

    @SuppressWarnings("unchecked")
    public List<String> castNames() {
        return (List<String>) names.get();
    }

    @Override
    public void sync() {
        super.sync();
        BlockHelper.resetBlocklist();
    }
}
