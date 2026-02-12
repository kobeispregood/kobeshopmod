package net.lazy.kobe.client.render;

import net.lazy.kobe.boss.StoneColossusEntity;
import net.lazy.kobe.client.StoneColossusModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneColossusRenderer
        extends GeoEntityRenderer<StoneColossusEntity> {

    public StoneColossusRenderer(EntityRendererProvider.Context context) {
        super(context, new StoneColossusModel());
        this.shadowRadius = 1.6F;
    }
}
