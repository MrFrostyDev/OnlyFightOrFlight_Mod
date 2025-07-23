package xyz.mrfrostydev.onlyfightflight.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.mrfrostydev.onlyfightflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;
import xyz.mrfrostydev.onlyfightflight.network.ClientPlayOutOfCombatPacket;
import xyz.mrfrostydev.onlyfightflight.network.OnlyFofPacketHandler;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class OutOfCombatEvents {
    public static List<Item> blockCache = new ArrayList<>();
    public static boolean isBlockBlacklist = false;

    public static List<EntityType<?>> mobCache = new ArrayList<>();
    public static boolean isMobBlacklist = false;

    @SubscribeEvent
    public static void OutOfCombatStart(OutOfCombatEvent.Start event){
        if(event.getEntity() instanceof ServerPlayer svplayer){
            OnlyFofPacketHandler.sendToPlayer(svplayer, new ClientPlayOutOfCombatPacket());
        }
    }

    @SubscribeEvent
    public static void OutOfCombatEnd(OutOfCombatEvent.End event){
        if(event.getEntity() instanceof ServerPlayer svplayer){
            OnlyFofPacketHandler.sendToPlayer(svplayer, new ClientPlayOutOfCombatPacket());
        }
    }

    @SubscribeEvent
    public static void OutOfCombatBlockPlace(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        if (player == null || player.isCreative()) return;
        ItemStack usedStack = event.getItemStack();
        if(!(usedStack.getItem() instanceof BlockItem)) return;

        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
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
        });
    }

    @SubscribeEvent
    public static void OutOfCombatAttack(LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        Entity victim = event.getEntity();
        int timeByAttacking = OnlyFofCommonConfig.TIME_BY_ATTACKING.get();

        if (!(attacker instanceof Player player) || timeByAttacking <= 0) return;
        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
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
        });
    }

    @SubscribeEvent
    public static void OutOfCombatDamaged(LivingHurtEvent event){
        Entity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        int timeByDamaged = OnlyFofCommonConfig.TIME_BY_DAMAGED.get();

        if(!(victim instanceof Player player) || attacker == null || timeByDamaged <= 0) return;
        player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).ifPresent((data) -> {
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
        });
    }

    public static List<Item> getBlockCache(){
        List<? extends String> blacklist = OnlyFofCommonConfig.BLOCK_BLACKLIST.get();
        List<? extends String> whitelist = OnlyFofCommonConfig.BLOCK_WHITELIST.get();

        if (!blockCache.isEmpty() || !isBlacklistOrWhitelistSet(blacklist, whitelist)) return blockCache;

        if(!blacklist.isEmpty()){
            for(String s : blacklist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(ForgeRegistries.ITEMS.getValue(resourceLocation));
            }
            isBlockBlacklist = true;
        }
        else{
            for(String s : whitelist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                blockCache.add(ForgeRegistries.ITEMS.getValue(resourceLocation));
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
                mobCache.add(ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation));
            }
            isMobBlacklist = true;
        }
        else{
            for(String s : whitelist){
                ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
                if (resourceLocation == null) continue;
                mobCache.add(ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation));
            }
            isMobBlacklist = false;
        }
        return mobCache;
    }

    public static boolean isBlacklistOrWhitelistSet(List<? extends String> b, List<? extends String> w){
        return (!b.isEmpty() || !w.isEmpty());
    }
}
