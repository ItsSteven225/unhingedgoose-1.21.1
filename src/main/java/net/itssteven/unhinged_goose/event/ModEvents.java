package net.itssteven.unhinged_goose.event;

import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.item.ModItems;
import net.itssteven.unhinged_goose.potion.ModPotions;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = UnhingedGoose.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ModItems.GOOSE_BONE.get(), ModPotions.INFERNAL_MOLT_POTION);
    }
}
