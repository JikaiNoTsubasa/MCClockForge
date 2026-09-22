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
    private static final ResourceLocation CLOCK_DAWN = new ResourceLocation(MCClock.MODID, "textures/gui/clock_dawn.png");
    private static final ResourceLocation CLOCK_NIGHT = new ResourceLocation(MCClock.MODID, "textures/gui/clock_night.png");
    private static final ResourceLocation CLOCK_MOON = new ResourceLocation(MCClock.MODID, "textures/gui/clock_moon.png");
    private static final ResourceLocation CLOCK_FONT = new ResourceLocation(MCClock.MODID, "textures/gui/clock_numbers.png");

    private static final int BACKGROUND_WIDTH = 50;
    private static final int BACKGROUND_HEIGHT = 10;
    private static final int ICON_SIZE = 11;

    // Dimensions de la police bitmap personnalisée
    private static final int CHAR_WIDTH = 5;
    private static final int CHAR_HEIGHT = 7;
    private static final int CHARS_PER_ROW = 10;
    private static final String CHAR_MAP = "0123456789:ABCDEFGHIJKLMNOPQRSTUVWXYZ   ";

    @Override
    public void render(net.minecraftforge.client.gui.overlay.ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        Level level = mc.level;
        long dayTime = level.getDayTime() % 24000;

        float timeProgress;
        ResourceLocation icon;

        if (dayTime < 13000) {
            // Cycle jour: 0 to 13000 (6h matin à 19h)
            timeProgress = dayTime / 13000.0f;
            if (dayTime < 12000) {
                icon = CLOCK_SUN;
            } else {
                icon = CLOCK_DAWN;
            }
        } else {
            // Cycle nuit: 13000 to 24000 (19h à 6h matin)
            timeProgress = (dayTime - 13000) / 11000.0f;
            if (dayTime < 23000) {
                icon = CLOCK_NIGHT;
            } else {
                icon = CLOCK_MOON;
            }
        }

        int[] pos = calculatePosition(screenWidth, screenHeight);
        int x = pos[0];
        int y = pos[1];

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(CLOCK_BACKGROUND, x, y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        int iconX = x + (int)(BACKGROUND_WIDTH * timeProgress) - ICON_SIZE / 2;
        int iconY = y - 1;
        guiGraphics.blit(icon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        if (ClockConfig.SHOW_TIME.get()) {
            boolean is24Hour = ClockConfig.IS_24_HOUR_FORMAT.get();
            String timeString = formatTime(dayTime, is24Hour);

            if (ClockConfig.USE_CUSTOM_FONT.get()) {
                // Utiliser la police bitmap personnalisée
                int textWidth = timeString.length() * CHAR_WIDTH;
                int textX = x + (BACKGROUND_WIDTH / 2) - (textWidth / 2);
                int textY = y - 15;
                drawCustomText(guiGraphics, timeString, textX, textY);
            } else {
                // Utiliser la police par défaut de Minecraft
                int textWidth = mc.font.width(timeString);
                int textX = x + (BACKGROUND_WIDTH / 2) - (textWidth / 2);
                int textY = y - 15;
                guiGraphics.drawString(mc.font, timeString, textX, textY, 0xFFFFFF, true);
            }
        }

        RenderSystem.disableBlend();
    }

    private void drawCustomText(GuiGraphics guiGraphics, String text, int x, int y) {
        int textureWidth = CHARS_PER_ROW * CHAR_WIDTH;  // 10 * 5 = 50 pixels
        int textureHeight = 4 * CHAR_HEIGHT;            // 4 * 7 = 28 pixels

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charIndex = CHAR_MAP.indexOf(c);

            if (charIndex >= 0) {
                int texX = (charIndex % CHARS_PER_ROW) * CHAR_WIDTH;
                int texY = (charIndex / CHARS_PER_ROW) * CHAR_HEIGHT;

                guiGraphics.blit(CLOCK_FONT, x + (i * CHAR_WIDTH), y, texX, texY,
                               CHAR_WIDTH, CHAR_HEIGHT, textureWidth, textureHeight);
            }
        }
    }

    private String formatTime(long dayTime, boolean is24HourFormat) {
        int totalMinutes = (int) ((dayTime * 1440) / 24000);
        int hours = (totalMinutes / 60 + 6) % 24;  // Minecraft starts at 6am, so add 6 hours offset
        int minutes = totalMinutes % 60;

        if (is24HourFormat) {
            return String.format("%02d:%02d", hours, minutes);
        } else {
            int hour12 = hours % 12;
            if (hour12 == 0) hour12 = 12;
            String ampm = hours >= 12 ? "PM" : "AM";
            return String.format("%02d:%02d %s", hour12, minutes, ampm);
        }
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
