package net.itssteven.unhinged_goose.item;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UnhingedGoose.MOD_ID);
    //Items
    public static final DeferredItem<Item> GOOSE_BONE = ITEMS.register("goose_bone",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOOSE_WINGS = ITEMS.register("goose_wings",
            () -> new Item(new Item.Properties().food(ModFoodProperties.GOOSE_WINGS)));

    public static final DeferredItem<Item> COOKED_GOOSE_WINGS = ITEMS.register("cooked_goose_wings",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_GOOSE_WINGS)));

    public static final DeferredItem<Item> NETHER_GOOSE = ITEMS.register("nether_goose",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOOSE_SPAWN_EGG = ITEMS.register("goose_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GOOSE, 0xFFECECEC, 0xFFF29F05, new Item.Properties()));

    public static final DeferredItem<Item> NETHER_GOOSE_SPAWN_EGG = ITEMS.register("nether_goose_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.NETHER_GOOSE, 0xFFECECEC, 0x990000, new Item.Properties()));
    //some bullshit
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
