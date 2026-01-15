package net.lazy.kobe.crate.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.lazy.kobe.crate.CrateBlockEntity;
import net.lazy.kobe.crate.CrateState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class CrateRenderer implements BlockEntityRenderer<CrateBlockEntity> {

    public CrateRenderer(BlockEntityRendererProvider.Context context) {
        // Context required by NeoForge, not used
    }

    @Override
    public void render(
            CrateBlockEntity be,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        // Only render while opening
        if (be.getState() != CrateState.OPENING) return;
        if (be.getDisplayItem().isEmpty()) return;

        float time = be.getTicks() + partialTicks;

        poseStack.pushPose();

        // Center item above crate
        poseStack.translate(0.5D, 1.2D, 0.5D);

        // Spin animation
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 6.0F));

        Minecraft.getInstance().getItemRenderer().renderStatic(
                be.getDisplayItem(),
                ItemDisplayContext.GROUND,
                light,
                overlay,
                poseStack,
                buffer,
                be.getLevel(),
                0
        );

        poseStack.popPose();
    }
}
