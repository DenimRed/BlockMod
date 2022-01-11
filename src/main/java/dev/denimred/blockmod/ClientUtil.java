package dev.denimred.blockmod;

import net.minecraft.client.Minecraft;

public final class ClientUtil {
    public static String getSelfName() {
        return Minecraft.getInstance().getUser().getName();
    }
}
