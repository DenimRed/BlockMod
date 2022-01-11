package dev.denimred.blockmod;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.denimred.blockmod.config.BlockModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.GameProfileArgument.Result;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = BlockMod.MOD_ID)
public final class ForgeEventListener {
    private static final Predicate<CommandSourceStack> IS_ADMIN = src -> src.hasPermission(Commands.LEVEL_ADMINS);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var cmdBlockMod = dispatcher.register(literal(BlockMod.MOD_ID).then(cmdBlock()).then(cmdUnblock()).then(cmdList()));
        dispatcher.register(literal("bm").redirect(cmdBlockMod));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> cmdBlock() {
        Command<CommandSourceStack> cmd = ctx -> {
            var list = BlockModConfig.SERVER_BLOCKLIST.castNames();
            var src = ctx.getSource();
            var count = 0;

            var players = argPlayersGet(ctx);
            if (players.size() == 1) {
                var name = players.iterator().next().getName();
                if (!list.contains(name)) {
                    list.add(name);
                    count++;
                    src.sendSuccess(new TextComponent("Added %s to the server block list".formatted(name)), true);
                } else {
                    src.sendFailure(new TextComponent("%s is already on the server block list".formatted(name)));
                }
            } else {
                for (GameProfile player : players) {
                    var name = player.getName();
                    if (!list.contains(name)) {
                        list.add(name);
                        count++;
                    }
                }
                src.sendSuccess(new TextComponent("Added %s players to the server block list".formatted(count)), true);
            }

            if (count > 0) BlockModConfig.SERVER_BLOCKLIST.save();
            return count;
        };
        return literal("block").requires(IS_ADMIN).then(argPlayers(ctx -> ctx.getSource().getOnlinePlayerNames()).executes(cmd));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> cmdUnblock() {
        Command<CommandSourceStack> cmd = ctx -> {
            var list = BlockModConfig.SERVER_BLOCKLIST.names.get();
            var src = ctx.getSource();
            var count = 0;

            var players = argPlayersGet(ctx);
            if (players.size() == 1) {
                var name = players.iterator().next().getName();
                if (list.contains(name)) {
                    list.remove(name);
                    count++;
                    src.sendSuccess(new TextComponent("Removed %s from the server block list".formatted(name)), true);
                } else {
                    src.sendFailure(new TextComponent("%s isn't on the server block list".formatted(name)));
                }
            } else {
                for (GameProfile player : players) {
                    var name = player.getName();
                    if (list.contains(name)) {
                        list.remove(name);
                        count++;
                    }
                }
                src.sendSuccess(new TextComponent("Removed %s players from the server block list".formatted(count)), true);
            }

            if (count > 0) BlockModConfig.SERVER_BLOCKLIST.save();
            return count;
        };
        return literal("unblock").requires(IS_ADMIN).then(argPlayers(BlockModConfig.SERVER_BLOCKLIST::castNames).executes(cmd));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> cmdList() {
        Command<CommandSourceStack> cmd = ctx -> {
            var list = BlockModConfig.SERVER_BLOCKLIST.names.get();
            var src = ctx.getSource();
            if (!list.isEmpty()) {
                var builder = new StringBuilder();
                list.forEach(s -> builder.append(s).append(", "));
                builder.delete(builder.length() - 2, builder.length());
                src.sendSuccess(new TextComponent("The following players have been blocked on the server: " + builder), false);
            } else {
                src.sendSuccess(new TextComponent("No players have been blocked on the server"), false);
            }
            return list.size();
        };
        return literal("list").executes(cmd);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Result> argPlayers(Supplier<Iterable<String>> names) {
        return argPlayers(ctx -> names.get());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Result> argPlayers(Function<CommandContext<CommandSourceStack>, Iterable<String>> names) {
        return argument("players", GameProfileArgument.gameProfile()).suggests((ctx, builder) -> SharedSuggestionProvider.suggest(names.apply(ctx), builder));
    }

    private static Collection<GameProfile> argPlayersGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return GameProfileArgument.getGameProfiles(ctx, "players");
    }
}
