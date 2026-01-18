package net.itssteven.unhinged_goose.potion;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, UnhingedGoose.MOD_ID);

    public static final Holder<Potion> INFERNAL_MOLT_POTION = POTIONS.register("infernal_molt_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.INFERNAL_MOLT, 600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
