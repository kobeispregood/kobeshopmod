package net.lazy.kobe.item.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

public class EthansEnd extends SwordItem {

    private static final ResourceLocation DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "ethans_end_damage");

    private static final ResourceLocation SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "ethans_end_speed");
    public EthansEnd(Item.Properties properties) {
        super(Tiers.DIAMOND, properties
                .attributes(createAttributes())
        );
    }

    private static ItemAttributeModifiers createAttributes() {

        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                DAMAGE_ID,
                                12.0,
                                Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                SPEED_ID,
                                -2.2,
                                Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {

            // Check cooldown
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }

            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = start.add(look.scale(14));

            HitResult result = level.clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));

            Vec3 targetPos;

            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) result;
                targetPos = blockHit.getLocation().subtract(look.scale(0.5));
            } else {
                targetPos = end;
            }

            BlockPos blockPos = BlockPos.containing(targetPos);

            if (level.getBlockState(blockPos).isAir()
                    && level.getBlockState(blockPos.above()).isAir()) {

                player.teleportTo(
                        targetPos.x,
                        targetPos.y,
                        targetPos.z
                );

                level.playSound(
                        null,
                        blockPos,
                        SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.HOSTILE,
                        1.8F,
                        1.0F
                );

                player.getCooldowns().addCooldown(this, 200);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Ethan's End")
                .withStyle(ChatFormatting.DARK_PURPLE);
}}
