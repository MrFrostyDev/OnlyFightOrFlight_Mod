package xyz.mrfrostydev.onlyfightflight;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = OnlyFofMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OnlyFofClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_INDICATOR;

    static {
        BUILDER.comment("(Only Fight or Flight) Client Configurations");

        BUILDER.push("UI Settings");
        BUILDER.comment("");
        BUILDER.comment("Disables the cross-sword indicator in the bottom center. (Default: false)");
        DISABLE_INDICATOR = BUILDER.define("disableIndicator", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
