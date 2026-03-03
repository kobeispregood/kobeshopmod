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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
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
    // === ADD THESE ===
    private boolean playHitAnim = false;
    private boolean playThrowAnim = false;

    private int slamTicks = 0;
    private int slamCooldown = 0;
    private int rockThrowCooldown = 0;
    private LivingEntity pendingMeleeTarget;
    private LivingEntity pendingThrowTarget;
    private boolean doingMelee = false;
    private int meleeTicks = 0;
    private boolean enragedPlayed = false;

    private boolean doingThrow = false;
    private int throwTicks = 0;

    private final ServerBossEvent bossBar =
            new ServerBossEvent(
                    Component.translatable(getType().getDescriptionId()),
                    BossEvent.BossBarColor.RED,
                    BossEvent.BossBarOverlay.PROGRESS
            );

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

        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false) {
            @Override
            public boolean canUse() {
                return !isSlamming() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isSlamming() && super.canContinueToUse();
            }
        });

        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 48.0F));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        targetSelector.addGoal(
                1,
                new NearestAttackableTargetGoal<>(this, Player.class, true)
        );

        targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(this, Monster.class, true) {

                    @Override
                    public boolean canUse() {
                        if (!super.canUse()) return false;

                        LivingEntity target = this.target;
                        return target != null
                                && target != StoneColossusEntity.this;
                    }
                }
        );

        targetSelector.addGoal(
                3,
                new HurtByTargetGoal(this).setAlertOthers()
        );
    }

    /* ================= DAMAGE BLOCK ================= */

    @Override
    public boolean doHurtTarget(Entity target) {
        if (doingMelee) return false;
        if (isSlamming()) return false;
        if (!(target instanceof LivingEntity living)) return false;

        if (!level().isClientSide) {
            doingMelee = true;
            meleeTicks = 0;
            pendingMeleeTarget = living;
            triggerAnim("attacks", "hit");
        }

        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        // Brief invulnerability during actual impact frame
        if (isSlamming() && slamTicks < SLAM_IMPACT_TICK + 6) {
            return false;
        }

        return super.hurt(source, amount);
    }

    /* ================= TICK ================= */

    @Override
    public void tick() {
        super.tick();

        // ================= BOSS BAR =================
        if (!level().isClientSide) {
            float pct = getHealth() / getMaxHealth();
            bossBar.setProgress(Mth.clamp(pct, 0.0F, 1.0F));
            bossBar.setName(Component.translatable(getType().getDescriptionId()));
        }

        // ================= COOLDOWNS =================
        if (slamCooldown > 0) slamCooldown--;
        if (rockThrowCooldown > 0) rockThrowCooldown--;

        LivingEntity target = getTarget();
        boolean enraged = getHealth() < getMaxHealth() * 0.25F;

        // ================= ENRAGE EFFECT =================
        if (enraged) {

            if (!enragedPlayed && !level().isClientSide) {
                enragedPlayed = true;

                level().playSound(
                        null,
                        blockPosition(),
                        ModSounds.COLOSSUS_WINDUP.get(),
                        SoundSource.HOSTILE,
                        3.0F,
                        0.6F
                );
            }

            if (level() instanceof ServerLevel server && tickCount % 4 == 0) {
                server.sendParticles(
                        ParticleTypes.FLAME,
                        getX(),
                        getY() + 1.2,
                        getZ(),
                        10,
                        0.7,
                        0.6,
                        0.7,
                        0.01
                );
            }
        }

        // ================= PASSIVE STONE AURA =================
        if (level() instanceof ServerLevel server) {
            if (tickCount % 6 == 0) {
                BlockState ground = level().getBlockState(blockPosition().below());
                if (ground.isAir()) ground = Blocks.STONE.defaultBlockState();

                server.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, ground),
                        getX(),
                        getY() + 0.1,
                        getZ(),
                        6,
                        0.6,
                        0.1,
                        0.6,
                        0.02
                );
            }
        }

// ================= HEAVY FOOTSTEP =================
        if (!isSlamming() && doingMelee == false && doingThrow == false) {
            if (onGround() && tickCount % 20 == 0 && level() instanceof ServerLevel server) {
                server.sendParticles(
                        ParticleTypes.CLOUD,
                        getX(),
                        getY(),
                        getZ(),
                        12,
                        0.8,
                        0.05,
                        0.8,
                        0.02
                );

                level().playSound(
                        null,
                        blockPosition(),
                        ModSounds.COLOSSUS_SLAM.get(),
                        SoundSource.HOSTILE,
                        0.6F,
                        0.5F
                );
            }
        }
        // ================= SLAM TRIGGER =================
        if (!level().isClientSide && target != null && !isSlamming() && slamCooldown <= 0) {
            if (distanceTo(target) <= SLAM_TRIGGER_RANGE && hasLineOfSight(target)) {
                startSlam();
            }
        }

        // ================= ROCK THROW TRIGGER =================
        if (!level().isClientSide
                && target != null
                && !isSlamming()
                && !doingMelee
                && rockThrowCooldown <= 0
                && distanceTo(target) > 7.0
                && distanceTo(target) < 34.0
                && hasLineOfSight(target)) {

            throwRockAt(target, enraged);
            rockThrowCooldown = enraged ? 20 : 50;
        }

        // ================= SLAM LOGIC =================
        if (isSlamming()) {
            getNavigation().stop();

            setYBodyRot(getYRot());
            yHeadRot = getYRot();

            slamTicks++;

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

                for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(20))) {
                    p.push(0, 0.2, 0);
                }
            }

            if (!slamDidImpact && slamTicks >= SLAM_IMPACT_TICK && !level().isClientSide) {
                slamDidImpact = true;
                doSlamImpact();
            }

            if (slamTicks >= SLAM_TOTAL_TICKS && !level().isClientSide) {
                endSlam();
            }
        }

        // ================= MELEE TIMING =================
// ================= MELEE TIMING =================
        if (doingMelee) {
            meleeTicks++;

            if (meleeTicks == 7) { // impact frame
                if (pendingMeleeTarget != null
                        && pendingMeleeTarget.isAlive()
                        && distanceTo(pendingMeleeTarget) < 4.0
                        && hasLineOfSight(pendingMeleeTarget)) {

                    dealMeleeDamageNow(pendingMeleeTarget);

                    this.push(
                            getLookAngle().x * 0.2,
                            0.1,
                            getLookAngle().z * 0.2
                    );

                    for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(8))) {
                        p.push(0, 0.15, 0);
                    }
                }
            }

            if (meleeTicks > 15) {
                doingMelee = false;
                pendingMeleeTarget = null;
            }
        }

        // ================= THROW TIMING =================
        if (doingThrow) {
            throwTicks++;

            // Throw charge particles
            if (level() instanceof ServerLevel server) {
                server.sendParticles(
                        ParticleTypes.CRIT,
                        getX(),
                        getEyeY(),
                        getZ(),
                        4,
                        0.4,
                        0.2,
                        0.4,
                        0.02
                );
            }

            if (throwTicks == 10) { // adjust to match visual release
                if (pendingThrowTarget != null && pendingThrowTarget.isAlive()) {
                    spawnRockNow(pendingThrowTarget);
                }
            }

            if (throwTicks > 20) {
                doingThrow = false;
                pendingThrowTarget = null;
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

        // temporary resistance
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, SLAM_TOTAL_TICKS, 2, false, false));
    }

    private void endSlam() {
        slamTicks = 0;
        setSlamming(false);
        setAggressive(true);
    }

    /* ================= SLAM IMPACT ================= */

    private void doSlamImpact() {
        if (!(level() instanceof ServerLevel server)) return;

        BlockState ground = level().getBlockState(blockPosition().below());
        if (ground.isAir()) ground = Blocks.STONE.defaultBlockState();

        server.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, ground),
                getX(), getY(), getZ(),
                220,
                3.8, 0.3, 3.8,
                0.35
        );

        level().playSound(
                null,
                blockPosition(),
                ModSounds.COLOSSUS_SLAM.get(),
                SoundSource.HOSTILE,
                4.0F,
                1.0F
        );

        AABB area = getBoundingBox().inflate(SLAM_HIT_RADIUS);

        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == this) continue;

            double dist = distanceTo(e);

            float damage = (float) Mth.clamp(
                    38.0 - (dist * 4.0),
                    16.0,
                    38.0
            );

            e.hurt(damageSources().mobAttack(this), damage);

            double kb = Mth.clamp(1.6 - (dist * 0.15), 0.6, 1.6);

            e.push(
                    (e.getX() - getX()) * kb,
                    1.2,
                    (e.getZ() - getZ()) * kb
            );
        }
    }

    /* ================= ROCK THROW ================= */

    private void throwRockAt(LivingEntity target, boolean enraged) {
        if (!level().isClientSide) {
            doingThrow = true;
            throwTicks = 0;
            pendingThrowTarget = target;
            triggerAnim("attacks", "throw");
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

    /* ================= GECKOLIB ================= */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        // ================= MOVEMENT =================
        controllers.add(new AnimationController<>(
                this,
                "movement",
                5,
                state -> {

                    if (isSlamming()) return PlayState.STOP;

                    return state.isMoving()
                            ? state.setAndContinue(
                            RawAnimation.begin()
                                    .thenLoop("animation.stone_colossus.walk"))
                            : state.setAndContinue(
                            RawAnimation.begin()
                                    .thenLoop("animation.stone_colossus.idle"));
                }
        ));

        // ================= SLAM =================
        controllers.add(new AnimationController<>(
                this,
                "slam",
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
                                    .thenPlay("animation.stone_colossus.recovery"));
                }
        ));

        // ================= ATTACKS (TRIGGER SYSTEM) =================
        // ================= ATTACKS (TRIGGER SYSTEM) =================
        controllers.add(
                new AnimationController<>(this, "attacks", 0, state -> PlayState.STOP)
                        .triggerableAnim(
                                "hit",
                                RawAnimation.begin()
                                        .thenPlay("animation.stone_colossus.hit"))
                        .triggerableAnim(
                                "throw",
                                RawAnimation.begin()
                                        .thenPlay("animation.stone_colossus.throw"))

        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
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
    private void spawnRockNow(LivingEntity target) {
        if (target == null || target.isRemoved()) return;

        RockProjectileEntity rock = new RockProjectileEntity(level(), this);
        rock.setPos(getX(), getEyeY() - 0.2, getZ());

        double dx = target.getX() - getX();
        double dy = target.getEyeY() - rock.getY();
        double dz = target.getZ() - getZ();

        rock.shoot(dx, dy, dz, 2.3F, 0.01F);
        level().addFreshEntity(rock);
    }

    private void dealMeleeDamageNow(LivingEntity target) {
        if (target == null || target.isRemoved()) return;

        target.hurt(
                damageSources().mobAttack(this),
                (float) getAttributeValue(Attributes.ATTACK_DAMAGE)
        );
    }
}