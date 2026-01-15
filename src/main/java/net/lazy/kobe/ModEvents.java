package net.lazy.kobe;

import net.lazy.kobe.shop.ShopCommand;
import net.lazy.kobe.shop.ShopAdminCommand;
import net.lazy.kobe.skills.SkillsCommand;
import net.lazy.kobe.command.ResetSkillCommand;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = KobeMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {

        new ShopCommand(event.getDispatcher());
        SkillsCommand.register(event.getDispatcher());
        ShopAdminCommand.register(event.getDispatcher());
        ResetSkillCommand.register(event.getDispatcher());
    }
}
