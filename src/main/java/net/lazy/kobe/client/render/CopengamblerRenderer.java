package net.lazy.kobe.client.render;

import net.lazy.kobe.boss.CopengamblerEntity;
import net.lazy.kobe.client.CopengamblerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CopengamblerRenderer
        extends GeoEntityRenderer<CopengamblerEntity> {

    public CopengamblerRenderer(EntityRendererProvider.Context context) {
        super(context, new CopengamblerModel());
        this.shadowRadius = 0.9F; // tweak later based on model size
    }
}
