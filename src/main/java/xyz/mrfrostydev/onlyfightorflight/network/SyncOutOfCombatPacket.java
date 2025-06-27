package xyz.mrfrostydev.onlyfightflight.onlyfightorflight.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.registries.DataAttachmentRegistry;

public record SyncOutOfCombatPacket(boolean isOutOfCombat, int outTime) implements CustomPacketPayload {
    public static final Type<SyncOutOfCombatPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "sync_out_of_combat"));

    public static final StreamCodec<ByteBuf, SyncOutOfCombatPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncOutOfCombatPacket::isOutOfCombat,
            ByteBufCodecs.INT, SyncOutOfCombatPacket::outTime,
            SyncOutOfCombatPacket::new
    );

    public static SyncOutOfCombatPacket create(OutOfCombatData data){
        return new SyncOutOfCombatPacket(data.isOutOfCombat(), data.getOutTime());
    }

    public static SyncOutOfCombatPacket create(boolean isOutOfCombat, int outTime){
        return new SyncOutOfCombatPacket(isOutOfCombat, outTime);
    }

    public static void handle(SyncOutOfCombatPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(DataAttachmentRegistry.OUT_OF_COMBAT,
                    new OutOfCombatData(context.player(), packet.isOutOfCombat(), packet.outTime()));
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.literal("Failed to send SyncOutOfCombatPacket: " + e.getMessage()));
            return null;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }
}
