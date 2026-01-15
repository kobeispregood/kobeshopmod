package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.mining.MiningData;
import net.lazy.kobe.mining.MiningSyncPacket;

import net.lazy.kobe.farming.FarmingAttachment;
import net.lazy.kobe.farming.FarmingData;
import net.lazy.kobe.farming.FarmingSyncPacket;

import net.lazy.kobe.combat.CombatAttachment;
import net.lazy.kobe.combat.CombatData;
import net.lazy.kobe.combat.CombatSyncPacket;

import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ResetSkillCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("mastery")

                        // ================= RESET =================
                        .then(Commands.literal("reset")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String skill = StringArgumentType.getString(ctx, "skill").toLowerCase();

                                            switch (skill) {

                                                case "mining" -> {
                                                    MiningData data = player.getData(MiningAttachment.MINING);
                                                    data.reset();

                                                    NetworkHandler.sendToPlayer(
                                                            new MiningSyncPacket(data.getXp()),
                                                            player
                                                    );

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§aMining Mastery reset."),
                                                            false
                                                    );
                                                }

                                                case "farming" -> {
                                                    FarmingData data = player.getData(FarmingAttachment.FARMING);
                                                    data.reset();

                                                    NetworkHandler.sendToPlayer(
                                                            new FarmingSyncPacket(
                                                                    data.getXp(),
                                                                    data.getClaimedLevels()
                                                                            .stream()
                                                                            .mapToInt(i -> i)
                                                                            .toArray()
                                                            ),
                                                            player
                                                    );

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§aFarming Mastery reset."),
                                                            false
                                                    );
                                                }

                                                case "combat" -> {
                                                    CombatData data = player.getData(CombatAttachment.COMBAT);

                                                    // reset combat state (MATCHES MINING / FARMING)
                                                    data.reset();

                                                    NetworkHandler.sendToPlayer(
                                                            new CombatSyncPacket(
                                                                    data.getLevel(),        // 0
                                                                    data.getXpIntoLevel(),  // 0
                                                                    new int[0]
                                                            ),
                                                            player
                                                    );

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§aCombat Mastery reset."),
                                                            false
                                                    );
                                                }

                                                default -> ctx.getSource().sendFailure(
                                                        Component.literal("§cUnknown mastery: " + skill)
                                                );
                                            }

                                            return 1;
                                        })
                                )
                        )

                        // ================= SET =================
                        .then(Commands.literal("set")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {

                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    String skill = StringArgumentType.getString(ctx, "skill").toLowerCase();
                                                    int level = IntegerArgumentType.getInteger(ctx, "level");

                                                    switch (skill) {

                                                        case "mining" -> {
                                                            MiningData data = player.getData(MiningAttachment.MINING);
                                                            data.setLevel(level);

                                                            NetworkHandler.sendToPlayer(
                                                                    new MiningSyncPacket(data.getXp()),
                                                                    player
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§aMining Mastery set to level " + level
                                                                    ),
                                                                    false
                                                            );
                                                        }

                                                        case "farming" -> {
                                                            FarmingData data = player.getData(FarmingAttachment.FARMING);
                                                            data.setLevel(level);

                                                            NetworkHandler.sendToPlayer(
                                                                    new FarmingSyncPacket(
                                                                            data.getXp(),
                                                                            data.getClaimedLevels()
                                                                                    .stream()
                                                                                    .mapToInt(i -> i)
                                                                                    .toArray()
                                                                    ),
                                                                    player
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§aFarming Mastery set to level " + level
                                                                    ),
                                                                    false
                                                            );
                                                        }

                                                        case "combat" -> {
                                                            CombatData data = player.getData(CombatAttachment.COMBAT);
                                                            data.setLevel(level);

                                                            NetworkHandler.sendToPlayer(
                                                                    new CombatSyncPacket(
                                                                            data.getLevel(),
                                                                            data.getXpIntoLevel(),
                                                                            data.getClaimedRewards()
                                                                                    .stream()
                                                                                    .mapToInt(i -> i)
                                                                                    .toArray()
                                                                    ),
                                                                    player
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§aCombat Mastery set to level " + level
                                                                    ),
                                                                    false
                                                            );
                                                        }

                                                        default -> ctx.getSource().sendFailure(
                                                                Component.literal("§cUnknown mastery: " + skill)
                                                        );
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
        );

        // Alias: /kobe mastery ...
        dispatcher.register(
                Commands.literal("kobe")
                        .then(Commands.literal("mastery")
                                .redirect(dispatcher.getRoot().getChild("mastery")))
        );
    }
}
