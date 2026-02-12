package net.lazy.kobe.registry;

import net.lazy.kobe.boss.CopengamblerEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEntityAttributes {

    public static void registerAttributes(EntityAttributeCreationEvent event) {

        System.out.println("REGISTERING ENTITY ATTRIBUTES");

        event.put(
                ModEntities.COPENGAMBLER.get(),
                CopengamblerEntity.createAttributes().build()
        );

        event.put(
                ModEntities.STONE_COLOSSUS.get(),
                net.lazy.kobe.boss.StoneColossusEntity.createAttributes().build()
        );
    }
}