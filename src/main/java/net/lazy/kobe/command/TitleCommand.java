package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.lazy.kobe.titles.TitleAttachment;
import net.lazy.kobe.titles.TitleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TitleCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("title")
                        .requires(source -> source.hasPermission(2)) // OP only for now
                        // -----------------------------------------
                        // UNLOCK
                        // -----------------------------------------
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String id = StringArgumentType.getString(ctx, "id");

                                            var data = player.getData(TitleAttachment.TITLES);

                                            if (TitleRegistry.get(id) == null) {
                                                player.sendSystemMessage(Component.literal("Unknown title id."));
                                                return 0;
                                            }

                                            data.unlock(id);
                                            player.refreshDisplayName();
                                            player.sendSystemMessage(Component.literal("Unlocked title: " + id));
                                            return 1;
                                        })
                                )
                        )
                        // -----------------------------------------
                        // REMOVE
                        // -----------------------------------------
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String id = StringArgumentType.getString(ctx, "id");

                                            var data = player.getData(TitleAttachment.TITLES);

                                            data.getUnlocked().remove(id);

                                            player.sendSystemMessage(Component.literal("Removed title: " + id));
                                            return 1;
                                        })
                                )
                        )
                        // -----------------------------------------
                        // SELECT
                        // -----------------------------------------
                        .then(Commands.literal("select")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String id = StringArgumentType.getString(ctx, "id");

                                            var data = player.getData(TitleAttachment.TITLES);

                                            if (!data.has(id)) {
                                                player.sendSystemMessage(Component.literal("You do not have this title."));
                                                return 0;
                                            }

                                            data.select(id);
                                            player.refreshDisplayName();
                                            player.sendSystemMessage(Component.literal("Selected title: " + id));
                                            return 1;

                                        })
                                )
                        )
                        // -----------------------------------------
                        // CLEAR
                        // -----------------------------------------
                        .then(Commands.literal("clear")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    var data = player.getData(TitleAttachment.TITLES);

                                    data.select(null);
                                    player.sendSystemMessage(Component.literal("Cleared selected title."));
                                    return 1;
                                })
                        )
        );
    }
}