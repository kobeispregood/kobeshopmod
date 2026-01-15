package net.lazy.kobe.world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

public class IslandDimension {

    public static final ResourceKey<Level> ISLAND_WORLD =
            ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath("kobe", "islands")
            );
}