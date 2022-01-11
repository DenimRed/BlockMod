package dev.denimred.blockmod.network;

import dev.denimred.blockmod.BlockMod;
import dev.denimred.blockmod.network.messages.s2c.SyncConfig;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class BlockModNetwork {
    private static final String VERSION = "0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(BlockMod.res("main"),
            () -> VERSION, NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));

    public static final PacketDistributor.PacketTarget ALL_COMPATIBLE = new PacketDistributor<Void>((d, v) -> p -> {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            final Connection connection = player.connection.connection;
            if (CHANNEL.isRemotePresent(connection)) {
                connection.send(p);
            }
        }
    }, NetworkDirection.PLAY_TO_CLIENT).noArg();

    public static void register() {
        var id = -1;
        SyncConfig.register(++id);
    }
}
