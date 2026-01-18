package net.itssteven.unhinged_goose.entity.custom;

import net.itssteven.unhinged_goose.entity.ModEntities;
import net.itssteven.unhinged_goose.entity.NetherGooseVariant;
import net.itssteven.unhinged_goose.item.ModItems;
import net.itssteven.unhinged_goose.sound.ModSounds;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class NetherGooseEntity extends TamableAnimal {
    //something about animations

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(NetherGooseEntity.class, EntityDataSerializers.INT);

    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState floatIdleAnimationState = new AnimationState();
    public final AnimationState floatAttackAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();

    public final AnimationState attack1AnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();

    private int attack1Timeout = 0;
    private int attack2Timeout = 0;


    private int angerTime = 0;
    private static final int MAX_ANGER_TIME = 600;

    private static final int WARNING_TIME = 100;
    private int warningSoundCooldown = 0;

    public NetherGooseEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

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
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, 8.0F)
                .add(Attributes.FOLLOW_RANGE, 30.0F);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModItems.GOOSE_BONE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
         NetherGooseEntity baby = ModEntities.NETHER_GOOSE.get().create(level);

        if (baby != null) {
            baby.setBaby(true);
        }

        return baby;
    }
    @Override
    public void tick() {
        super.tick();


        //client  side
        if (this.level().isClientSide) {

            boolean inLiquid = this.isInWater() || this.isInLava();
            boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
            boolean attacking1 = this.attack1Timeout > 0;
            boolean attacking2 = this.attack2Timeout > 0;
            boolean flying = !this.onGround() && !inLiquid;

            this.swimAnimationState.stop();
            this.floatIdleAnimationState.stop();
            this.floatAttackAnimationState.stop();
            this.flyAnimationState.stop();
            this.attack1AnimationState.stop();
            this.attack2AnimationState.stop();

            if (inLiquid) {
                if (attacking1 || attacking2) {
                    this.floatAttackAnimationState.startIfStopped(this.tickCount);
                }
                else if (moving) {
                    this.swimAnimationState.startIfStopped(this.tickCount);
                }
                else {
                    this.floatIdleAnimationState.startIfStopped(this.tickCount);
                }
                return;
            }

            if (flying) {
                this.flyAnimationState.startIfStopped(this.tickCount);
                return;
            }

            if (attacking2) {
                this.attack2AnimationState.startIfStopped(this.tickCount);
                return;
            }

            if (attacking1) {
                this.attack1AnimationState.startIfStopped(this.tickCount);
            }
        }

        //server side
        if (!this.level().isClientSide) {
            this.setupAnimationStates();
        }

        //logic i guess
        if (!this.level().isClientSide && !this.isBaby() && !this.isTame()) {

            Player player = this.level().getNearestPlayer(this, 6.0D);

            if (player != null && this.hasLineOfSight(player)) {

                angerTime++;

                int warningStart = MAX_ANGER_TIME - WARNING_TIME;

                if (angerTime < warningStart) {
                    return;
                }

                if (angerTime >= warningStart && angerTime < MAX_ANGER_TIME) {

                    this.getNavigation().stop();
                    this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

                    this.getLookControl().setLookAt(player, 30.0F, 30.0F);

                    if (warningSoundCooldown-- <= 0) {
                        this.playSound(ModSounds.GOOSE_ALERT.get(), 1.0F, 0.9F);
                        warningSoundCooldown = 40;
                    }

                    return;
                }

                if (angerTime >= MAX_ANGER_TIME) {
                    this.setTarget(player);
                }

            } else {
                angerTime = 0;
                warningSoundCooldown = 0;
            }
        }
    }

    private void setupAnimationStates() {

        if (this.attack1Timeout > 0) {
            --this.attack1Timeout;
        } else {
            this.attack1AnimationState.stop();
        }

        if (this.attack2Timeout > 0) {
            --this.attack2Timeout;
        } else {
            this.attack2AnimationState.stop();
        }
    }
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result) {
            if (this.random.nextBoolean()) {
                this.level().broadcastEntityEvent(this, (byte) 10); // attack1
            } else {
                this.level().broadcastEntityEvent(this, (byte) 11); // attack2
            }
        }

        return result;
    }
    @Override
    public void handleEntityEvent(byte id) {

        if (id == 10) {
            this.attack1AnimationState.start(this.tickCount);
            this.attack1Timeout = 9;
        }
        else if (id == 11) {
            this.attack2AnimationState.start(this.tickCount);
            this.attack2Timeout = 15;
        }
        else {
            super.handleEntityEvent(id);
        }
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public NetherGooseVariant getVariant() {
        return NetherGooseVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(NetherGooseVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));

    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        NetherGooseVariant variant = Util.getRandom(NetherGooseVariant.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}
