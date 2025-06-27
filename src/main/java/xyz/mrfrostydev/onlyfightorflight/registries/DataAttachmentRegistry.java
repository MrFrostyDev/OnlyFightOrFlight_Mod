package xyz.mrfrostydev.onlyfightorflight.registries;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatSerializer;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofMain;

public class DataAttachmentRegistry {
    public static DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OnlyFofMain.MOD_ID);

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static DeferredHolder<AttachmentType<?>, AttachmentType<OutOfCombatData>> OUT_OF_COMBAT = ATTACHMENT_TYPES.register(
            "out_of_combat",
            () -> AttachmentType.builder((holder) -> {
                try {
                    Player player = (Player) holder;
                    return new OutOfCombatData(player);
                } catch (ClassCastException e) {
                    OnlyFofMain.LOGGER.error("Attempted to place OUT_OF_COMBAT attachment data to a non-player!");
                }
                return null;
            }).serialize(new OutOfCombatSerializer()).copyOnDeath().build()
    );
}
