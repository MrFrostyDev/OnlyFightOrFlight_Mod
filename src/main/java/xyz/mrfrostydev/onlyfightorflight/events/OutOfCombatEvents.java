package xyz.mrfrostydev.onlyfightorflight.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofCommonConfig;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class OutOfCombatEvents {
    public static List<Item> blockCache = new ArrayList<>();
    public static boolean isBlacklist = false;

    @SubscribeEvent
    public static void OutOfCombatBlockPlace(UseItemOnBlockEvent event){
        Player player = event.getPlayer();
        if (player == null) return;
        ItemStack usedStack = player.getItemInHand(event.getHand());
        if(!(usedStack.getItem() instanceof BlockItem)) return;

        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK){
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);

            getBlockCache();
            boolean willCancelPlaceB = isBlacklist && blockCache.stream().anyMatch(usedStack::is) && !data.isOutOfCombat();
            boolean willCancelPlaceW = !isBlacklist && blockCache.stream().noneMatch(usedStack::is) && !data.isOutOfCombat();

            if(willCancelPlaceB || willCancelPlaceW){
                event.cancelWithResult(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
                if(event.getLevel().isClientSide()){
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
        if (!(attacker instanceof Player player)) return;
        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
        data.updateTime(OnlyFofCommonConfig.TIME_BY_ATTACKING.get());

    }

    @SubscribeEvent
    public static void OutOfCombatDamaged(LivingDamageEvent.Post event){
        Entity victim = event.getEntity();
        if(!(victim instanceof Player player)) return;

        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
        data.updateTime(OnlyFofCommonConfig.TIME_BY_DAMAGED.get());
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
