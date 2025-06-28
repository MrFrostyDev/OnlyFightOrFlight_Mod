package xyz.mrfrostydev.onlyfightflight.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// thank you! [Kaupenjoe Youtube]
public class OutOfCombatCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<OutOfCombatData> OUT_OF_COMBAT = CapabilityManager.get(new CapabilityToken<OutOfCombatData>(){});

    private OutOfCombatData data = null;
    private final LazyOptional<OutOfCombatData> optional = LazyOptional.of(this::createData);

    public OutOfCombatData createData(){
        if (data == null){
            this.data = new OutOfCombatData();
        }

        return this.data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == OUT_OF_COMBAT){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createData().saveNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createData().loadNBT(tag);
    }
}
