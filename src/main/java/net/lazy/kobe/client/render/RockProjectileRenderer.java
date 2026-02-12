package net.lazy.kobe.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.lazy.kobe.boss.projectile.RockProjectileEntity;

public class RockProjectileRenderer extends ThrownItemRenderer<RockProjectileEntity> {
    public RockProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, 1.0F, true);
    }
}
