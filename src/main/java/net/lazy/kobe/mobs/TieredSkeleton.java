package net.lazy.kobe.mobs;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TieredSkeleton extends Skeleton {

    private static final EntityDataAccessor<Integer> TIER =
            SynchedEntityData.defineId(TieredSkeleton.class, EntityDataSerializers.INT);

    private RangedBowAttackGoal<TieredSkeleton> bowGoal;

    private int teleportCooldown = 0;
    private int teleportWarning = 0;

    public TieredSkeleton(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    /* ===================== NO SUN BURN ===================== */

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    /* ===================== GOALS ===================== */

    @Override
    protected void registerGoals() {
        super.registerGoals();
        bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 40, 15.0F);
        this.goalSelector.addGoal(4, bowGoal);
    }

    /* ===================== DATA ===================== */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TIER, 0);
    }

    public MobTier getTier() {
        return MobTier.values()[entityData.get(TIER)];
    }

    public void setTier(MobTier tier) {
        entityData.set(TIER, tier.ordinal());
        applyTierStats();
        equipTierBow();
    }

    /* ===================== STATS ===================== */

    private void applyTierStats() {
        int tierIndex = getTier().ordinal();

        double health = 18.0 * Math.pow(2.2, tierIndex);
        double damage = 4.0 * Math.pow(1.5, tierIndex);

        getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);

        setHealth((float) health);
        setCustomName(formatTierName(getTier()));
        setCustomNameVisible(true);
    }

    /* ===================== EQUIP ===================== */

    private void equipTierBow() {
        if (level().isClientSide) return;

        ItemStack bow = new ItemStack(Items.BOW);

        var registry = level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> power = registry.getHolderOrThrow(Enchantments.POWER);
        Holder<Enchantment> punch = registry.getHolderOrThrow(Enchantments.PUNCH);
        Holder<Enchantment> flame = registry.getHolderOrThrow(Enchantments.FLAME);

        if (getTier() == MobTier.TIER_5) {
            bow.enchant(power, 5);
            bow.enchant(punch, 2);
            bow.enchant(flame, 1);
        }

        setItemSlot(EquipmentSlot.MAINHAND, bow);
    }

    /* ===================== TICK ===================== */

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;

        if (teleportCooldown > 0) teleportCooldown--;
        if (teleportWarning > 0) {
            teleportWarning--;

            spawnFlameRing();

            if (teleportWarning == 0 && getTarget() != null) {
                teleportAway(getTarget());
            }
        }

        if (getTier() == MobTier.TIER_5) {

            LivingEntity target = getTarget();

            if (target != null && distanceTo(target) < 4 && teleportCooldown <= 0) {

                teleportWarning = 20; // 1 second warning
                teleportCooldown = 100 + random.nextInt(100); // 5–10 sec

                level().playSound(
                        null,
                        blockPosition(),
                        SoundEvents.BLAZE_SHOOT,
                        SoundSource.HOSTILE,
                        1.2F,
                        0.8F + random.nextFloat() * 0.4F
                );
            }

            handleHoming();
        }
    }

    /* ===================== TELEPORT ===================== */

    private void teleportAway(LivingEntity target) {

        Vec3 away = position().subtract(target.position()).normalize();

        double newX = getX() + away.x * 6;
        double newZ = getZ() + away.z * 6;

        teleportTo(newX, getY(), newZ);

        ((ServerLevel) level()).sendParticles(
                ParticleTypes.SMOKE,
                newX, getY() + 1, newZ,
                40, 0.6, 1, 0.6, 0
        );
    }

    private void spawnFlameRing() {

        for (int i = 0; i < 12; i++) {

            double angle = (Math.PI * 2 * i) / 12;

            double x = getX() + Math.cos(angle) * 2;
            double z = getZ() + Math.sin(angle) * 2;

            ((ServerLevel) level()).sendParticles(
                    ParticleTypes.FLAME,
                    x, getY(), z,
                    1, 0, 0, 0, 0
            );
        }
    }

    /* ===================== HOMING ===================== */

    private void handleHoming() {

        level().getEntitiesOfClass(AbstractArrow.class,
                        getBoundingBox().inflate(40),
                        arrow -> arrow.getPersistentData().getBoolean("InfernalArrow"))
                .forEach(arrow -> {

                    if (arrow.onGround()) {
                        if (arrow.tickCount > 10) arrow.discard();
                        return;
                    }

                    UUID id = arrow.getPersistentData().getUUID("Target");
                    Entity target = ((ServerLevel) level()).getEntity(id);

                    if (!(target instanceof LivingEntity living)) return;

                    if (arrow.tickCount > 15) return; // short homing

                    Vec3 toTarget = living.position().add(0, 0.6, 0)
                            .subtract(arrow.position());

                    Vec3 direction = toTarget.normalize();

                    Vec3 newMotion = arrow.getDeltaMovement().scale(0.95)
                            .add(direction.scale(0.08));

                    arrow.setDeltaMovement(newMotion);
                });
    }

    /* ===================== SHOOT ===================== */

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {

        if (getTier() != MobTier.TIER_5) {
            super.performRangedAttack(target, velocity);
            return;
        }

        shootInfernal(target, -6);
        shootInfernal(target, 0);
        shootInfernal(target, 6);
    }

    private void shootInfernal(LivingEntity target, float offset) {

        AbstractArrow arrow = getArrow(
                new ItemStack(Items.ARROW),
                1.0F,
                getMainHandItem()
        );

        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        double baseAngle = Math.atan2(dz, dx);
        double angle = baseAngle + Math.toRadians(offset);

        arrow.shoot(Math.cos(angle), 0.28, Math.sin(angle), 1.8F, 1.5F);

        arrow.getPersistentData().putBoolean("InfernalArrow", true);
        arrow.getPersistentData().putUUID("Target", target.getUUID());

        level().addFreshEntity(arrow);
    }

    /* ===================== SAVE ===================== */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MobTier", entityData.get(TIER));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("MobTier")) {
            entityData.set(TIER, tag.getInt("MobTier"));
        }

        applyTierStats();
        equipTierBow();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        applyTierStats();
        equipTierBow();

        return data;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }
    private Component formatTierName(MobTier tier) {

        return switch (tier) {

            case TIER_1 ->
                    Component.literal("Tier I Skeleton")
                            .withStyle(net.minecraft.ChatFormatting.WHITE);

            case TIER_2 ->
                    Component.literal("Tier II Skeleton")
                            .withStyle(net.minecraft.ChatFormatting.GREEN);

            case TIER_3 ->
                    Component.literal("Tier III Skeleton")
                            .withStyle(net.minecraft.ChatFormatting.DARK_GREEN);

            case TIER_4 ->
                    Component.literal("❄ Frozen Skeleton")
                            .withStyle(net.minecraft.ChatFormatting.DARK_BLUE);

            case TIER_5 ->
                    Component.literal("🔥 Infernal Skeleton")
                            .withStyle(net.minecraft.ChatFormatting.RED);
        };
    }
}