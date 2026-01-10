package net.itssteven.unhinged_goose.item;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UnhingedGoose.MOD_ID);
    //tabs
    public static final Supplier<CreativeModeTab> UNHINGEDGOOSE_ITEMS_TAB =CREATIVE_MODE_TAB.register("unhingedgoose_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.NETHER_GOOSE_HEAD.get()))
                    .title(Component.translatable("creativetab.unhingedgoose.unhingedggoose_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //items
                        output.accept(ModItems.GOOSE_WINGS);
                        output.accept(ModItems.COOKED_GOOSE_WINGS);
                        output.accept(ModItems.GOOSE_BONE);
                        output.accept(ModItems.NETHER_GOOSE);
                        output.accept(ModBlocks.NETHER_GOOSE_HEAD);
                        output.accept(ModItems.GOOSE_SPAWN_EGG);
                        //shit
                    }).build());
    //bullshit
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
