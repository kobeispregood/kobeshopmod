package net.lazy.kobe.world;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(IslandCommands.register());
        dispatcher.register(IslandCommands.registerHub());
        dispatcher.register(IslandCommands.register());
        dispatcher.register(IslandCommands.registerHub());
        dispatcher.register(IslandCommands.registerSpawn());  // <--- ADD THIS

    }
}
