package xyz.mrfrostydev.onlyfightorflight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.network.SyncOutOfCombatPacket;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber
public class WorldTickEvent {
    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event){
        Level level = event.getLevel();
        if(level.isClientSide() || level.getServer() == null) return;

        level.players().stream().toList().forEach(player -> {
            if (!(player instanceof ServerPlayer svplayer)) return;

            // Rather hefty check if ran frequently so try to reduce the amount of calls.
            boolean isBeingTargetted = false;
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
            }

            OutOfCombatData data = svplayer.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
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
            PacketDistributor.sendToPlayer(svplayer, SyncOutOfCombatPacket.create(data));
        });
    }
}
