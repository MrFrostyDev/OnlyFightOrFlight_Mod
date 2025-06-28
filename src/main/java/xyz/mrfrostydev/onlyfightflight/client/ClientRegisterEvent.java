package xyz.mrfrostydev.onlyfightflight.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.mrfrostydev.onlyfightflight.OnlyFofMain;

@Mod.EventBusSubscriber(modid = OnlyFofMain.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegisterEvent {

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
        event.registerAboveAll("out_of_combat_icon", new OutOfCombatOverlay());
    }
}
