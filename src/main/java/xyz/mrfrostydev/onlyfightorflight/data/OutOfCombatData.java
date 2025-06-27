package xyz.mrfrostydev.onlyfightorflight.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class OutOfCombatData {
    private Player player;
    private boolean isOutOfCombat;
    private int outTime;

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
        if(isOutOfCombat){
            this.outTime = outTime;
            isOutOfCombat = false;
        }
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
            isOutOfCombat = true;
        }
    }

    public void saveNBT(CompoundTag tag, HolderLookup.Provider provider){
        tag.putBoolean("isOutOfCombat", isOutOfCombat);
        tag.putInt("outTime", outTime);
    }

    public void loadNBT(CompoundTag tag, HolderLookup.Provider provider){
        isOutOfCombat = tag.getBoolean("isOutOfCombat");
        outTime = tag.getInt("outTime");
    }

    public Player getPlayer() {
        return player;
    }

    public int getOutTime() {
        return outTime;
    }

    public boolean isOutOfCombat() {
        return isOutOfCombat;
    }
}
