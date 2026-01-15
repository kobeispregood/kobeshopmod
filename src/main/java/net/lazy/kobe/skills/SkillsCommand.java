// net/lazy/kobe/skills/SkillsCommand.java
package net.lazy.kobe.skills;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class SkillsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("skills")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            player.openMenu(new SimpleMenuProvider(
                                    (id, inv, p) -> new SkillsMenu(id, inv),
                                    Component.literal("Skills")
                            ));

                            return 1;
                        })
        );
    }
}
