package xyz.mrfrostydev.onlyfightflight.onlyfightorflight.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightflight.onlyfightorflight.registries.DataAttachmentRegistry;

@EventBusSubscriber
public class OutOfCombatEvents {
    public static final int ATTACK_TIME = 200;
    public static final int DAMAGED_TIME = 500;

    @SubscribeEvent
    public static void OutOfCombatBlockPlace(UseItemOnBlockEvent event){
        Player player = event.getPlayer();
        if (player == null) return;

        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.BLOCK){
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            if(!data.isOutOfCombat()){
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
    public static void OutOfCombatAttack(AttackEntityEvent event){
        Player player = event.getEntity();
        Entity target = event.getTarget();

        boolean isValid = target.isAttackable() && target.isAlive();

        if(isValid){
            OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
            if(data.isOutOfCombat()){
                data.startCombat(ATTACK_TIME);
            }
        }
    }

    @SubscribeEvent
    public static void OutOfCombatDamaged(LivingDamageEvent event){
        Entity victim = event.getEntity();
        if(!(victim instanceof Player player)) return;

        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);
        if(data.isOutOfCombat()){
            data.startCombat(DAMAGED_TIME);
        }

    }

}
