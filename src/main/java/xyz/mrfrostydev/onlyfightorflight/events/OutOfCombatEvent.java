package xyz.mrfrostydev.onlyfightorflight.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;

public class OutOfCombatEvent extends PlayerEvent implements ICancellableEvent {
    private OutOfCombatData data;

    public OutOfCombatEvent(Player player, OutOfCombatData data){
        super(player);
        this.data = data;
    }

    public OutOfCombatData getData() {
        return data;
    }

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
