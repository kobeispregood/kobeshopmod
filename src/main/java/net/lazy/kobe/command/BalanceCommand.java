package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.lazy.kobe.econ.MoneyAPI;

public class BalanceCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // Main /balance command
        dispatcher.register(
                Commands.literal("balance")
                        // /balance
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayer();
                            if (p != null)
                                p.sendSystemMessage(Component.literal("§eYour balance: §a$" + MoneyAPI.get(p)));
                            return 1;
                        })

                        // /balance add <player> <amount>
                        .then(Commands.literal("add")
                                .requires(src -> src.hasPermission(2))
                                .then(
                                        Commands.argument("player", EntityArgument.player())
                                                .then(
                                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(ctx -> {
                                                                    ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                                    MoneyAPI.add(p, amount);

                                                                    ctx.getSource().sendSuccess(() ->
                                                                            Component.literal("§aAdded §e$" + amount + " §ato §e" + p.getName().getString()), true);
                                                                    return 1;
                                                                })
                                                )
                                ))

                        // /balance remove <player> <amount>
                        .then(Commands.literal("remove")
                                .requires(src -> src.hasPermission(2))
                                .then(
                                        Commands.argument("player", EntityArgument.player())
                                                .then(
                                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(ctx -> {
                                                                    ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                                    MoneyAPI.tryRemove(p, amount);

                                                                    ctx.getSource().sendSuccess(() ->
                                                                            Component.literal("§cRemoved §e$" + amount + " §cfrom §e" + p.getName().getString()), true);
                                                                    return 1;
                                                                })
                                                )
                                ))

                        // /balance set <player> <amount>
                        .then(Commands.literal("set")
                                .requires(src -> src.hasPermission(2))
                                .then(
                                        Commands.argument("player", EntityArgument.player())
                                                .then(
                                                        Commands.argument("amount", IntegerArgumentType.integer(0))
                                                                .executes(ctx -> {
                                                                    ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                                    MoneyAPI.set(p, amount);

                                                                    ctx.getSource().sendSuccess(() ->
                                                                            Component.literal("§eSet §a" + p.getName().getString() + "§e's balance to §a$" + amount), true);
                                                                    return 1;
                                                                })
                                                )
                                ))
        );

        // Proper Brigadier alias for /bal → /balance
        dispatcher.register(
                Commands.literal("bal")
                        .redirect(dispatcher.getRoot().getChild("balance"))
        );
    }
}
