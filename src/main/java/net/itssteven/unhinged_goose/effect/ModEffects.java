package net.itssteven.unhinged_goose.effect;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, UnhingedGoose.MOD_ID);

    public static final Holder<MobEffect> INFERNAL_MOLT = MOB_EFFECTS.register("infernal_molt",
            () -> new InfernalMoltEffect(MobEffectCategory.HARMFUL, 9701130)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "infernal_molt_speed"),
                            -0.35D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                            ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "infernal_molt_damage"),
                            -3.0D,
                            AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED,
                            ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "infernal_molt_fatigue"),
                            -0.6D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
