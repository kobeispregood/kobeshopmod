package net.lazy.kobe.boss;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class CopengamblerEntity extends Monster implements GeoEntity {

    /* ===================== PHASE ===================== */

    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(CopengamblerEntity.class, EntityDataSerializers.INT);

    private static final int PHASE_IDLE = 0;
    private static final int PHASE_SLOT = 1;
    private static final int PHASE_RESOLVE = 2;
    private static final int PHASE_RECOVER = 3;

    /* ===================== TIMING ===================== */

    private static final int LEFT_SPAWN_TICK = 10;
    private static final int MID_SPAWN_TICK = 20;
    private static final int RIGHT_SPAWN_TICK = 30;
    private static final int SLOT_DURATION = 150;

    private static final int ROLL_SOUND_INTERVAL = 5;

    // ⏱️ SHORTENED END
    private static final int RESOLVE_DELAY = 10; // was 20
    private static final int RESOLVE_END = 20;   // was 30

    /* ===================== STATE ===================== */

    private final AnimatableInstanceCache animCache =
            new SingletonAnimatableInstanceCache(this);

    private final ServerBossEvent bossBar =
            new ServerBossEvent(getDisplayName(),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS);

    private int phaseTick = 0;
    private int rollSoundTick = 0;
    private boolean jackpotFired = false;

    private GambleScreenEntity left;
    private GambleScreenEntity middle;
    private GambleScreenEntity right;

    private GambleScreenEntity.ScreenSymbol forcedSymbol;

    private boolean isGambling() {
        return getPhase() == PHASE_SLOT || getPhase() == PHASE_RESOLVE;
    }

    public CopengamblerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 500;
    }

    /* ===================== ATTRIBUTES ===================== */

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5000)
                .add(Attributes.ATTACK_DAMAGE, 122)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    /* ===================== AI ===================== */

    @Override
    protected void registerGoals() {

        // ✅ Touch-up: block melee goal entirely while gambling (prevents swing attempts)
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.6, true) {
            @Override
            public boolean canUse() {
                return !isGambling() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isGambling() && super.canContinueToUse();
            }
        });

        goalSelector.addGoal(2, new RandomStrollGoal(this, 1.8));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
        targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(
                        this,
                        Monster.class,
                        true,
                        true
                ));
    }

    /* ===================== SYNC ===================== */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHASE, PHASE_IDLE);
    }

    /* ===================== BOSS BAR ===================== */

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

    /* ===================== MELEE DAMAGE BLOCK (YOUR REQUEST) ===================== */

    @Override
    public boolean doHurtTarget(Entity target) {
        // ✅ Touch-up: she cannot DEAL melee damage during gamble animation/phases
        if (isGambling()) {
            return false;
        }
        return super.doHurtTarget(target);
    }

    /* ===================== TICK ===================== */

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;

        bossBar.setProgress(getHealth() / getMaxHealth());
        phaseTick++;

        cleanupTempEntities();

        switch (getPhase()) {

            case PHASE_IDLE -> {
                if (phaseTick > 120 && getTarget() != null) {
                    startSlotPhase(server);
                }
            }

            case PHASE_SLOT -> {
                setDeltaMovement(Vec3.ZERO);

                // ✅ Touch-up: stop pathing jitter during slot
                getNavigation().stop();

                // ✅ ADD: "INVULNERABLE" VISUALS DURING SLOT (so players know hits won't work)
                if (phaseTick % 3 == 0) {
                    server.sendParticles(
                            ParticleTypes.ENCHANT,
                            getX(), getY() + 1.6, getZ(),
                            14, 0.65, 0.6, 0.65, 0.0
                    );
                    server.sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            getX(), getY() + 1.2, getZ(),
                            6, 0.45, 0.5, 0.45, 0.01
                    );
                }

                if (!allScreensLocked()) {
                    rollSoundTick++;
                    if (rollSoundTick >= ROLL_SOUND_INTERVAL) {
                        rollSoundTick = 0;
                        level().playSound(
                                null,
                                blockPosition(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP,
                                SoundSource.HOSTILE,
                                1.0F,
                                1.0F
                        );
                    }
                }

                if (left == null && phaseTick >= LEFT_SPAWN_TICK)
                    left = spawnScreen(-1.6F);
                if (middle == null && phaseTick >= MID_SPAWN_TICK)
                    middle = spawnScreen(0.0F);
                if (right == null && phaseTick >= RIGHT_SPAWN_TICK)
                    right = spawnScreen(1.6F);

                lockHeadToMiddle();

                if (phaseTick >= SLOT_DURATION && allScreensLocked()) {
                    setPhase(PHASE_RESOLVE);
                }
            }

            case PHASE_RESOLVE -> {
                setDeltaMovement(Vec3.ZERO);

                // ✅ Touch-up: stop pathing jitter during resolve too
                getNavigation().stop();

                if (!jackpotFired && phaseTick >= RESOLVE_DELAY) {
                    jackpotFired = true;
                    executeJackpot(middle.getSymbol());
                }

                if (phaseTick >= RESOLVE_END) {
                    cleanupScreens();
                    setPhase(PHASE_RECOVER);
                }
            }

            case PHASE_RECOVER -> {
                if (phaseTick > 40) setPhase(PHASE_IDLE); // was 60
            }
        }
    }

    /* ===================== SLOT ===================== */

    private void startSlotPhase(ServerLevel server) {
        setPhase(PHASE_SLOT);
        phaseTick = 0;
        rollSoundTick = 0;
        jackpotFired = false;

        left = middle = right = null;

        forcedSymbol = GambleScreenEntity.ScreenSymbol.values()
                [random.nextInt(GambleScreenEntity.ScreenSymbol.values().length)];

        server.playSound(
                null,
                blockPosition(),
                SoundEvents.BEACON_AMBIENT,
                SoundSource.HOSTILE,
                1.8F,
                1.0F
        );
    }

    /* ===================== SCREENS ===================== */

    private GambleScreenEntity spawnScreen(float sideOffset) {
        if (!(level() instanceof ServerLevel server)) return null;

        GambleScreenEntity screen = ModEntities.GAMBLE_SCREEN.get().create(server);
        if (screen == null) return null;

        screen.setOwner(this, sideOffset);
        screen.setForcedSymbol(forcedSymbol);

        Vec3 forward = Vec3.directionFromRotation(0, getYRot()).normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();

        Vec3 pos = position()
                .add(forward.scale(1.6))
                .add(right.scale(sideOffset))
                .add(0, getBbHeight() * 0.75, 0);

        screen.setPos(pos.x, pos.y, pos.z);

        float yaw = getYRot() + (random.nextFloat() - 0.5F) * 4.0F;
        screen.setYRot(yaw);
        screen.setYHeadRot(yaw);
        screen.setYBodyRot(yaw);

        server.addFreshEntity(screen);
        return screen;
    }

    private boolean allScreensLocked() {
        return left != null && middle != null && right != null
                && left.isLocked() && middle.isLocked() && right.isLocked();
    }

    private void cleanupScreens() {
        if (left != null) left.discard();
        if (middle != null) middle.discard();
        if (right != null) right.discard();
    }

    private void lockHeadToMiddle() {
        if (middle == null) return;

        Vec3 d = middle.position().subtract(position());
        float yaw = (float) (Mth.atan2(d.z, d.x) * (180F / Math.PI)) - 90F;

        setYRot(yaw);
        yHeadRot = yaw;
        yBodyRot = yaw;
    }

    /* ===================== JACKPOT (UNCHANGED) ===================== */

    private void executeJackpot(GambleScreenEntity.ScreenSymbol symbol) {
        if (!(level() instanceof ServerLevel server)) return;

        switch (symbol) {

            case SKULL -> {
                AABB box = getBoundingBox().inflate(6);

                server.sendParticles(
                        ParticleTypes.SOUL,
                        getX(), getY() + 1.5, getZ(),
                        80, 1, 1, 1, 0.05
                );

                for (Monster m : server.getEntitiesOfClass(Monster.class, box)) {
                    if (m != this) m.hurt(damageSources().magic(), 18);
                }

                for (int i = 0; i < 8; i++) {
                    Zombie baby = EntityType.ZOMBIE.create(server);
                    if (baby == null) continue;

                    baby.setBaby(true);
                    baby.moveTo(
                            getX() + random.nextGaussian() * 4,
                            getY(),
                            getZ() + random.nextGaussian() * 4,
                            random.nextFloat() * 360F,
                            0
                    );

                    LivingEntity target = getTarget();
                    if (target != null) {
                        baby.setTarget(target);
                        baby.setLastHurtByMob(target);
                    }

                    baby.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SPEED, 20 * 15, 0)); // shorter
                    baby.getPersistentData().putInt("copengambler_life", 20 * 15);

                    server.addFreshEntity(baby);
                }
            }

            case DIAMOND -> unleashFireRain();

            case BAR -> {
                for (int i = 0; i < 3; i++) {
                    Snowball s = new Snowball(server, this);
                    s.setPos(getX(), getY() + 1.2, getZ());
                    server.addFreshEntity(s);
                }
            }

            case STAR -> {
                AABB area = getBoundingBox().inflate(12);

                // ✅ ADD: SMITE ALL CURRENT ENEMIES (anything targeting Copengambler)
                for (Monster m : server.getEntitiesOfClass(Monster.class, area)) {
                    if (m == this) continue;
                    if (m.getTarget() != this) continue;

                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
                    if (bolt != null) {
                        // spawn directly on the enemy so it looks like they get smited
                        bolt.moveTo(m.getX(), m.getY(), m.getZ());
                        server.addFreshEntity(bolt);
                    }
                }

                // existing player lightning logic stays
                for (ServerPlayer player : server.getEntitiesOfClass(ServerPlayer.class, area)) {
                    for (int i = 0; i < 8; i++) {

                        BlockPos pos = random.nextFloat() < 0.25F
                                ? player.blockPosition()
                                : player.blockPosition().offset(
                                random.nextInt(8) - 4, 0,
                                random.nextInt(8) - 4
                        );

                        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
                        if (bolt != null) {
                            bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                            bolt.setCause(player);
                            server.addFreshEntity(bolt);
                        }
                    }
                }
            }
        }
    }

    /* ===================== HELPERS ===================== */

    private void unleashFireRain() {
        if (!(level() instanceof ServerLevel server)) return;

        BlockPos c = blockPosition();

        for (int i = 0; i < 40; i++) {
            Arrow arrow = EntityType.ARROW.create(server);
            if (arrow == null) continue;

            arrow.setPos(
                    c.getX() + random.nextGaussian() * 3,
                    c.getY() + 18 + random.nextInt(6),
                    c.getZ() + random.nextGaussian() * 3
            );

            arrow.setOwner(this);
            arrow.setBaseDamage(2.0D);
            arrow.setRemainingFireTicks(4 * 20);
            arrow.setCritArrow(false);

            arrow.setDeltaMovement(
                    random.nextGaussian() * 0.05,
                    -1.2,
                    random.nextGaussian() * 0.05
            );

            arrow.getPersistentData().putInt("copengambler_life", 20 * 4); // shorter

            server.addFreshEntity(arrow);
        }
    }

    private void cleanupTempEntities() {
        if (!(level() instanceof ServerLevel server)) return;
        if (this.isDeadOrDying()) return; // 🔥 prevents wiping drops on death

        for (Zombie z : server.getEntitiesOfClass(Zombie.class, getBoundingBox().inflate(30))) {
            if (!z.getPersistentData().contains("copengambler_life")) continue;
            int life = z.getPersistentData().getInt("copengambler_life") - 1;
            z.getPersistentData().putInt("copengambler_life", life);
            if (life <= 0) z.discard();
        }

        for (Arrow a : server.getEntitiesOfClass(Arrow.class, getBoundingBox().inflate(40))) {
            if (!a.getPersistentData().contains("copengambler_life")) continue;
            int life = a.getPersistentData().getInt("copengambler_life") - 1;
            a.getPersistentData().putInt("copengambler_life", life);
            if (life <= 0 || a.onGround()) a.discard();
        }
    }

    private void setPhase(int p) {
        entityData.set(PHASE, p);
        phaseTick = 0;
    }

    private int getPhase() {
        return entityData.get(PHASE);
    }

    /* ===================== GECKOLIB ===================== */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (getPhase() == PHASE_SLOT)
                return state.setAndContinue(
                        RawAnimation.begin().thenPlay("animation.copengambler.screen_roll_slow2"));
            if (swinging)
                return state.setAndContinue(
                        RawAnimation.begin().thenPlay("animation.copengambler.hit"));
            if (getDeltaMovement().horizontalDistanceSqr() > 0.03)
                return state.setAndContinue(
                        RawAnimation.begin().thenLoop("animation.copengambler.run"));
            if (getDeltaMovement().horizontalDistanceSqr() > 0.002)
                return state.setAndContinue(
                        RawAnimation.begin().thenLoop("animation.copengambler.walk"));
            return state.setAndContinue(
                    RawAnimation.begin().thenLoop("animation.copengambler.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean isPushable() {
        // mobs & players cannot shove during gamble
        return !isGambling();
    }

    @Override
    protected void pushEntities() {
        // prevent collision pushes during gamble
        if (!isGambling()) {
            super.pushEntities();
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        // prevent knockback during gamble
        if (!isGambling()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // keeps your original behavior: invulnerable during SLOT only
        return getPhase() != PHASE_SLOT && super.hurt(source, amount);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return this.getType().getDefaultLootTable();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level,
                                       DamageSource source,
                                       boolean recentlyHit) {

        super.dropCustomDeathLoot(level, source, recentlyHit);

        if (level.isClientSide()) return;

        // Mojmap 1.21: lastHurtByPlayer is Player, not ServerPlayer
        if (!(this.lastHurtByPlayer instanceof ServerPlayer player)) return;

        // 🎁 Drops FIRST
        this.spawnAtLocation(new ItemStack(ModItems.JESS_JACKPOT.get(), 1), 0.0f);

        if (this.random.nextFloat() < 0.9f) {
            this.spawnAtLocation(new ItemStack(ModItems.LEGENDARY_KEY.get(), 1), 0.0f);
        }

        // 🔥 Boss finish visuals AFTER drops
        level.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                this.getX(),
                this.getY() + 1.5,
                this.getZ(),
                1,
                0, 0, 0,
                0
        );

        level.playSound(
                null,
                this.blockPosition(),
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.HOSTILE,
                2.5F,
                1.0F
        );

        // ⚡ Lightning last so it can't interfere
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(this.getX(), this.getY(), this.getZ());
            bolt.setVisualOnly(true); // 🔥 THIS IS THE FIX
            level.addFreshEntity(bolt);
        }
    }
}