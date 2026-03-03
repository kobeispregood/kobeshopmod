package net.lazy.kobe.client.render;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.mobs.MobTier;
import net.lazy.kobe.mobs.TieredSkeleton;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class TieredSkeletonRenderer extends MobRenderer<TieredSkeleton, SkeletonModel<TieredSkeleton>> {

    private static final ResourceLocation VANILLA_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    private static final ResourceLocation FROZEN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    KobeMod.MOD_ID,
                    "textures/entity/frozenskeleton.png"
            );

    private static final ResourceLocation INFERNAL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    KobeMod.MOD_ID,
                    "textures/entity/infernalskeleton.png"
            );

    public TieredSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context,
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)),
                0.5F);

        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()
        ));

        // 🔥 THIS IS WHAT YOU WERE MISSING
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(TieredSkeleton entity) {

        MobTier tier = entity.getTier();

        if (tier == MobTier.TIER_4) {
            return FROZEN_TEXTURE;
        }

        if (tier == MobTier.TIER_5) {
            return INFERNAL_TEXTURE;
        }

        return VANILLA_TEXTURE;
    }
}