package xyz.mrfrostydev.onlyfightflight;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = OnlyFofMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OnlyFofCommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> TIME_BY_ATTACKING;
    public static final ForgeConfigSpec.ConfigValue<Integer> TIME_BY_DAMAGED;
    public static final ForgeConfigSpec.ConfigValue<Integer> TIME_BY_TARGETED;

    public static final ForgeConfigSpec.ConfigValue<Integer> UPDATE_INTERVAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> RADIUS_CHECK;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_BLACKLIST;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_BLACKLIST;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_AGGRO_COMBAT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_BY_PROXIMITY;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SAY_YES;

    static {
        BUILDER.comment("(Only Fight or Flight) Common Configurations");

        BUILDER.push("Time Adjustments");
        BUILDER.comment("");
        BUILDER.comment("Change the time set in-combat when player is ATTACKING. (Default: 200 in ticks)");
        TIME_BY_ATTACKING = BUILDER.worldRestart().define("timeByAttacking", 200);
        BUILDER.comment("");
        BUILDER.comment("Change the time set in-combat when player is being DAMAGED. (Default: 400 in ticks)");
        TIME_BY_DAMAGED = BUILDER.worldRestart().define("timeByDamaged", 400);
        BUILDER.comment("");
        BUILDER.comment("Change the time set in-combat when player is being TARGETED by a mob. (Default: 300 in ticks)");
        TIME_BY_TARGETED = BUILDER.worldRestart().define("timeByTargeted", 300);
        BUILDER.pop();

        BUILDER.push("Enable/Disable Features");
        BUILDER.comment("");
        BUILDER.comment("Determine whether being targeted by a mod is considered in-combat. \"true\" disables it. (Default: false)");
        DISABLE_AGGRO_COMBAT = BUILDER.worldRestart().define("disableAggroCombat", false);
        BUILDER.comment("");
        BUILDER.comment("Trigger in-combat through proximity with mobs instead of based on aggro. Recommended to use with Mob Blacklist. (Default: false)");
        ENABLE_BY_PROXIMITY = BUILDER.worldRestart().define("enableByProximity", false);
        BUILDER.pop();

        BUILDER.push("Allowed/Disallowed Settings");
        BUILDER.comment("");
        BUILDER.comment("Allow certain blocks to be placed regardless if player is in combat. (If blockBlacklist has any valid entries, this list is ignored)");
        BUILDER.comment("Example: \n blockWhitelist = [\n \t\"minecraft:dirt\",\n \t\"minecraft:crafting_table\",\n \t\"minecraft:glass\"\n ]");
        BUILDER.comment("");
        BLOCK_WHITELIST = BUILDER.worldRestart().defineListAllowEmpty("blockWhitelist", List.of(
                "minecraft:torch", "minecraft:redstone_torch", "minecraft:soul_torch",
                "minecraft:lantern", "minecraft:soul_lantern", "minecraft:cobweb"
        ), ValidItemPredicate.create());
        BUILDER.comment("");
        BUILDER.comment("Deny certain blocks to be placed if player is in combat. This will cause every other block to be placeable regardless if player is in combat.");
        BUILDER.comment("Example: \n blockBlacklist = [\n \t\"minecraft:sand\",\n \t\"minecraft:gravel\",\n \t\"modid:something_block\"\n ]");
        BUILDER.comment("");
        BLOCK_BLACKLIST = BUILDER.worldRestart().defineListAllowEmpty("blockBlacklist", List.of(), ValidItemPredicate.create());

        BUILDER.comment("");
        BUILDER.comment("Allow certain mobs/entities to not trigger in-combat regardless if player attacks or is attacked by them. (If mobBlacklist has any valid entries, this list is ignored)");
        BUILDER.comment("Written the same as blockWhitelist.");
        BUILDER.comment("");
        MOB_WHITELIST = BUILDER.worldRestart().defineListAllowEmpty("mobWhitelist", List.of(), ValidMobPredicate.create());
        BUILDER.comment("");
        BUILDER.comment("Make only certain mobs/entities trigger in-combat effects. This will cause all other mobs/entities");
        BUILDER.comment("to never trigger combat effects regardless if you attack them or been attacked by them.");
        BUILDER.comment("Written the same as blockBlacklist.");
        BUILDER.comment("");
        MOB_BLACKLIST = BUILDER.worldRestart().defineListAllowEmpty("mobBlacklist", List.of(), ValidMobPredicate.create());
        BUILDER.pop();

        BUILDER.push("Performance");
        BUILDER.comment("");
        BUILDER.comment("Change how often each player gets checked for any surrounding mobs targeting them. (Default: 60 in ticks)");
        BUILDER.comment("Be careful not to set this too low as it may cause performance issues!");
        UPDATE_INTERVAL = BUILDER.worldRestart().define("updateInterval", 60);
        BUILDER.comment("");
        BUILDER.comment("Change the radius of the check for surrounding mobs. (Default: 16 in blocks)");
        BUILDER.comment("Don't make this too high!");
        RADIUS_CHECK = BUILDER.worldRestart().define("radiusCheck", 16);
        BUILDER.pop();

        BUILDER.comment("");
        BUILDER.comment("Do you like Pierogi? Yes, this is an important setting. (Default: true)");
        SAY_YES = BUILDER.define("sayYesDoIt", true);

        SPEC = BUILDER.build();
    }

    private static class ValidItemPredicate implements Predicate<Object> {
        public ValidItemPredicate(){}

        static ValidItemPredicate create(){
            return new ValidItemPredicate();
        }

        @Override
        public boolean test(Object o) {
            if(!(o instanceof String s)) return false;
            ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
            if (resourceLocation == null) return false;

            return ForgeRegistries.ITEMS.getValue(resourceLocation) != Items.AIR;
        }
    }

    private static class ValidMobPredicate implements Predicate<Object> {
        public ValidMobPredicate(){}

        static ValidMobPredicate create(){
            return new ValidMobPredicate();
        }

        @Override
        public boolean test(Object o) {
            if(!(o instanceof String s)) return false;
            ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
            if (resourceLocation == null) return false;

            Optional<Holder.Reference<EntityType<?>>> holder = ForgeRegistries.ENTITY_TYPES.getDelegate(resourceLocation);
            return holder.isPresent();
        }
    }

}
