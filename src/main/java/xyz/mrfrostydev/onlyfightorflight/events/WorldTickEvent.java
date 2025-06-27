package xyz.mrfrostydev.onlyfightflight.onlyfightorflight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.network.SyncOutOfCombatPacket;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.registries.DataAttachmentRegistry;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber
public class WorldTickEvent {

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event){
        final int TARGET_CHECK_FREQ = 60;

        Level level = event.getLevel();
        if(level.isClientSide() || level.getServer() == null) return;

        event.getLevel().players().stream().toList().forEach(player -> {
            if (!(player instanceof ServerPlayer svplayer)) return;

            // Rather hefty check if ran frequently so try to reduce the amount of calls.
            boolean isBeingTargetted = false;
            if(level.getServer().getTickCount() % TARGET_CHECK_FREQ != 0){
                AABB area = new AABB(svplayer.blockPosition());
                area.inflate(8);
                Predicate<Entity> predicate = (entity -> {
                    if(entity instanceof Mob mob){
                        LivingEntity target = mob.getTarget();
                        return target != null && target.is(svplayer);
                    }
                    return false;
                });

                List<Entity> nearby = level.getEntities(svplayer, area, predicate);
                isBeingTargetted = !nearby.isEmpty();
            }

            OutOfCombatData data = svplayer.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            OnlyFofMain.LOGGER.info("OutOfCombatTicks: {}", data.getOutTime());

            if(isBeingTargetted){
                data.startCombat(data.getOutTime() + TARGET_CHECK_FREQ);
            }
            else if (data.getOutTime() > 0){
                data.tick();
                PacketDistributor.sendToPlayer(svplayer, SyncOutOfCombatPacket.create(data));
            }
        });
    }
}
