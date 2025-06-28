package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatData;

public class OutOfCombatEvent extends Event {
    private OutOfCombatData data;

    public OutOfCombatEvent(OutOfCombatData data){
        this.data = data;
    }

    public OutOfCombatData getData() {
        return data;
    }

    @Cancelable
    public static class Start extends OutOfCombatEvent{

        private int outTime;
        private int newOutTime;

        public Start(OutOfCombatData data, int outTime){
            super(data);
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
        public End(OutOfCombatData data){
            super(data);
        }
    }

}
