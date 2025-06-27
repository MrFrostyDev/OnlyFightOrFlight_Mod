package xyz.mrfrostydev.onlyfightflight.onlyfightorflight;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.registries.DataAttachmentRegistry;

@Mod(OnlyFofMain.MOD_ID)
public class OnlyFofMain {
    public static final String MOD_ID = "onlyfightorflight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OnlyFofMain(IEventBus modEventBus, ModContainer modContainer) {
        DataAttachmentRegistry.register(modEventBus);
    }
}
