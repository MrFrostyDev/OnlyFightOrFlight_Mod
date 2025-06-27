package xyz.mrfrostydev.onlyfightflight.onlyfightorflight.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class OutOfCombatSerializer implements IAttachmentSerializer<CompoundTag, OutOfCombatData> {

    @Override
    public OutOfCombatData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        var data = new OutOfCombatData((Player)holder);
        data.loadNBT(tag, provider);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(OutOfCombatData attachment, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        attachment.saveNBT(tag, provider);
        return tag;
    }
}
