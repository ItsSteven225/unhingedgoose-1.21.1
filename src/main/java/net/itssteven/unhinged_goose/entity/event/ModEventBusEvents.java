package net.itssteven.unhinged_goose.entity.event;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.ModEntities;
import net.itssteven.unhinged_goose.entity.client.GooseModel;
import net.itssteven.unhinged_goose.entity.client.NetherGooseModel;
import net.itssteven.unhinged_goose.entity.custom.GooseEntity;
import net.itssteven.unhinged_goose.entity.custom.NetherGooseEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = UnhingedGoose.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GooseModel.LAYER_LOCATION, GooseModel::createBodyLayer);
        event.registerLayerDefinition(NetherGooseModel.LAYER_LOCATION, NetherGooseModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GOOSE.get(), GooseEntity.createAttributes().build());
        event.put(ModEntities.NETHER_GOOSE.get(), NetherGooseEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.GOOSE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
