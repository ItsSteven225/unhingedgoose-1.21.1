package net.itssteven.unhinged_goose.entity.custom;

import net.itssteven.unhinged_goose.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GooseEntity extends Animal {

    private boolean hadEffectBefore = false;

    private void transformIntoNetherGoose() {
        if (!(this.level() instanceof ServerLevel level)) return;

        Pig pig = EntityType.PIG.create(level);
        if (pig == null) return;

        pig.moveTo(
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getYRot(),
                this.getXRot()
        );

        level.addFreshEntity(pig);
        this.discard();
    }

    public final AnimationState flyAnimationState = new AnimationState();

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    public GooseEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2F, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, (double) 1.0F));
        this.goalSelector.addGoal(3, new TemptGoal(this, (double) 1.0F, (p_335679_) -> p_335679_.is(ItemTags.CHICKEN_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, 3.0F)
                .add(Attributes.FOLLOW_RANGE, 30D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.CHICKEN_FOOD);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (!this.onGround()) {
                this.flyAnimationState.startIfStopped(this.tickCount);
            } else {
                this.flyAnimationState.stop();
            }
        }

        if (!this.level().isClientSide) {
            this.setupAnimationState();

            boolean inNether = this.level().dimension() == Level.NETHER;
            boolean hasEffect = this.hasEffect(MobEffects.FIRE_RESISTANCE);

            if (inNether && hadEffectBefore && !hasEffect) {
                transformIntoNetherGoose();
            }
            hadEffectBefore = hasEffect;
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.GOOSE.get().create(level());
    }

    private void setupAnimationState() {
        if (this.attackAnimationTimeout > 0) {
            --this.attackAnimationTimeout;
        } else {
            this.attackAnimationState.stop();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result) {
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        return result;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationState.start(this.tickCount);
            this.attackAnimationTimeout = 9;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (this.isAlive() && !this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.85, 1.0));
        }
        super.travel(travelVector);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource damageSource) {
        return super.causeFallDamage(fallDistance - 3.0F, damageMultiplier * 0.5F, damageSource);
    }
}