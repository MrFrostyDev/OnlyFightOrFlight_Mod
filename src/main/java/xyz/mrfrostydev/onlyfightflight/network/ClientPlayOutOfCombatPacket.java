package xyz.mrfrostydev.onlyfightflight.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.mrfrostydev.onlyfightflight.client.OutOfCombatOverlay;
import java.util.function.Supplier;

public class ClientPlayOutOfCombatPacket {
    public ClientPlayOutOfCombatPacket(){}

    public ClientPlayOutOfCombatPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            OutOfCombatOverlay.startAnimation();
        });
    }
}
