package com.triedge.mcclock;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ClockRenderer implements IGuiOverlay {
    public static final ClockRenderer INSTANCE = new ClockRenderer();

    private static final ResourceLocation CLOCK_BACKGROUND = new ResourceLocation(MCClock.MODID, "textures/gui/clock_background.png");
    private static final ResourceLocation CLOCK_SUN = new ResourceLocation(MCClock.MODID, "textures/gui/clock_sun.png");
    private static final ResourceLocation CLOCK_MOON = new ResourceLocation(MCClock.MODID, "textures/gui/clock_moon.png");

    private static final int BACKGROUND_WIDTH = 50;
    private static final int BACKGROUND_HEIGHT = 10;
    private static final int ICON_SIZE = 11;

    @Override
    public void render(net.minecraftforge.client.gui.overlay.ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        Level level = mc.level;
        long dayTime = level.getDayTime() % 24000;

        boolean isNight = dayTime >= 12000;
        float timeProgress;
        ResourceLocation icon;

        if (isNight) {
            timeProgress = (dayTime - 12000) / 12000.0f;
            icon = CLOCK_MOON;
        } else {
            timeProgress = dayTime / 12000.0f;
            icon = CLOCK_SUN;
        }

        int[] pos = calculatePosition(screenWidth, screenHeight);
        int x = pos[0];
        int y = pos[1];

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(CLOCK_BACKGROUND, x, y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        int iconX = x + (int)((BACKGROUND_WIDTH - ICON_SIZE) * timeProgress);
        int iconY = y - 1;
        guiGraphics.blit(icon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        RenderSystem.disableBlend();
    }

    private int[] calculatePosition(int screenWidth, int screenHeight) {
        ClockConfig.ClockPosition position = ClockConfig.POSITION.get();
        int xOffset = ClockConfig.X_OFFSET.get();
        int yOffset = ClockConfig.Y_OFFSET.get();

        int x, y;

        switch (position) {
            case TOP_LEFT:
                x = xOffset;
                y = yOffset;
                break;
            case TOP_RIGHT:
                x = screenWidth - BACKGROUND_WIDTH - xOffset;
                y = yOffset;
                break;
            case BOTTOM_LEFT:
                x = xOffset;
                y = screenHeight - BACKGROUND_HEIGHT - yOffset;
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - BACKGROUND_WIDTH - xOffset;
                y = screenHeight - BACKGROUND_HEIGHT - yOffset;
                break;
            default:
                x = screenWidth - BACKGROUND_WIDTH - xOffset;
                y = yOffset;
                break;
        }

        return new int[]{x, y};
    }
}
