package dev.denimred.blockmod.config;

import dev.denimred.blockmod.network.messages.s2c.SyncConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class BlockModConfig {
    public static final BlockModClientConfig CLIENT = register(Type.CLIENT, BlockModClientConfig::new, "client", "Client-side settings used for personal configuration.");
    public static final BlockModList CLIENT_BLOCKLIST = register(Type.CLIENT, BlockModList::new, "blocklist", "Contains references to players that should be blocked.");
    public static final BlockModList SERVER_BLOCKLIST = register(Type.SERVER, BlockModList::new, "blocklist", "Contains references to players that should be blocked.");
    protected ModConfig modConfig;

    public static void register() {
        // Triggers the classloader
    }

    private static <T extends BlockModConfig> T register(Type type, Function<Builder, T> factory, String name, String... desc) {
        Pair<T, ForgeConfigSpec> pair = new Builder().configure(builder -> {
            if (desc.length > 0) builder.comment(desc);
            builder.push(name);
            T config = factory.apply(builder);
            builder.pop();
            return config;
        });
        T config = pair.getLeft();
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        String fileName = name.contains(type.extension())
                ? "%s-%s.toml".formatted(container.getModId(), name)
                : "%s-%s-%s.toml".formatted(container.getModId(), name, type.extension());
        config.modConfig = new ModConfig(type, pair.getRight(), container, fileName);
        container.addConfig(config.modConfig);
        FMLJavaModLoadingContext.get().getModEventBus().<ModConfigEvent.Reloading>addListener(event -> {
            if (event.getConfig() == config.modConfig) config.onReload();
        });
        return config;
    }

    public void save() {
        ((ForgeConfigSpec) modConfig.getSpec()).save();
        sync();
    }

    public void sync() {
        if (modConfig.getType() == Type.SERVER) {
            SyncConfig.send(modConfig);
        }
    }

    protected void onReload() {
        sync();
    }
}
