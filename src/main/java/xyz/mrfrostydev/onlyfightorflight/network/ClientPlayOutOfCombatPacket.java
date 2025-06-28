package xyz.mrfrostydev.onlyfightorflight.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightorflight.client.OutOfCombatOverlay;

public class ClientPlayOutOfCombatPacket implements CustomPacketPayload {
    public static final Type<ClientPlayOutOfCombatPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "play_out_of_combat"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientPlayOutOfCombatPacket> STREAM_CODEC = CustomPacketPayload.codec(
            ClientPlayOutOfCombatPacket::write,
            ClientPlayOutOfCombatPacket::new
    );

    public ClientPlayOutOfCombatPacket(){}

    public ClientPlayOutOfCombatPacket(FriendlyByteBuf buf){}

    public void write(FriendlyByteBuf buf){}

    public static void handle(ClientPlayOutOfCombatPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            OutOfCombatOverlay.startAnimation();
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }
}
