package net.itssteven.unhinged_goose.effect;

import net.itssteven.unhinged_goose.entity.ModEntities;
import net.itssteven.unhinged_goose.entity.custom.GooseEntity;
import net.itssteven.unhinged_goose.entity.custom.NetherGooseEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class InfernalMoltEffect extends MobEffect {

    public InfernalMoltEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            double shake = 0.05D;
            entity.setDeltaMovement(
                    entity.getDeltaMovement().add(
                            (entity.getRandom().nextDouble() - 0.5D) * shake,
                            0,
                            (entity.getRandom().nextDouble() - 0.5D) * shake
                    )
            );
            return true;
        }
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));

        var effectInstance = entity.getEffect(ModEffects.INFERNAL_MOLT);
        if (effectInstance != null && effectInstance.getDuration() <= 1) {
            spawnGooseEffect(entity);
        }
        return true;
    }

    private void spawnGooseEffect(LivingEntity entity) {
        if (entity instanceof GooseEntity) return;
        if (entity instanceof NetherGooseEntity) return;

        if (entity.level() instanceof ServerLevel level) {
            GooseEntity goose = ModEntities.GOOSE.get().create(level);

            if (goose != null) {
                goose.moveTo(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        entity.getYRot(),
                        entity.getXRot()
                );
                level.addFreshEntity(goose);
                entity.hurt(level.damageSources().magic(), 8.0F);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}