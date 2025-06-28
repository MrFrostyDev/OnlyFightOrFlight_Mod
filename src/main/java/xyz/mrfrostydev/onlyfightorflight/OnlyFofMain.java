package xyz.mrfrostydev.onlyfightorflight;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

@Mod(OnlyFofMain.MOD_ID)
public class OnlyFofMain {
    public static final String MOD_ID = "onlyfightorflight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OnlyFofMain(IEventBus modEventBus, ModContainer modContainer) {
        DataAttachmentRegistry.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, OnlyFofCommonConfig.SPEC, String.format("%s-common.toml", OnlyFofMain.MOD_ID));
        modContainer.registerConfig(ModConfig.Type.CLIENT, OnlyFofClientConfig.SPEC, String.format("%s-client.toml", OnlyFofMain.MOD_ID));
    }
}

