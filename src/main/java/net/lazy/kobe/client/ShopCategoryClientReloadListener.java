package net.lazy.kobe.client;

import net.lazy.kobe.shop.ShopCategoryLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class ShopCategoryClientReloadListener
        extends SimplePreparableReloadListener<Void> {

    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void ignored, ResourceManager manager, ProfilerFiller profiler) {
        ShopCategoryLoader.load(manager);
    }
}
