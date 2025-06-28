package xyz.mrfrostydev.onlyfightorflight.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofClientConfig;
import xyz.mrfrostydev.onlyfightorflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightorflight.data.OutOfCombatData;
import xyz.mrfrostydev.onlyfightorflight.registries.DataAttachmentRegistry;

public class OutOfCombatOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation COMBAT_START_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "textures/gui/combat/fullcombaticon_start.png");
    private static final ResourceLocation COMBAT_END_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "textures/gui/combat/fullcombaticon_end.png");
    private static final int[] TEXTURE_ARRAY = {0, 64, 128, 192, 256, 320};
    private static final int DEFAULT_PLAYTIME = 15;

    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 64;

    static final int TEXTURE_WIDTH = IMAGE_WIDTH;
    static final int TEXTURE_HEIGHT = 384;

    private static boolean active = false;
    private static int nextTickEnd = 0;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;

        if(player == null
                || Minecraft.getInstance().options.hideGui
                || player.isSpectator()
                || OnlyFofClientConfig.DISABLE_INDICATOR.get()
        ) return;

        if (active && player.tickCount >= nextTickEnd || player.tickCount < 40){
            active = false;
            nextTickEnd = player.tickCount;
        }

        OutOfCombatData data = player.getData(DataAttachmentRegistry.OUT_OF_COMBAT);

        int posX = (guiGraphics.guiWidth() / 2) - (IMAGE_WIDTH / 2) + 1;
        int posY = guiGraphics.guiHeight() - 50 - (IMAGE_HEIGHT / 2);

        // guiGraphics.blit(ResourceLocation atlasLocation,
        // int x, int y,
        // float uOffset, float vOffset,
        // int width, int height,
        // int textureWidth, int textureHeight)

        if(active){
            float progress = (float)(DEFAULT_PLAYTIME - (nextTickEnd - player.tickCount)) / DEFAULT_PLAYTIME;
            int index = Mth.floor((progress % ((float)DEFAULT_PLAYTIME / TEXTURE_ARRAY.length)) * TEXTURE_ARRAY.length);
            if(index >= TEXTURE_ARRAY.length || index < 0) return;

            if(data.isOutOfCombat()){
                guiGraphics.blit(COMBAT_END_TEXTURE,
                        posX, posY,
                        0, TEXTURE_ARRAY[index],
                        IMAGE_WIDTH, IMAGE_HEIGHT,
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
            else{
                guiGraphics.blit(COMBAT_START_TEXTURE,
                        posX, posY,
                        0, TEXTURE_ARRAY[index],
                        IMAGE_WIDTH, IMAGE_HEIGHT,
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
        else if(!data.isOutOfCombat()){
            guiGraphics.blit(COMBAT_START_TEXTURE,
                    posX, posY,
                    0, TEXTURE_ARRAY[TEXTURE_ARRAY.length - 1],
                    IMAGE_WIDTH, IMAGE_HEIGHT,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    public static void startAnimation(){
        if (Minecraft.getInstance().options.hideGui
                || Minecraft.getInstance().player == null
                || Minecraft.getInstance().player.isSpectator()
                || active) return;
        nextTickEnd = Minecraft.getInstance().player.tickCount + DEFAULT_PLAYTIME;
        active = true;
    }
}
