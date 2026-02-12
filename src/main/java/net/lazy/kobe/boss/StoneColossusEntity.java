package net.lazy.kobe.boss;

import net.lazy.kobe.boss.projectile.RockProjectileEntity;
import net.lazy.kobe.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class StoneColossusEntity extends Monster implements GeoEntity {

    /* ===================== TUNING ===================== */

    private static final double SLAM_TRIGGER_RANGE = 9.0;
    private static final double SLAM_HIT_RADIUS = 8.0;

    private static final int SLAM_IMPACT_TICK = 64;
    private static final int SLAM_TOTAL_TICKS = 130;
    private static final int SLAM_COOLDOWN_TICKS = 400;

    /* ===================== DATA ===================== */

    private static final EntityDataAccessor<Boolean> SLAMMING =
            SynchedEntityData.defineId(StoneColossusEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache animCache =
            new SingletonAnimatableInstanceCache(this);

    private boolean slamAnimStarted = false;
    private boolean slamDidImpact = false;
    private boolean windupSoundPlayed = false;

    private int slamTicks = 0;
    private int slamCooldown = 0;
    private int rockThrowCooldown = 0;

    private final ServerBossEvent bossBar =
            new ServerBossEvent(
                    Component.translatable(getType().getDescriptionId()),
                    BossEvent.BossBarColor.RED,
                    BossEvent.BossBarOverlay.PROGRESS
            );

    /* ================= CONSTRUCTOR ================= */

    public StoneColossusEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
    }

    /* ================= SYNCED DATA ================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLAMMING, false);
    }

    private boolean isSlamming() {
        return entityData.get(SLAMMING);
    }

    private void setSlamming(boolean value) {
        entityData.set(SLAMMING, value);
    }

    /* ================= ATTRIBUTES ================= */

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    /* ================= AI GOALS ================= */

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 48.0F));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        targetSelector.addGoal(
                1,
                new NearestAttackableTargetGoal<>(this, Player.class, true)
        );
    }

    /* ================= TICK ================= */

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            float pct = getHealth() / getMaxHealth();
            bossBar.setProgress(Mth.clamp(pct, 0.0F, 1.0F));
            bossBar.setName(Component.translatable(getType().getDescriptionId()));
        }

        if (slamCooldown > 0) slamCooldown--;
        if (rockThrowCooldown > 0) rockThrowCooldown--;

        LivingEntity target = getTarget();
        boolean enraged = getHealth() < getMaxHealth() * 0.5F;

        if (!level().isClientSide && target != null && !isSlamming() && slamCooldown <= 0) {
            if (distanceTo(target) <= SLAM_TRIGGER_RANGE && hasLineOfSight(target)) {
                startSlam();
            }
        }

        if (!level().isClientSide
                && target != null
                && !isSlamming()
                && rockThrowCooldown <= 0
                && distanceTo(target) > 7.0
                && distanceTo(target) < 34.0
                && hasLineOfSight(target)) {

            throwRockAt(target);
            rockThrowCooldown = enraged ? 30 : 50;
        }

        if (isSlamming()) {
            getNavigation().stop();
            slamTicks++;

            // 🔊 Play wind-up sound ONCE
            if (!windupSoundPlayed && slamTicks == 1 && !level().isClientSide) {
                windupSoundPlayed = true;
                level().playSound(
                        null,
                        blockPosition(),
                        ModSounds.COLOSSUS_WINDUP.get(),
                        SoundSource.HOSTILE,
                        3.0F,
                        1.0F
                );
            }

            if (!slamDidImpact && slamTicks >= SLAM_IMPACT_TICK && !level().isClientSide) {
                slamDidImpact = true;
                doSlamImpact();
            }

            if (slamTicks >= SLAM_TOTAL_TICKS && !level().isClientSide) {
                endSlam();
            }
        }
    }

    private void startSlam() {
        if (level().isClientSide) return;

        setSlamming(true);
        slamTicks = 0;
        slamDidImpact = false;
        slamAnimStarted = false;
        windupSoundPlayed = false;
        slamCooldown = SLAM_COOLDOWN_TICKS;
    }

    private void endSlam() {
        slamTicks = 0;
        setSlamming(false);
        setAggressive(true);
    }

    /* ================= SLAM IMPACT ================= */

    private void breakPlayerShield(Player player) {
        if (!player.isBlocking()) return;

        player.disableShield();

        EquipmentSlot slot =
                player.getUsedItemHand() == InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND;

        ItemStack shield = player.getItemBySlot(slot);
        if (!shield.isEmpty()) {
            shield.hurtAndBreak(1000, player, slot);
        }
    }

    private void doSlamImpact() {
        if (!(level() instanceof ServerLevel server)) return;

        BlockState ground = level().getBlockState(blockPosition().below());
        if (ground.isAir()) ground = Blocks.STONE.defaultBlockState();

        server.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, ground),
                getX(), getY(), getZ(),
                180,
                3.5, 0.3, 3.5,
                0.35
        );

        level().playSound(
                null,
                blockPosition(),
                ModSounds.COLOSSUS_SLAM.get(),
                SoundSource.HOSTILE,
                3.5F,
                1.0F
        );

        AABB area = getBoundingBox().inflate(SLAM_HIT_RADIUS);

        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == this) continue;

            double dist = distanceTo(e);

            if (e instanceof Player player) {
                breakPlayerShield(player);
            }

            float damage = (float) Mth.clamp(
                    34.0 - (dist * 3.5),
                    14.0,
                    34.0
            );

            e.hurt(damageSources().mobAttack(this), damage);

            double kb = Mth.clamp(1.4 - (dist * 0.12), 0.5, 1.4);

            e.push(
                    (e.getX() - getX()) * kb,
                    1.0,
                    (e.getZ() - getZ()) * kb
            );
        }
    }

    /* ================= DEATH MESSAGE ================= */

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (level().isClientSide) return;

        Component msg = Component.literal("⚔ The Stone Colossus has been defeated!")
                .withStyle(ChatFormatting.GOLD);

        for (ServerPlayer player : ((ServerLevel) level()).players()) {
            player.sendSystemMessage(msg);
        }
    }

    /* ================= ROCK THROW ================= */

    private void throwRockAt(LivingEntity target) {
        RockProjectileEntity rock = new RockProjectileEntity(level(), this);
        rock.setPos(getX(), getEyeY() - 0.2, getZ());

        double dx = target.getX() - getX();
        double dy = target.getEyeY() - rock.getY();
        double dz = target.getZ() - getZ();

        float speed = getHealth() < getMaxHealth() * 0.5F ? 2.6F : 2.2F;
        rock.shoot(dx, dy, dz, speed, 0.10F);

        level().addFreshEntity(rock);
    }

    /* ================= GECKOLIB ================= */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(
                this, "movement", 5,
                state -> {
                    if (isSlamming()) return PlayState.STOP;

                    return state.isMoving()
                            ? state.setAndContinue(RawAnimation.begin().thenLoop("animation.stone_colossus.walk"))
                            : state.setAndContinue(RawAnimation.begin().thenLoop("animation.stone_colossus.idle"));
                }
        ));

        controllers.add(new AnimationController<>(
                this, "slam",
                state -> {
                    if (!isSlamming()) {
                        slamAnimStarted = false;
                        return PlayState.STOP;
                    }

                    if (!slamAnimStarted) {
                        state.getController().forceAnimationReset();
                        slamAnimStarted = true;
                    }

                    return state.setAndContinue(
                            RawAnimation.begin()
                                    .thenPlay("animation.stone_colossus.windup")
                                    .thenPlay("animation.stone_colossus.slam")
                                    .thenPlay("animation.stone_colossus.recovery")
                    );
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }

    /* ================= BOSS BAR ================= */

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossBar.removePlayer(player);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
