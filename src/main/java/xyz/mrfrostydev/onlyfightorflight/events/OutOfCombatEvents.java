package xyz.mrfrostydev.onlyfightorflight.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class OutOfCombatEvents {
    public static List<Item> blockCache = new ArrayList<>();
    public static boolean isBlockBlacklist = false;

    public static List<EntityType<?>> mobCache = new ArrayList<>();
    public static boolean isMobBlacklist = false;


    @SubscribeEvent
    public static void OutOfCombatBlockPlace(UseItemOnBlockEvent event){
        Player player = event.getPlayer();
        if (player == null || player.isCreative()) return;
        ItemStack usedStack = player.getItemInHand(event.getHand());
        if(!(usedStack.getItem() instanceof BlockItem)) return;

        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK){
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);

            boolean willCancelPlace = true;
            if(isBlacklistOrWhitelistSet(OnlyFofCommonConfig.BLOCK_BLACKLIST.get(),  OnlyFofCommonConfig.BLOCK_WHITELIST.get())){
                getBlockCache();
                boolean willCancelPlaceB = isBlockBlacklist && blockCache.stream().anyMatch(usedStack::is);
                boolean willCancelPlaceW = !isBlockBlacklist && blockCache.stream().noneMatch(usedStack::is);

                if(!willCancelPlaceB && !willCancelPlaceW){
                    willCancelPlace = false;
                }
            }

            if(willCancelPlace && !data.isOutOfCombat()){
                event.setCanceled(true);
                if(player.level().isClientSide()){
                    Minecraft.getInstance().gui.setOverlayMessage(
                            Component.translatable("message.onlyfof.outofcombat.denyplace")
                                    .withStyle(ChatFormatting.DARK_RED),
                            false
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void OutOfCombatAttack(LivingDamageEvent.Post event){
        Entity attacker = event.getSource().getEntity();
        Entity victim = event.getEntity();
        int timeByAttacking = OnlyFofCommonConfig.TIME_BY_ATTACKING.get();

        if (!(attacker instanceof Player player) || timeByAttacking <= 0) return;
        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);

        boolean willCancelPlace = true;
        if(isBlacklistOrWhitelistSet(OnlyFofCommonConfig.MOB_BLACKLIST.get(),  OnlyFofCommonConfig.MOB_WHITELIST.get())){
            getMobCache();
            boolean willCancelPlaceB = isMobBlacklist && mobCache.stream().anyMatch(e -> victim.getType().equals(e));
            boolean willCancelPlaceW = !isMobBlacklist && mobCache.stream().noneMatch(e -> victim.getType().equals(e));

            if(!willCancelPlaceB && !willCancelPlaceW){
                willCancelPlace = false;
            }
        }
        if(willCancelPlace){
            data.updateTime(timeByAttacking);
        }
    }

    @SubscribeEvent
    public static void OutOfCombatDamaged(LivingDamageEvent.Post event){
        Entity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        int timeByDamaged = OnlyFofCommonConfig.TIME_BY_DAMAGED.get();

        if(!(victim instanceof Player player) || attacker == null || timeByDamaged <= 0) return;
        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);

        boolean willCancelPlace = true;
        if(isBlacklistOrWhitelistSet(OnlyFofCommonConfig.MOB_BLACKLIST.get(),  OnlyFofCommonConfig.MOB_WHITELIST.get())){
            getMobCache();
            boolean willCancelPlaceB = isMobBlacklist && mobCache.stream().anyMatch(e -> attacker.getType().equals(e));
            boolean willCancelPlaceW = !isMobBlacklist && mobCache.stream().noneMatch(e -> attacker.getType().equals(e));

            if(!willCancelPlaceB && !willCancelPlaceW){
                willCancelPlace = false;
            }
        }
        if(willCancelPlace){
            data.updateTime(timeByDamaged);
        }
    }

    public static List<Item> getBlockCache(){
        List<? extends String> blacklist = OnlyFofCommonConfig.BLOCK_BLACKLIST.get();
        List<? extends String> whitelist = OnlyFofCommonConfig.BLOCK_WHITELIST.get();

        if (!blockCache.isEmpty() || !isBlacklistOrWhitelistSet(blacklist, whitelist)) return blockCache;

        if(!blacklist.isEmpty()){
            for(String s : blacklist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(BuiltInRegistries.ITEM.get(resourceLocation));
            }
            isBlockBlacklist = true;
        }
        else{
            for(String s : whitelist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(BuiltInRegistries.ITEM.get(resourceLocation));
            }
            isBlockBlacklist = false;
        }
        return blockCache;
    }

    public static List<EntityType<?>> getMobCache(){
        List<? extends String> blacklist = OnlyFofCommonConfig.MOB_BLACKLIST.get();
        List<? extends String> whitelist = OnlyFofCommonConfig.MOB_WHITELIST.get();

        if (!mobCache.isEmpty() || !isBlacklistOrWhitelistSet(blacklist, whitelist)) return mobCache;

        if(!blacklist.isEmpty()){
            for(String s : blacklist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                mobCache.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
            }
            isMobBlacklist = true;
        }
        else{
            for(String s : whitelist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                mobCache.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
            }
            isMobBlacklist = false;
        }
        return mobCache;
    }

    public static boolean isBlacklistOrWhitelistSet(List<? extends String> b, List<? extends String> w){
        return (!b.isEmpty() || !w.isEmpty());
    }
}
