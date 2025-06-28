package xyz.mrfrostydev.onlyfightflight.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.mrfrostydev.onlyfightflight.OnlyFofMain;

// thank you! [TurtyWurty Youtube] [IronSpellsAndSpellbooks Mod]
public class OnlyFofPacketHandler {
    private static int id = 0;

    private static SimpleChannel INSTANCE;

    public static void register(){
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(SyncOutOfCombatPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncOutOfCombatPacket::encode)
                .decoder(SyncOutOfCombatPacket::new)
                .consumerMainThread(SyncOutOfCombatPacket::handle)
                .add();

        INSTANCE.messageBuilder(ClientPlayOutOfCombatPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientPlayOutOfCombatPacket::encode)
                .decoder(ClientPlayOutOfCombatPacket::new)
                .consumerMainThread(ClientPlayOutOfCombatPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(ServerPlayer player, MSG msg){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
