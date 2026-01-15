package net.lazy.kobe.oregen;

import com.mojang.brigadier.CommandDispatcher;
import net.lazy.kobe.oregen.gui.OreGenMenu;
import net.lazy.kobe.shop.ShopEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class OreGenCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("oregen")
                        // /oregen upgrade
                        .then(Commands.literal("upgrade")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    ShopEvents.buyOreGenUpgrade(player);
                                    return 1;
                                })
                        )

                        // /oregen gui
                        .then(Commands.literal("gui")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    player.openMenu(new SimpleMenuProvider(
                                            (id, inv, p) -> new OreGenMenu(id, inv),
                                            Component.literal("Ore Generator")
                                    ));

                                    return 1;
                                })
                        )

                        // /oregen reset
                        .then(Commands.literal("reset")
                                .requires(src -> src.hasPermission(2)) // OP only
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    player.setData(OreGenAttachment.LEVEL, 0);

                                    player.sendSystemMessage(
                                            Component.literal("Ore generator level reset to 0.")
                                    );

                                    return 1;
                                })
                        )
        );
    }
}
