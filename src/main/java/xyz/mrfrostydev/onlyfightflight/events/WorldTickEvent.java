package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.mrfrostydev.onlyfightflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;
import xyz.mrfrostydev.onlyfightflight.network.OnlyFofPacketHandler;
import xyz.mrfrostydev.onlyfightflight.network.SyncOutOfCombatPacket;

import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class WorldTickEvent {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event){
        Level level = event.level;
        if(level.isClientSide() || level.getServer() == null) return;

        level.players().stream().toList().forEach(player -> {
            if (!(player instanceof ServerPlayer svplayer)) return;

            // Rather hefty check if ran frequently so try to reduce the amount of calls.
            boolean isBeingTargetted;
            int checkFreq = OnlyFofCommonConfig.UPDATE_INTERVAL.get();
            if(!OnlyFofCommonConfig.DISABLE_AGGRO_COMBAT.get() && level.getServer().getTickCount() % checkFreq == 0){
                float checkRadius = OnlyFofCommonConfig.RADIUS_CHECK.get();
                AABB area = new AABB(svplayer.blockPosition()).inflate(checkRadius, checkRadius, checkRadius);
                Predicate<Entity> predicate = (entity -> {
                    if(entity instanceof Mob mob){
                        LivingEntity target = mob.getTarget();
                        return target != null && target.is(svplayer);
                    }
                    return false;
                });

                List<Entity> nearby = level.getEntities(player, area, predicate);
                isBeingTargetted = !nearby.isEmpty();
            } else {
                isBeingTargetted = false;
            }

            svplayer.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent(data -> {
                if(isBeingTargetted || data.getOutTime() > 0){
                    if(isBeingTargetted){
                        if(data.isOutOfCombat()){
                            data.startCombat(OnlyFofCommonConfig.TIME_BY_TARGETED.get());
                        }
                        else{
                            data.addTime(checkFreq);
                        }
                    }
                    else if (data.getOutTime() > 0){
                        data.tick();
                    }
                    OnlyFofPacketHandler.sendToPlayer((ServerPlayer) player, new SyncOutOfCombatPacket(data));
                }
            });
        });
    }
}
