package net.itssteven.unhinged_goose.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties GOOSE_WINGS = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(2.0F)
            .usingConvertsTo(ModItems.GOOSE_BONE.get())
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.25F)
            .build();
    public static final FoodProperties COOKED_GOOSE_WINGS = (new FoodProperties.Builder())
            .nutrition(5)
            .saturationModifier(0.7F)
            .usingConvertsTo(ModItems.GOOSE_BONE.get())
            .build();
}
