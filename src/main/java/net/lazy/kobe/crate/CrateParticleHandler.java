package net.lazy.kobe.crate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Random;

@EventBusSubscriber(
        modid = "kobe",
        bus = EventBusSubscriber.Bus.GAME,
        value = Dist.CLIENT
)
public class CrateParticleHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Throttle for performance
        if (mc.player.tickCount % 5 != 0) return;

        Level level = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        BlockPos.betweenClosedStream(
                playerPos.offset(-5, -3, -5),
                playerPos.offset(5, 3, 5)
        ).forEach(pos -> {

            if (!(level.getBlockState(pos).getBlock() instanceof CrateBlock)) return;
            if (!(level.getBlockEntity(pos) instanceof CrateBlockEntity crate)) return;

            // ✅ CORRECT METHOD
            CrateType type = crate.getCrateType();

            double x = pos.getX() + 0.5 + RANDOM.nextGaussian() * 0.2;
            double y = pos.getY() + 1.1;
            double z = pos.getZ() + 0.5 + RANDOM.nextGaussian() * 0.2;

            /* =========================
             *  LEGENDARY CRATE (GOLD)
             * ========================= */
            if (type == CrateType.LEGENDARY) {

                level.addParticle(
                        ParticleTypes.FLAME,
                        x, y, z,
                        0, 0.01, 0
                );

                if (RANDOM.nextFloat() < 0.4f) {
                    level.addParticle(
                            ParticleTypes.END_ROD,
                            x, y + 0.1, z,
                            0, 0.02, 0
                    );
                }
            }

            /* =========================
             *  RARE CRATE (PURPLE)
             * ========================= */
            else if (type == CrateType.RARE) {

                level.addParticle(
                        ParticleTypes.PORTAL,
                        x, y, z,
                        0, 0.02, 0
                );

                level.addParticle(
                        ParticleTypes.ENCHANT,
                        x, y, z,
                        0, 0.02, 0
                );
            }

            /* =========================
             *  COMMON / DEFAULT
             * ========================= */
            else {

                level.addParticle(
                        ParticleTypes.ENCHANT,
                        x, y, z,
                        0, 0.015, 0
                );
            }
        });
    }
}
