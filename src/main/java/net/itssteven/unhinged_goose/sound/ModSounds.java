package net.itssteven.unhinged_goose.sound;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, UnhingedGoose.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOSE_IDLE =
            registerSound("entity.goose.idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOSE_HURT =
            registerSound("entity.goose.hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOSE_DEATH =
            registerSound("entity.goose.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOSE_ALERT =
            registerSound("entity.goose.alert");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, name)));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}