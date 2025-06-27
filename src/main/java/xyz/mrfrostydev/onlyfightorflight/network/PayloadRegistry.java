package xyz.mrfrostydev.onlyfightflight.onlyfightorflight.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.OnlyFofMain;

@EventBusSubscriber
public class PayloadRegistry {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar(OnlyFofMain.MOD_ID)
                .versioned("1.0")
                .optional();

        registrar.playBidirectional(
                SyncOutOfCombatPacket.TYPE,
                SyncOutOfCombatPacket.STREAM_CODEC,
                SyncOutOfCombatPacket::handle
        );
    }
}
