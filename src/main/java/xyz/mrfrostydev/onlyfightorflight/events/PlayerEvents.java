package xyz.mrfrostydev.onlyfightorflight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.network.ClientPlayOutOfCombatPacket;
import xyz.mrfrostydev.onlyfightorflight.network.SyncOutOfCombatPacket;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

@EventBusSubscriber
public class PlayerEvents {

    @SubscribeEvent
    public static void OutOfCombatStart(OutOfCombatEvent.Start event){
        if(event.getEntity() instanceof ServerPlayer svplayer){
            PacketDistributor.sendToPlayer(svplayer, new ClientPlayOutOfCombatPacket());
        }
    }

    @SubscribeEvent
    public static void OutOfCombatEnd(OutOfCombatEvent.End event){
        if(event.getEntity() instanceof ServerPlayer svplayer){
            PacketDistributor.sendToPlayer(svplayer, new ClientPlayOutOfCombatPacket());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            player.setData(DataAttachmentRegistry.OUT_OF_COMBAT, data);

            if (!player.level().isClientSide()) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, SyncOutOfCombatPacket.create(data));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            player.setData(DataAttachmentRegistry.OUT_OF_COMBAT, data);

            if(!player.level().isClientSide()){
                PacketDistributor.sendToPlayer((ServerPlayer) player, SyncOutOfCombatPacket.create(data));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide() && event.isWasDeath()) {
            Player newPlayer = event.getEntity();
            Player oldPlayer = event.getOriginal();
            OutOfCombatData oldData = oldPlayer.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            newPlayer.setData(DataAttachmentRegistry.OUT_OF_COMBAT, oldData);

            if(newPlayer instanceof ServerPlayer svplayer){
                PacketDistributor.sendToPlayer(svplayer, SyncOutOfCombatPacket.create(oldData));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            OutOfCombatData oldData = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            player.setData(DataAttachmentRegistry.OUT_OF_COMBAT, oldData);

            if(player instanceof ServerPlayer svplayer){
                PacketDistributor.sendToPlayer(svplayer, SyncOutOfCombatPacket.create(oldData));
            }
        }
    }

}
