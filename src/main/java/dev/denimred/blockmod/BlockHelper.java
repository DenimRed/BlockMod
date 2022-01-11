package dev.denimred.blockmod;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.authlib.GameProfile;
import dev.denimred.blockmod.config.BlockModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.*;

import static dev.denimred.blockmod.BlockHelper.BlockSide.CLIENT;
import static dev.denimred.blockmod.BlockHelper.BlockSide.SERVER;
import static dev.denimred.blockmod.BlockHelper.RenderStyle.BILLBOARD;
import static dev.denimred.blockmod.BlockHelper.RenderStyle.SKIN;

public final class BlockHelper {
    private static final Multimap<BlockSide, String> BLOCKLIST = Multimaps.newSetMultimap(new EnumMap<>(BlockSide.class), HashSet::new);
    private static final Collection<String> BLOCKLIST_VALUES = Collections.unmodifiableCollection(BLOCKLIST.values());
    private static final ResourceLocation BILLBOARD_ALEX_CLIENT = placeholder(CLIENT, BILLBOARD, true);
    private static final ResourceLocation BILLBOARD_ALEX_SERVER = placeholder(SERVER, BILLBOARD, true);
    private static final ResourceLocation BILLBOARD_STEVE_CLIENT = placeholder(CLIENT, BILLBOARD, false);
    private static final ResourceLocation BILLBOARD_STEVE_SERVER = placeholder(SERVER, BILLBOARD, false);
    private static final ResourceLocation SKIN_ALEX_CLIENT = placeholder(CLIENT, SKIN, true);
    private static final ResourceLocation SKIN_ALEX_SERVER = placeholder(SERVER, SKIN, true);
    private static final ResourceLocation SKIN_STEVE_CLIENT = placeholder(CLIENT, SKIN, false);
    private static final ResourceLocation SKIN_STEVE_SERVER = placeholder(SERVER, SKIN, false);
    private static final Lazy<String> SELF_NAME = Lazy.of(() -> FMLEnvironment.dist.isClient() ? ClientUtil.getSelfName() : null);
    private static int interrupt = 0;

    private static ResourceLocation placeholder(BlockSide blockSide, RenderStyle renderStyle, boolean slim) {
        var blockTypeName = blockSide.name().toLowerCase(Locale.ROOT);
        var renderTypeName = renderStyle.name().toLowerCase(Locale.ROOT);
        var slimName = slim ? "alex" : "steve";
        return BlockMod.res("textures/placeholders/%s/%s_%s.png".formatted(blockTypeName, renderTypeName, slimName));
    }

    public static void resetBlocklist() {
        BLOCKLIST.clear();
        BLOCKLIST.putAll(CLIENT, BlockModConfig.CLIENT_BLOCKLIST.names.get());
        BLOCKLIST.putAll(SERVER, BlockModConfig.SERVER_BLOCKLIST.names.get());
    }

    public static void pushInterrupt() {
        if (++interrupt == Integer.MIN_VALUE) throw new RuntimeException("Overflow! Forgot to pop?");
    }

    public static void popInterrupt() {
        if (--interrupt < 0) throw new RuntimeException("Too low! Forgot to push?");
    }

    private static boolean shouldSkipBlocking() {
        return interrupt > 0 || !BlockModConfig.CLIENT.enabled.get() || BLOCKLIST_VALUES.isEmpty();
    }

    public static boolean canBlock(String name) {
        return !name.equals(SELF_NAME.get()) || BlockModConfig.CLIENT.allowSelfBlock.get();
    }

    public static List<Style> blockStyles(String text, List<Style> originalStyles) {
        if (shouldSkipBlocking()) return originalStyles;

        final ArrayList<Style> styles = new ArrayList<>(originalStyles);
        for (String block : BLOCKLIST_VALUES) {
            if (!canBlock(block)) continue;
            int last = 0;
            int index;
            while ((index = text.indexOf(block, last)) >= 0) {
                final int length = block.length();
                last = index + length;
                for (int i = index; i < last; i++) {
                    styles.set(i, styles.get(i).applyFormat(ChatFormatting.OBFUSCATED));
                }
            }
        }
        return styles;
    }

    public static String blockRaw(String text) {
        if (shouldSkipBlocking()) return text;

        String blocked = text;
        for (String block : BLOCKLIST_VALUES) {
            if (!canBlock(block)) continue;
            // TODO: This causes all following text styles to reset...
            blocked = blocked.replaceAll(block, "§k" + block + "§r");
        }
        return blocked;
    }

    public static boolean isBlocked(Player player) {
        return getBlockSide(player.getGameProfile()) != null;
    }

    @Nullable
    public static BlockSide getBlockSide(GameProfile profile) {
        final String name = profile.getName();
        if (canBlock(name)) {
            if (BLOCKLIST.get(CLIENT).contains(name)) {
                return CLIENT;
            } else if (BLOCKLIST.get(SERVER).contains(name)) {
                return SERVER;
            }
        }
        return null;
    }

    @Nullable
    public static ResourceLocation getBillboardTexture(PlayerInfo info) {
        return getTexture(info, true);
    }

    @Nullable
    public static ResourceLocation getSkinTexture(PlayerInfo info) {
        return getTexture(info, false);
    }

    @Nullable
    private static ResourceLocation getTexture(PlayerInfo info, boolean billboard) {
        var blockSide = getBlockSide(info.getProfile());
        return blockSide != null ? switch (blockSide) {
            case CLIENT -> info.getModelName().equals("slim") ?
                    billboard ? BILLBOARD_ALEX_CLIENT : SKIN_ALEX_CLIENT :
                    billboard ? BILLBOARD_STEVE_CLIENT : SKIN_STEVE_CLIENT;
            case SERVER -> info.getModelName().equals("slim") ?
                    billboard ? BILLBOARD_ALEX_SERVER : SKIN_ALEX_SERVER :
                    billboard ? BILLBOARD_STEVE_SERVER : SKIN_STEVE_SERVER;
        } : null;
    }

    public enum BlockSide {
        CLIENT,
        SERVER
    }

    public enum RenderStyle {
        BILLBOARD,
        SKIN,
        HIDE;

        public boolean isVisible() {
            return this != HIDE;
        }
    }
}
