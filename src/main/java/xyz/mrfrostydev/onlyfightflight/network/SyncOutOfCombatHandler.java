package xyz.mrfrostydev.onlyfightflight.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class SyncOutOfCombatHandler {
    public static void handlePacket(Supplier<NetworkEvent.Context> context, boolean isOutOfCombat, int outTime) {
        Minecraft.getInstance().player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent(data -> {
            data.setData(isOutOfCombat, outTime);
        });
    }
}
