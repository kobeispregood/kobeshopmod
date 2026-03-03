package net.lazy.kobe.mobs;

import net.lazy.kobe.registry.ModEntities;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModMobAttributes {

    public static void register(EntityAttributeCreationEvent event) {

        event.put(
                ModEntities.STONE_COLOSSUS.get(),
                net.lazy.kobe.boss.StoneColossusEntity.createAttributes().build()
        );

        event.put(
                ModEntities.COPENGAMBLER.get(),
                net.lazy.kobe.boss.CopengamblerEntity.createAttributes().build()
        );

        event.put(
                ModEntities.TIERED_ZOMBIE.get(),
                TieredZombie.createAttributes().build()
        );

        event.put(
                ModEntities.TIERED_SKELETON.get(),
                TieredSkeleton.createAttributes().build()
        );


    }
}