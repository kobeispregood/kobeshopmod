package net.lazy.kobe.curios;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.menu.BlakeyBagData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

@EventBusSubscriber(modid = "kobe")
public class RileysRamen extends Item {

    // 5 minutes
    private static final int COOLDOWN_TICKS = 20 * 60 * 5;
    // 20 seconds
    private static final int SATURATION_TICKS = 20 * 20;

    // Stored on the ITEMSTACK
    private static final String KEY_LAST_USE = "kobe_rileys_ramen_last_use";

    public RileysRamen(Properties properties) {
        super(properties);
    }

    /* =============================================================
     * SERVER: apply effect + write cooldown onto ITEMSTACK
     * ============================================================= */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // run once per second
        if (player.tickCount % 20 != 0) return;

        ItemStack ramenStack = ItemStack.EMPTY;
        ItemStack bagStack = ItemStack.EMPTY;
        boolean fromBag = false;

        // -------------------------------------------------
        // NORMAL EQUIP
        // -------------------------------------------------
        var curioOpt = CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findFirstCurio(ModItems.RILEYS_RAMEN.get()));

        if (curioOpt.isPresent()) {
            ramenStack = curioOpt.get().stack();
        } else {
            // -------------------------------------------------
            // INSIDE BLAKEY BAG
            // -------------------------------------------------
            var bagOpt = CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> inv.findFirstCurio(ModItems.BLAKEY_BAG.get()));

            if (bagOpt.isPresent()) {
                bagStack = bagOpt.get().stack();

                var items = BlakeyBagData.getItems(
                        bagStack,
                        player.level().registryAccess()
                );

                for (ItemStack s : items) {
                    if (s.is(ModItems.RILEYS_RAMEN.get())) {
                        ramenStack = s;
                        fromBag = true;
                        break;
                    }
                }
            }
        }

        if (ramenStack.isEmpty()) return;

        // -------------------------------------------------
        // COOLDOWN (ITEMSTACK-BASED)
        // -------------------------------------------------
        CustomData cd = ramenStack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        long lastUse = cd.copyTag().getLong(KEY_LAST_USE);
        long gameTime = player.level().getGameTime();

        if (gameTime - lastUse < COOLDOWN_TICKS) return;

        // -------------------------------------------------
        // APPLY EFFECT
        // -------------------------------------------------
        player.addEffect(new MobEffectInstance(
                MobEffects.SATURATION,
                SATURATION_TICKS,
                0,
                true,
                false
        ));

        // -------------------------------------------------
        // WRITE COOLDOWN TO STACK
        // -------------------------------------------------
        CustomData.update(
                DataComponents.CUSTOM_DATA,
                ramenStack,
                tag -> tag.putLong(KEY_LAST_USE, gameTime)
        );

        // -------------------------------------------------
        // PERSIST BACK INTO BAG IF NEEDED
        // -------------------------------------------------
        if (fromBag && !bagStack.isEmpty()) {
            BlakeyBagData.save(
                    bagStack,
                    BlakeyBagData.getItems(
                            bagStack,
                            player.level().registryAccess()
                    ),
                    player.level().registryAccess()
            );
        }
    }

    /* =============================================================
     * CLIENT TOOLTIP: LIVE COOLDOWN TIMER
     * ============================================================= */
    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.literal("Grants Saturation for 20s every 5 minutes")
                        .withStyle(ChatFormatting.GRAY)
        );

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        long gameTime = mc.level.getGameTime();

        CustomData cd = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        long lastUse = cd.copyTag().getLong(KEY_LAST_USE);
        long remaining = COOLDOWN_TICKS - (gameTime - lastUse);

        if (remaining <= 0) {
            tooltip.add(
                    Component.literal("✔ Ready")
                            .withStyle(ChatFormatting.GREEN)
            );
        } else {
            long seconds = remaining / 20;
            long minutes = seconds / 60;
            seconds %= 60;

            tooltip.add(
                    Component.literal(
                            "Next bowl in " + minutes + ":" + String.format("%02d", seconds)
                    ).withStyle(ChatFormatting.YELLOW)
            );
        }
    }
}