package net.lazy.kobe.boss.projectile;

import net.lazy.kobe.registry.ModEntities;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RockProjectileEntity extends ThrowableItemProjectile {

    public RockProjectileEntity(EntityType<? extends RockProjectileEntity> type, Level level) {
        super(type, level);
    }

    public RockProjectileEntity(Level level, LivingEntity owner) {
        super(ModEntities.ROCK_PROJECTILE.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.COBBLESTONE; // purely visual
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {

            Entity owner = getOwner();
            DamageSource source = owner instanceof LivingEntity living
                    ? damageSources().mobProjectile(this, living)
                    : damageSources().generic();

            target.hurt(source, 10.0F);
        }

        discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!level().isClientSide) {
            ((ServerLevel) level()).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    getX(), getY(), getZ(),
                    12, 0.2, 0.2, 0.2, 0.05
            );
        }

        discard();
    }
}