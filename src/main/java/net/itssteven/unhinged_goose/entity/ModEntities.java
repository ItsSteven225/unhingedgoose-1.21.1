package net.itssteven.unhinged_goose.entity;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.custom.GooseEntity;
import net.itssteven.unhinged_goose.entity.custom.NetherGooseEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, UnhingedGoose.MOD_ID);

    public static final Supplier<EntityType<GooseEntity>> GOOSE =
            ENTITY_TYPES.register("goose",
                    () -> EntityType.Builder.of(GooseEntity::new, MobCategory.CREATURE)
                            .sized(0.5F, 1.1F).build("goose"));

    public static final Supplier<EntityType<NetherGooseEntity>> NETHER_GOOSE =
            ENTITY_TYPES.register("nether_goose",
                    () -> EntityType.Builder.of(NetherGooseEntity::new, MobCategory.CREATURE)
                            .sized(1.125F, 2.5625F).build("nether_goose"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
