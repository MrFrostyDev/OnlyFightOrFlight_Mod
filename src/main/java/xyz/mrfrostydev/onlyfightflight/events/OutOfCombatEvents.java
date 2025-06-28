package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.mrfrostydev.onlyfightflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightflight.client.OutOfCombatOverlay;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class OutOfCombatEvents {
    public static List<Item> blockCache = new ArrayList<>();
    public static boolean isBlacklist = false;

    @SubscribeEvent
    public static void OutOfCombatStart(OutOfCombatEvent.Start event){
        if(Minecraft.getInstance().player != null){
            OutOfCombatOverlay.startAnimation();
        }
    }

    @SubscribeEvent
    public static void OutOfCombatEnd(OutOfCombatEvent.End event){
        if(Minecraft.getInstance().player != null){
            OutOfCombatOverlay.startAnimation();
        }
    }

    @SubscribeEvent
    public static void OutOfCombatBlockPlace(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        if (player == null) return;
        ItemStack usedStack = event.getItemStack();
        if(!(usedStack.getItem() instanceof BlockItem)) return;

        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
            getBlockCache();
            boolean willCancelPlaceB = isBlacklist && blockCache.stream().anyMatch(usedStack::is) && !data.isOutOfCombat();
            boolean willCancelPlaceW = !isBlacklist && blockCache.stream().noneMatch(usedStack::is) && !data.isOutOfCombat();

            if(willCancelPlaceB || willCancelPlaceW){
                event.setCanceled(true);
                if(player.level().isClientSide()){
                    Minecraft.getInstance().gui.setOverlayMessage(
                            Component.translatable("message.onlyfof.outofcombat.denyplace")
                                    .withStyle(ChatFormatting.DARK_RED),
                            false
                    );
                }
            }
        });

    }

    @SubscribeEvent
    public static void OutOfCombatAttack(LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player)) return;
        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
            data.updateTime(OnlyFofCommonConfig.TIME_BY_ATTACKING.get());
        });
    }

    @SubscribeEvent
    public static void OutOfCombatDamaged(LivingHurtEvent event){
        Entity victim = event.getEntity();
        if(!(victim instanceof Player player)) return;
        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
            data.updateTime(OnlyFofCommonConfig.TIME_BY_DAMAGED.get());
        });
    }

    private static List<Item> getBlockCache(){
        if (!blockCache.isEmpty()) return blockCache;
        List<? extends String> blacklist = OnlyFofCommonConfig.BLOCK_BLACKLIST.get();
        List<? extends String> whitelist = OnlyFofCommonConfig.BLOCK_WHITELIST.get();

        if(!blacklist.isEmpty()){
            for(String s : blacklist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(BuiltInRegistries.ITEM.get(resourceLocation));
            }
            isBlacklist = true;
        }
        else{
            for(String s : whitelist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(BuiltInRegistries.ITEM.get(resourceLocation));
            }
            isBlacklist = false;
        }
        return blockCache;
    }

}
