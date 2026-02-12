package net.lazy.kobe.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class GambleScreenEntity extends Entity implements GeoEntity {

    /* ===================== SYMBOL ===================== */

    public enum ScreenSymbol {
        SKULL,
        BAR,
        DIAMOND,
        STAR
    }

    private static final EntityDataAccessor<Integer> SYMBOL =
            SynchedEntityData.defineId(GambleScreenEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> LOCKED =
            SynchedEntityData.defineId(GambleScreenEntity.class, EntityDataSerializers.BOOLEAN);

    /* ===================== STATE ===================== */

    private final AnimatableInstanceCache animCache =
            new SingletonAnimatableInstanceCache(this);

    private Entity owner;
    private float sideOffset;
    private ScreenSymbol forcedSymbol;

    private int ageTicks = 0;
    private int spinTicks = 0;

    /* ===================== CONSTRUCTOR ===================== */

    public GambleScreenEntity(EntityType<?> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    /* ===================== SETUP ===================== */

    public void setOwner(Entity owner, float sideOffset) {
        this.owner = owner;
        this.sideOffset = sideOffset;
    }

    public void setForcedSymbol(ScreenSymbol symbol) {
        this.forcedSymbol = symbol;
    }

    /* ===================== DATA ===================== */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SYMBOL, 0);
        builder.define(LOCKED, false);
    }

    public ScreenSymbol getSymbol() {
        return ScreenSymbol.values()[entityData.get(SYMBOL)];
    }

    public boolean isLocked() {
        return entityData.get(LOCKED);
    }

    /* ===================== TICK ===================== */

    @Override
    public void tick() {
        super.tick();

        if (owner == null || !owner.isAlive()) {
            if (!level().isClientSide) discard();
            return;
        }

        ageTicks++;

        // ================= ROTATION (CLIENT + SERVER) =================
        float yaw = owner.getYRot();

        // ✅ correct interpolation for NON-living entities
        this.yRotO = this.getYRot();
        this.setYRot(yaw);

        // ================= POSITION =================
        Vec3 forward = Vec3.directionFromRotation(0, yaw).normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();

        Vec3 pos = owner.position()
                .add(forward.scale(1.0))
                .add(right.scale(sideOffset))
                .add(0, owner.getBbHeight() * 1.0, 0);

        setPos(pos.x, pos.y, pos.z);

        // ================= SERVER-ONLY LOGIC =================
        if (level().isClientSide) return;

        if (isLocked()) return;

        spinTicks++;

        if (spinTicks % 4 == 0) {
            entityData.set(SYMBOL, random.nextInt(ScreenSymbol.values().length));
        }

        if (spinTicks >= 30) {
            lockSymbol();
        }
    }

    private void lockSymbol() {
        ScreenSymbol result =
                forcedSymbol != null
                        ? forcedSymbol
                        : ScreenSymbol.values()[random.nextInt(ScreenSymbol.values().length)];

        entityData.set(SYMBOL, result.ordinal());
        entityData.set(LOCKED, true);
    }

    /* ===================== ENTITY ===================== */

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {}

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(0.6F, 0.6F);
    }

    /* ===================== GECKOLIB ===================== */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "main",
                0,
                state -> {

                    // 🔒 When locked: play the lock animation once
                    if (isLocked()) {
                        return state.setAndContinue(
                                RawAnimation.begin()
                                        .thenPlay("animation.gamblescreen.lock")
                        );
                    }

                    // 🎰 While spinning: wiggle/roll loop
                    return state.setAndContinue(
                            RawAnimation.begin()
                                    .thenLoop("animation.gamblescreen.roll")
                    );
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }
}