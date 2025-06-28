package xyz.mrfrostydev.onlyfightorflight;

import net.neoforged.neoforge.common.ModConfigSpec;


public class OnlyFofClientConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> DISABLE_INDICATOR;

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
