package net.lazy.kobe.curios;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.menu.BlakeyBagData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

import javax.management.Attribute;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = "kobe")
public class ComfortCloak extends Item {

    public ComfortCloak(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // run once per second
        if (player.tickCount % 20 != 0) return;

        ItemStack cloakStack = ItemStack.EMPTY;
        ItemStack bagStack = ItemStack.EMPTY;
        boolean fromBag = false;

        // -------------------------------------------------
        // NORMAL EQUIP
        // -------------------------------------------------
        var curioOpt = CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findFirstCurio(ModItems.COMFORT_CLOAK.get()));

        if (curioOpt.isPresent()) {
            cloakStack = curioOpt.get().stack();
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
                    if (s.is(ModItems.COMFORT_CLOAK.get())) {
                        cloakStack = s;
                        fromBag = true;
                        break;
                    }
                }
            }
        }

        if (cloakStack.isEmpty()) return;

        // -------------------------------------------------
        // COOLDOWN (ITEMSTACK-BASED)
        // -------------------------------------------------
        CustomData cd = cloakStack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
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
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealth != null) {
            AttributeModifier modifier = new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("kobe", "comfort_cloak"),
                    20.0,
                    AttributeModifier.Operation.ADD_VALUE
            );

            if (!maxHealth.hasModifier(modifier.id())) {
                maxHealth.addTransientModifier(modifier);
            }
        }
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.translatable("Increases +♥Health by 2 ")
                        .withStyle(ChatFormatting.RED)
        );
        }
    }
