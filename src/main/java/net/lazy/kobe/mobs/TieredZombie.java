package net.lazy.kobe.mobs;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class TieredZombie extends Zombie {

    // =============================================================
    // SYNCED DATA
    // =============================================================

    private static final EntityDataAccessor<Integer> TIER =
            SynchedEntityData.defineId(TieredZombie.class, EntityDataSerializers.INT);

    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public TieredZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    // =============================================================
    // DATA REGISTRATION (1.21+ CORRECT VERSION)
    // =============================================================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TIER, 0);
    }

    // =============================================================
    // TIER LOGIC
    // =============================================================

    public void setTier(MobTier tier) {
        this.entityData.set(TIER, tier.ordinal());
        applyTierStats();
    }

    public MobTier getTier() {
        return MobTier.values()[this.entityData.get(TIER)];
    }

    private void applyTierStats() {

        MobTier tier = getTier();
        int tierIndex = tier.ordinal();

        double health = 20.0 * Math.pow(3, tierIndex);
        double damage = 4.0 * Math.pow(1.9, tierIndex);

        if (getAttribute(Attributes.MAX_HEALTH) != null) {
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        }

        if (getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        }

        if (getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
            getAttribute(Attributes.ATTACK_KNOCKBACK)
                    .setBaseValue(tierIndex >= 2 ? 1.5 + tierIndex * 0.5 : 0.0);
        }

        setHealth((float) health);
        setCustomName(formatTierName(tier));
        setCustomNameVisible(true);

        if (tier == MobTier.TIER_5) {
            igniteForTicks(Integer.MAX_VALUE);
        }

        if (tier == MobTier.TIER_4) {
            setTicksFrozen(200);
        }
    }

    private Component formatTierName(MobTier tier) {

        return switch (tier) {

            case TIER_1 ->
                    Component.literal("Tier I Zombie")
                            .withStyle(net.minecraft.ChatFormatting.WHITE);

            case TIER_2 ->
                    Component.literal("Tier II Zombie")
                            .withStyle(net.minecraft.ChatFormatting.GREEN);

            case TIER_3 ->
                    Component.literal("Tier III Zombie")
                            .withStyle(net.minecraft.ChatFormatting.DARK_GREEN);

            case TIER_4 ->
                    Component.literal("❄ Frozen Zombie")
                            .withStyle(net.minecraft.ChatFormatting.DARK_BLUE);

            case TIER_5 ->
                    Component.literal("🔥 Infernal Zombie")
                            .withStyle(net.minecraft.ChatFormatting.RED);
        };
    }

    // =============================================================
    // COMBAT EFFECTS
    // =============================================================

    @Override
    public boolean doHurtTarget(Entity target) {

        boolean success = super.doHurtTarget(target);
        if (!success) return false;

        if (target instanceof LivingEntity living) {

            if (getTier() == MobTier.TIER_4) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
                living.setTicksFrozen(Math.min(living.getTicksFrozen() + 120, 200));
            }

            if (getTier() == MobTier.TIER_5) {
                living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, 1));
                living.igniteForTicks(100);
            }
        }

        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (getTier() == MobTier.TIER_5 && source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }

        return super.hurt(source, amount);
    }

    // =============================================================
    // PARTICLES
    // =============================================================

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {

            if (getTier() == MobTier.TIER_4) {
                level().addParticle(ParticleTypes.SNOWFLAKE, getX(), getY() + 1, getZ(), 0, 0.02, 0);
            }

            if (getTier() == MobTier.TIER_5) {
                level().addParticle(ParticleTypes.FLAME, getX(), getY() + 1, getZ(), 0, 0.02, 0);
            }
        }
    }

    // =============================================================
    // EXPLOSION
    // =============================================================

    @Override
    public void die(DamageSource source) {

        if (!level().isClientSide && getTier() == MobTier.TIER_5) {
            level().explode(this, getX(), getY(), getZ(), 3.5F, Level.ExplosionInteraction.NONE);
        }

        super.die(source);
    }

    // =============================================================
    // SPAWN
    // =============================================================

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        applyTierStats();
        return data;
    }

    // =============================================================
    // ATTRIBUTES
    // =============================================================

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0);
    }

    // =============================================================
    // SAVE / LOAD
    // =============================================================

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MobTier", this.entityData.get(TIER));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("MobTier")) {
            this.entityData.set(TIER, tag.getInt("MobTier"));
        }

        applyTierStats();
    }
    @Override
    protected boolean isSunBurnTick() {

        // Tier 5 keeps vanilla behavior (which won't matter since it's fire immune anyway)
        if (getTier() == MobTier.TIER_5) {
            return super.isSunBurnTick();
        }

        // Tier 1–4 never burn in sunlight
        return false;
    }
}