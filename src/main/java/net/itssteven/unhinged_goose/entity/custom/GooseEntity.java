package net.itssteven.unhinged_goose.entity.custom;

import net.itssteven.unhinged_goose.entity.GooseVariant;
import net.itssteven.unhinged_goose.entity.ModEntities;
import net.itssteven.unhinged_goose.sound.ModSounds;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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


    private UUID parentUUID;

    private int warningSoundCount = 0;
    private int warningTickTimer = 0;

    public boolean isParentOf(GooseEntity baby) {
        return baby.parentUUID != null && baby.parentUUID.equals(this.getUUID());
    }

    private boolean isProtectingBaby(Player player) {
        return !this.level().getEntitiesOfClass(GooseEntity.class,
                player.getBoundingBox().inflate(3.0F),
                baby -> baby.isBaby() && this.isParentOf(baby)
        ).isEmpty();
    }

    private int warningCount = 0;
    private int warningCooldown = 0;

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(GooseEntity.class, EntityDataSerializers.INT);
    //something about animations
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState floatIdleAnimationState = new AnimationState();
    public final AnimationState floatAttackAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    public GooseEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2F, true) {
            @Override
            public boolean canUse() {
                return !mob.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.2F));
        this.goalSelector.addGoal(3, new BreedGoal(this, (double) 1.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, (double) 1.0F, (p_335679_) -> p_335679_.is(ItemTags.CHICKEN_FOOD), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, (double) 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0F)
                .add(Attributes.MOVEMENT_SPEED, (double) 0.25F)
                .add(Attributes.ATTACK_DAMAGE, (double) 3.0F)
                .add(Attributes.FOLLOW_RANGE, 15D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.CHICKEN_FOOD);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            boolean inWater = this.isInWater();
            boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
            boolean attacking = this.attackAnimationTimeout > 0;
            boolean flying = !this.onGround() && !inWater;

            this.swimAnimationState.stop();
            this.floatIdleAnimationState.stop();
            this.floatAttackAnimationState.stop();
            this.flyAnimationState.stop();
            this.attackAnimationState.stop();

            if (flying) {
                this.flyAnimationState.startIfStopped(this.tickCount);
            }
            else if (inWater) {
                if (attacking) {
                    this.floatAttackAnimationState.startIfStopped(this.tickCount);
                } else if (moving) {
                    this.swimAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.floatIdleAnimationState.startIfStopped(this.tickCount);
                }
            }
            else if (attacking) {
                this.attackAnimationState.startIfStopped(this.tickCount);
            }

            return;
        }


        boolean inNether = this.level().dimension() == Level.NETHER;
        boolean hasEffect = this.hasEffect(MobEffects.FIRE_RESISTANCE);
        if (inNether && hadEffectBefore && !hasEffect) {
            transformIntoNetherGoose();
        }
        hadEffectBefore = hasEffect;

        if (!this.isBaby()) {

            if (this.getTarget() != null) {
                warningSoundCount = 0;
                warningTickTimer = 0;
                return;
            }

            Player player = this.level().getNearestPlayer(this, 5.0F);

            if (player != null && isProtectingBaby(player)) {
                this.getNavigation().stop();
                this.getLookControl().setLookAt(player, 30.0F, 30.0F);

                if (warningTickTimer > 0) {
                    warningTickTimer--;
                } else if (warningSoundCount < 3) {
                    this.playSound(ModSounds.GOOSE_ALERT.get(), 1.0F, 1.0F);
                    warningSoundCount++;
                    warningTickTimer = 40;
                } else {
                    this.setTarget(player);
                    this.setAggressive(true);
                }
            } else {
                warningSoundCount = 0;
                warningTickTimer = 0;
            }
        }

        this.setupAnimationState();
    }
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        GooseEntity baby = ModEntities.GOOSE.get().create(level);

        if (baby != null) {
            baby.parentUUID = this.getUUID();
            baby.setVariant(this.getVariant());
        }
        return baby;
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
        float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean flag = target.hurt(this.damageSources().mobAttack(this), damage);

        if (flag) {
            this.playSound(ModSounds.GOOSE_ATTACK.get(), 1.0F, 1.0F);
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        return flag;
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
    public void travel(Vec3 travelVector) {

        if (this.isInWater()) {

            this.moveRelative(0.06F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());

            Vec3 motion = this.getDeltaMovement();

            this.setDeltaMovement(
                    motion.x * 0.9D,
                    motion.y * 0.9D,
                    motion.z * 0.9D
            );

            if (motion.y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.005D, 0));
            }

            return;
        }

        if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.85D, 1.0D));
        }

        super.travel(travelVector);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource damageSource) {
        return super.causeFallDamage(fallDistance - 3.0F, damageMultiplier * 0.5F, damageSource);
    }
    //VARIANTS AND THAT SHITS THAT NOBODY CARES

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public GooseVariant getVariant() {
        return GooseVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(GooseVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getTypeVariant());

        if (this.parentUUID != null) {
            compound.putUUID("ParentUUID", this.parentUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));

        if (compound.hasUUID("ParentUUID")) {
            this.parentUUID = compound.getUUID("ParentUUID");
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        GooseVariant variant = Util.getRandom(GooseVariant.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    //SOUNDS YAY
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GOOSE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.GOOSE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GOOSE_DEATH.get();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player && !this.isBaby()) {
            return true;
        }
        return super.canAttack(target);
    }
}
