package net.lazy.kobe.client.render;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.mobs.TieredZombie;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;

public class TieredZombieRenderer extends MobRenderer<TieredZombie, ZombieModel<TieredZombie>> {

    // 🔥 Lava Zombie (Tier 5)
    private static final ResourceLocation LAVA_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    KobeMod.MOD_ID,
                    "textures/entity/lavazombie.png"
            );

    // 🧊 Frozen Zombie (Tier 4 example if you want it)
    private static final ResourceLocation FROZEN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    KobeMod.MOD_ID,
                    "textures/entity/frozenzombie.png"
            );

    // 🧟 Vanilla Zombie Texture (Tier 1–3)
    private static final ResourceLocation VANILLA_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/entity/zombie/zombie.png"
            );

    public TieredZombieRenderer(EntityRendererProvider.Context context) {
        super(context,
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                0.5F);

        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(TieredZombie entity) {

        return switch (entity.getTier()) {

            case TIER_4 -> FROZEN_TEXTURE;

            case TIER_5 -> LAVA_TEXTURE;

            default -> VANILLA_TEXTURE;
        };
    }
}