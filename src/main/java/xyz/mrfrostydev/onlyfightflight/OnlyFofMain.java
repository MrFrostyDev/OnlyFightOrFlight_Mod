package xyz.mrfrostydev.onlyfightflight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.mrfrostydev.onlyfightflight.network.OnlyFofPacketHandler;

@Mod(OnlyFofMain.MOD_ID)
public class OnlyFofMain {

    public static final String MOD_ID = "onlyfightorflight";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OnlyFofMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        OnlyFofPacketHandler.register();

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, OnlyFofClientConfig.SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, OnlyFofCommonConfig.SPEC);
    }
}
