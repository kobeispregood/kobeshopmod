package net.lazy.kobe.world;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IslandRegistry {

    private static final List<ResourceLocation> ISLANDS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        // Add your island structure files here
        ISLANDS.add(ResourceLocation.fromNamespaceAndPath("kobe", "skyisland"));
        // Example for future islands:
        // ISLANDS.add(ResourceLocation.fromNamespaceAndPath("kobe", "skyisland_desert"));
        // ISLANDS.add(ResourceLocation.fromNamespaceAndPath("kobe", "skyisland_snow"));
    }

    public static ResourceLocation getRandomIsland() {
        return ISLANDS.get(RANDOM.nextInt(ISLANDS.size()));
    }
}
