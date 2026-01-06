package net.itssteven.unhinged_goose.item;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
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
    //some bullshit
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
