package net.lazy.kobe.registry;

import net.lazy.kobe.boss.CopengamblerEntity;
import net.lazy.kobe.boss.GambleScreenEntity;
import net.lazy.kobe.boss.StoneColossusEntity;
import net.lazy.kobe.boss.projectile.RockProjectileEntity;
import net.lazy.kobe.mobs.TieredSkeleton;
import net.lazy.kobe.mobs.TieredZombie;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, "kobe");

    public static final DeferredHolder<EntityType<?>, EntityType<StoneColossusEntity>> STONE_COLOSSUS =
            ENTITIES.register(
                    "stone_colossus",
                    () -> EntityType.Builder
                            .of(StoneColossusEntity::new, MobCategory.MONSTER)
                            .sized(1.8F, 3.5F)
                            .fireImmune()
                            .build("stone_colossus")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<CopengamblerEntity>> COPENGAMBLER =
            ENTITIES.register(
                    "copengambler",
                    () -> EntityType.Builder
                            .of(CopengamblerEntity::new, MobCategory.MONSTER)
                            .sized(1.0F, 3.0F)
                            .fireImmune()
                            .build("copengambler")
            );
    public static final DeferredHolder<EntityType<?>, EntityType<RockProjectileEntity>> ROCK_PROJECTILE =
            ENTITIES.register(
                    "rock_projectile",
                    () -> EntityType.Builder.<RockProjectileEntity>of(
                                    RockProjectileEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .build("rock_projectile")
            );
    // 🎰 GAMBLE SCREEN (visual-only attack helper)
// ModEntities.java
    public static final DeferredHolder<EntityType<?>, EntityType<GambleScreenEntity>> GAMBLE_SCREEN =
            ENTITIES.register("gamble_screen", () ->
                    EntityType.Builder.<GambleScreenEntity>of(
                                    GambleScreenEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(1.5F, 1.0F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("gamble_screen") // 🔥 FIXED
            );
    public static final DeferredHolder<EntityType<?>, EntityType<TieredZombie>> TIERED_ZOMBIE =
            ENTITIES.register(
                    "tiered_zombie",
                    () -> EntityType.Builder
                            .of(TieredZombie::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("tiered_zombie")
            );
    public static final DeferredHolder<EntityType<?>, EntityType<TieredSkeleton>> TIERED_SKELETON =
            ENTITIES.register(
                    "tiered_skeleton",
                    () -> EntityType.Builder
                            .of(TieredSkeleton::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("tiered_skeleton")
            );
}
