package net.lazy.kobe.client;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.boss.GambleScreenEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GambleScreenModel extends GeoModel<GambleScreenEntity> {

    @Override
    public ResourceLocation getModelResource(GambleScreenEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "geo/gamblescreen.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(GambleScreenEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "textures/entity/gamblescreen_skull.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(GambleScreenEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "animations/gamblescreen.animation.json"
        );
    }
}