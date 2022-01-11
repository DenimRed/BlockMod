package dev.denimred.blockmod.network.messages.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ConfigSync;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

import static dev.denimred.blockmod.network.BlockModNetwork.ALL_COMPATIBLE;
import static dev.denimred.blockmod.network.BlockModNetwork.CHANNEL;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

public final class SyncConfig {
    private final String name;
    private final byte[] data;

    private SyncConfig(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readByteArray());
    }

    public SyncConfig(ModConfig config) {
        this(config.getFileName(), readConfig(config));
    }

    public SyncConfig(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public static void register(int id) {
        CHANNEL.messageBuilder(SyncConfig.class, id, PLAY_TO_CLIENT)
                .encoder(SyncConfig::encode)
                .decoder(SyncConfig::new)
                .consumer(SyncConfig::handle).add();
    }

    public static void send(ModConfig config) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            CHANNEL.send(ALL_COMPATIBLE, new SyncConfig(config));
        }
    }

    private static byte[] readConfig(ModConfig config) {
        try {
            return Files.readAllBytes(config.getFullPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeByteArray(data);
    }

    private void handle(Supplier<NetworkEvent.Context> sup) {
        // Pretty hacky
        ConfigSync.INSTANCE.receiveSyncedConfig(new HandshakeMessages.S2CConfigData(name, data), sup);
        sup.get().setPacketHandled(true);
    }
}
