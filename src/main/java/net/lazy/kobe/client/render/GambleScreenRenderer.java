package net.lazy.kobe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lazy.kobe.KobeMod;
import net.lazy.kobe.boss.GambleScreenEntity;
import net.lazy.kobe.client.GambleScreenModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GambleScreenRenderer extends GeoEntityRenderer<GambleScreenEntity> {

    private static final ResourceLocation SKULL =
            ResourceLocation.fromNamespaceAndPath(KobeMod.MOD_ID, "textures/entity/gamblescreen_skull.png");
    private static final ResourceLocation BAR =
            ResourceLocation.fromNamespaceAndPath(KobeMod.MOD_ID, "textures/entity/gamblescreen_bar.png");
    private static final ResourceLocation DIAMOND =
            ResourceLocation.fromNamespaceAndPath(KobeMod.MOD_ID, "textures/entity/gamblescreen_diamond.png");
    private static final ResourceLocation STAR =
            ResourceLocation.fromNamespaceAndPath(KobeMod.MOD_ID, "textures/entity/gamblescreen_star.png");

    public GambleScreenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GambleScreenModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            GambleScreenEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();

        // ✅ interpolate entity yaw ourselves
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        // ✅ rotate the whole model (this is what you were missing)
        // If it ends up backwards, flip the sign or add 180 (see note below)
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));

        float scale = 0.55F;
        if (entity.isLocked()) scale *= 1.1F;
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(
            GambleScreenEntity animatable,
            ResourceLocation texture,
            MultiBufferSource bufferSource,
            float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(GambleScreenEntity entity) {
        return switch (entity.getSymbol()) {
            case SKULL -> SKULL;
            case BAR -> BAR;
            case DIAMOND -> DIAMOND;
            case STAR -> STAR;
        };
    }

    @Override
    protected int getBlockLightLevel(GambleScreenEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(GambleScreenEntity entity, BlockPos pos) {
        return 15;
    }
}