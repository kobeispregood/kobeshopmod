// net/lazy/kobe/command/CommandEvents.java
package net.lazy.kobe.command;

import net.lazy.kobe.skills.SkillsCommand;
import net.lazy.kobe.command.ResetSkillCommand;

import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.lazy.kobe.KobeMod;

@EventBusSubscriber(modid = KobeMod.MOD_ID)
public class CommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SkillsCommand.register(event.getDispatcher());
        ResetSkillCommand.register(event.getDispatcher());
        TestDamageCommand.register(event.getDispatcher());
        TitleCommand.register(event.getDispatcher());
    }
}