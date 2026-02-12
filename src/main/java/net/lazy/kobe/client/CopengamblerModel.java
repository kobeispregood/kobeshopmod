package net.lazy.kobe.client;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.boss.CopengamblerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CopengamblerModel extends GeoModel<CopengamblerEntity> {

    @Override
    public ResourceLocation getModelResource(CopengamblerEntity object) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "geo/copengambler.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(CopengamblerEntity object) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "textures/entity/copengambler.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(CopengamblerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                KobeMod.MOD_ID,
                "animations/copengambler.animation.json"
        );
    }
}
