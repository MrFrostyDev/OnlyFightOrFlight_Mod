package xyz.mrfrostydev.onlyfightflight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import xyz.mrfrostydev.onlyfightflight.OnlyFofClientConfig;
import xyz.mrfrostydev.onlyfightflight.OnlyFofMain;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatCapability;
import xyz.mrfrostydev.onlyfightflight.data.OutOfCombatData;

public class OutOfCombatOverlay implements IGuiOverlay {
    private static final ResourceLocation COMBAT_START_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "textures/gui/combat/fullcombaticon_start.png");
    private static final ResourceLocation COMBAT_END_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnlyFofMain.MOD_ID, "textures/gui/combat/fullcombaticon_end.png");
    private static final int[] TEXTURE_ARRAY = {0, 64, 128, 192, 256, 320};
    private static final int DEFAULT_PLAYTIME = 20;

    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 64;

    static final int TEXTURE_WIDTH = IMAGE_WIDTH;
    static final int TEXTURE_HEIGHT = 384;

    private static boolean active = false;
    private static int nextTickEnd = 0;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;

        if(player == null
                || Minecraft.getInstance().options.hideGui
                || player.isSpectator()
                || !player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).isPresent()
                || OnlyFofClientConfig.DISABLE_INDICATOR.get()
        ) return;

        if (active && player.tickCount >= nextTickEnd || player.tickCount < 40){
            active = false;
            nextTickEnd = player.tickCount;
        }

        OutOfCombatData data = player.getCapability(OutOfCombatCapability.OUT_OF_COMBAT).resolve().get();

        int posX = (screenWidth / 2) - (IMAGE_WIDTH / 2);
        int posY = screenHeight - 50 - (IMAGE_HEIGHT / 2);

        // guiGraphics.blit(ResourceLocation atlasLocation,
        // int x, int y,
        // float uOffset, float vOffset,
        // int width, int height,
        // int textureWidth, int textureHeight)

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(active){
            if (player.tickCount >= nextTickEnd || player.tickCount < 40){
                active = false;
                nextTickEnd = player.tickCount;
            }

            float progress = (float)(DEFAULT_PLAYTIME - (nextTickEnd - player.tickCount)) / DEFAULT_PLAYTIME;
            int index = Mth.floor((progress % ((float)DEFAULT_PLAYTIME / TEXTURE_ARRAY.length)) * TEXTURE_ARRAY.length);
            if(index >= TEXTURE_ARRAY.length || index < 0) return;

            if(data.isOutOfCombat()){
                RenderSystem.setShaderTexture(0, COMBAT_END_TEXTURE);
                guiGraphics.blit(COMBAT_END_TEXTURE,
                        posX, posY,
                        0, TEXTURE_ARRAY[index],
                        IMAGE_WIDTH, IMAGE_HEIGHT,
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
            else{
                RenderSystem.setShaderTexture(0, COMBAT_START_TEXTURE);
                guiGraphics.blit(COMBAT_START_TEXTURE,
                        posX, posY,
                        0, TEXTURE_ARRAY[index],
                        IMAGE_WIDTH, IMAGE_HEIGHT,
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
        else if(!data.isOutOfCombat()){
            RenderSystem.setShaderTexture(0, COMBAT_START_TEXTURE);
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
