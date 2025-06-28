package xyz.mrfrostydev.onlyfightflight.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatData;

import java.util.function.Supplier;

public class SyncOutOfCombatPacket {
    private final boolean isOutOfCombat;
    private final int outTime;

    public SyncOutOfCombatPacket(OutOfCombatData data){
        this.isOutOfCombat = data.isOutOfCombat();
        this.outTime = data.getOutTime();
    }

    public SyncOutOfCombatPacket(boolean isOutOfCombat, int outTime){
        this.isOutOfCombat = isOutOfCombat;
        this.outTime = outTime;
    }

    public SyncOutOfCombatPacket(FriendlyByteBuf buf) {
        this.isOutOfCombat = buf.readBoolean();
        this.outTime = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isOutOfCombat);
        buf.writeInt(outTime);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft.getInstance().player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent(data -> {
                data.setData(isOutOfCombat, outTime);
            });
        });
    }
}
