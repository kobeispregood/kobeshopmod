package net.lazy.kobe.curios;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.menu.BlakeyBagData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

@EventBusSubscriber(modid = "kobe")
public class SwiftSocks extends Item {

    public static final ResourceLocation SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "swift_socks_speed");

    public SwiftSocks(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        ItemStack sockStack = ItemStack.EMPTY;

        // -------------------------------------------------
        // NORMAL EQUIP
        // -------------------------------------------------
        var curioOpt = CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findFirstCurio(ModItems.SWIFT_SOCKS.get()));

        if (curioOpt.isPresent()) {
            sockStack = curioOpt.get().stack();
        } else {
            // -------------------------------------------------
            // INSIDE BLAKEY BAG
            // -------------------------------------------------
            var bagOpt = CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> inv.findFirstCurio(ModItems.BLAKEY_BAG.get()));

            if (bagOpt.isPresent()) {
                var bagStack = bagOpt.get().stack();

                var items = BlakeyBagData.getItems(
                        bagStack,
                        player.level().registryAccess()
                );

                for (ItemStack s : items) {
                    if (s.is(ModItems.SWIFT_SOCKS.get())) {
                        sockStack = s;
                        break;
                    }
                }
            }
        }

        // -------------------------------------------------
        // REMOVE IF NOT EQUIPPED
        // -------------------------------------------------
        if (sockStack.isEmpty()) {
            speed.removeModifier(SPEED_ID);
            return;
        }

        // -------------------------------------------------
        // APPLY 15% SPEED BOOST
        // -------------------------------------------------
        speed.removeModifier(SPEED_ID);

        speed.addTransientModifier(
                new AttributeModifier(
                        SPEED_ID,
                        0.15,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.literal("Increases Speed by 15%")
                        .withStyle(ChatFormatting.YELLOW)
        );
    }
}