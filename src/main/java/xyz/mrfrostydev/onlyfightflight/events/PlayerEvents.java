package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.mrfrostydev.onlyfightflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;
import xyz.mrfrostydev.onlyfightflight.network.OnlyFofPacketHandler;
import xyz.mrfrostydev.onlyfightflight.network.SyncOutOfCombatPacket;


@Mod.EventBusSubscriber
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
                OnlyFofPacketHandler.sendToPlayer((ServerPlayer) player, new SyncOutOfCombatPacket(data));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
                OnlyFofPacketHandler.sendToPlayer((ServerPlayer) player, new SyncOutOfCombatPacket(data));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide() && event.isWasDeath()) {
            Player newPlayer = event.getEntity();
            Player oldPlayer = event.getOriginal();
            oldPlayer.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((oldData) -> {
                newPlayer.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((newData) -> {
                    newData.copyFrom(oldData);
                    OnlyFofPacketHandler.sendToPlayer((ServerPlayer) newPlayer, new SyncOutOfCombatPacket(oldData));
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
                OnlyFofPacketHandler.sendToPlayer((ServerPlayer) player, new SyncOutOfCombatPacket(data));
            });
        }
    }

    @SubscribeEvent
    public static void onAttackCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        Entity entity = event.getObject();
        if(entity instanceof Player){
            if(!entity.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).isPresent()){
                event.addCapability(ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "out_of_combat_data"), new OutOfCombatCapability());
            }
        }
    }
}
