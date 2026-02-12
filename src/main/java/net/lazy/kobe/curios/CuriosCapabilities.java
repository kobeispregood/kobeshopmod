package net.lazy.kobe.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.lazy.kobe.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public final class CuriosCapabilities {

    private CuriosCapabilities() {}

    private static final ResourceLocation DUNHAM_DICE_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "dunham_dice_damage"
            );

    public static void register(final RegisterCapabilitiesEvent evt) {

        evt.registerItem(
                CuriosCapability.ITEM,
                (stack, ctx) -> new ICurio() {

                    @Override
                    public ItemStack getStack() {
                        return stack;
                    }

                    // STEP 2 → TICK
                    @Override
                    public void curioTick(SlotContext slotContext) {
                        BlakeyBagLogic.tick(slotContext, stack);
                    }

                    // STEP 5 → ATTRIBUTES
                    @Override
                    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                            SlotContext slotContext,
                            ResourceLocation identifier
                    ) {
                        return BlakeyBagLogic.getAttributes(slotContext, stack);
                    }
                },
                ModItems.BLAKEY_BAG.get()
        );
        evt.registerItem(
                CuriosCapability.ITEM,
                (stack, ctx) -> new ICurio() {

                    @Override
                    public ItemStack getStack() {
                        return stack;
                    }

                    // ✅ THIS IS THE TICK (Curios calls this)
                    @Override
                    public void curioTick(SlotContext slotContext) {

                    }

                    // ✅ THIS IS WHERE ATTRIBUTES MUST LIVE
                    @Override
                    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                            SlotContext slotContext,
                            ResourceLocation identifier
                    ) {
                        return HashMultimap.create(); // ← no vanilla attributes
                    }
                },
                ModItems.DUNHAMDICE.get()
        );
    }
}
