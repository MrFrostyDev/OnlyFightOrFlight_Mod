package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatData;

public class OutOfCombatEvent extends PlayerEvent {
    private OutOfCombatData data;

    public OutOfCombatEvent(Player player, OutOfCombatData data){
        super(player);
        this.data = data;
    }

    public OutOfCombatData getData() {
        return data;
    }

    @Cancelable
    public static class Start extends OutOfCombatEvent{

        private int outTime;
        private int newOutTime;

        public Start(Player player, OutOfCombatData data, int outTime){
            super(player, data);
            this.outTime = outTime;
            this.newOutTime = outTime;
        }


        public int getOutTime() {
            return outTime;
        }

        public int getNewOutTime() {
            return newOutTime;
        }

        public void setNewOutTime(int outTime) {
            this.newOutTime = outTime;
        }
    }

    public static class End extends OutOfCombatEvent{
        public End(Player player, OutOfCombatData data){
            super(player, data);
        }
    }

}
