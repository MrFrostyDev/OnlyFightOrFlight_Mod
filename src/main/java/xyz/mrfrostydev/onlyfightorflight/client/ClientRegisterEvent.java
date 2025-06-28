package xyz.mrfrostydev.onlyfightorflight.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofMain;

@EventBusSubscriber(modid = OnlyFofMain.MOD_ID, value = Dist.CLIENT)
public class ClientRegisterEvent {

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event){
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "out_of_combat_overlay"), new OutOfCombatOverlay());
    }
}
