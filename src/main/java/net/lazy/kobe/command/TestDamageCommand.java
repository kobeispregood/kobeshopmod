package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.combat.CombatStatsCalculator;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public class TestDamageCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("combat")
                        .requires(source -> source.hasPermission(2))

                        // /combat stats
                        .then(Commands.literal("stats")
                                .executes(TestDamageCommand::runStats))

                        // /combat damage <amount>
                        .then(Commands.literal("damage")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                        .executes(TestDamageCommand::runTestDamage)))
        );
    }

    /* =============================================================
     *  /combat stats
     * ============================================================= */
    private static int runStats(CommandContext<CommandSourceStack> context) {

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            CombatStats stats = CombatStatsCalculator.calculate(player);

            player.sendSystemMessage(Component.literal("§6=== Combat Stats ==="));
            player.sendSystemMessage(Component.literal("§cBase Damage Mult: §a" + stats.baseDamageMultiplier()));
            player.sendSystemMessage(Component.literal("§5Crit Chance: §a" + (stats.critChance() * 100) + "%"));
            player.sendSystemMessage(Component.literal("§eCrit Multiplier: §a" + stats.critMultiplier()));
            player.sendSystemMessage(Component.literal("§4Lifesteal: §a" + (stats.lifestealPercent() * 100) + "%"));
            player.sendSystemMessage(Component.literal("§6Defense: §a" + (stats.defence())));


            return 1;

        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Player only command."));
            return 0;
        }
    }

    /* =============================================================
     *  /combat damage <amount>
     * ============================================================= */
    private static int runTestDamage(CommandContext<CommandSourceStack> context) {

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            float amount = FloatArgumentType.getFloat(context, "amount");

            DamageSource source = player.damageSources().source(DamageTypes.GENERIC);
            player.hurt(source, amount);

            player.sendSystemMessage(
                    Component.literal("§cDamaged for " + amount + " HP.")
            );

            return 1;

        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Player only command."));
            return 0;
        }
    }
}