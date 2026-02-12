package net.lazy.kobe.client;

import net.lazy.kobe.boss.StoneColossusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StoneColossusModel extends GeoModel<StoneColossusEntity> {

    @Override
    public ResourceLocation getModelResource(StoneColossusEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                "kobe",
                "geo/stone_colossus.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(StoneColossusEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                "kobe",
                "textures/entity/stone_colossus.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(StoneColossusEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                "kobe",
                "animations/stone_colossus.animation.json"
        );
    }
}
