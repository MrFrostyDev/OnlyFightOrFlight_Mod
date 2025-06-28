package xyz.mrfrostydev.onlyfightflight.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import xyz.mrfrostydev.onlyfightflight.events.OutOfCombatEvent;

public class OutOfCombatData {

    private Player player;
    private boolean isOutOfCombat;
    private int outTime;

    public OutOfCombatData(){
        this.isOutOfCombat = true;
        this.outTime = 0;
    }

    public OutOfCombatData(Player player){
        this.player = player;
        this.isOutOfCombat = true;
        this.outTime = 0;
    }

    public OutOfCombatData(Player player, boolean isOutOfCombat, int outTime){
        this.player = player;
        this.isOutOfCombat = isOutOfCombat;
        this.outTime = outTime;
    }

    public void startCombat(int outTime){
        OutOfCombatEvent.Start event = new OutOfCombatEvent.Start(this.player, this, outTime);
        if(isOutOfCombat && !MinecraftForge.EVENT_BUS.post(event)){
            this.outTime = event.getNewOutTime();
            this.isOutOfCombat = false;
        }
    }

    public void copyFrom(OutOfCombatData data){
        this.isOutOfCombat = data.isOutOfCombat();
        this.outTime = data.getOutTime();
    }

    public void setData(boolean isOutOfCombat, int outTime){
        this.isOutOfCombat = isOutOfCombat;
        this.outTime = outTime;
    }

    public void addTime(int addedTime){
        this.outTime = this.outTime + addedTime;
    }

    public void updateTime(int time){
        if(isOutOfCombat){
            startCombat(time);
        }
        else if(this.outTime < time){
            this.outTime = time;
        }
    }

    public void tick(){
        outTime = outTime - 1;
        if(outTime <= 0){
            MinecraftForge.EVENT_BUS.post(new OutOfCombatEvent.End(this.player, this));
            isOutOfCombat = true;
        }
    }

    public void saveNBT(CompoundTag tag){
        tag.putBoolean("isOutOfCombat", isOutOfCombat);
        tag.putInt("outTime", outTime);
    }

    public void loadNBT(CompoundTag tag){
        isOutOfCombat = tag.getBoolean("isOutOfCombat");
        outTime = tag.getInt("outTime");
    }

    public int getOutTime() {
        return outTime;
    }

    public boolean isOutOfCombat() {
        return isOutOfCombat;
    }

    public Player getPlayer() {
        return player;
    }
}
