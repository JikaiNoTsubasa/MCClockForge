package com.triedge.mcclock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClockConfigScreen extends Screen {
    private final Screen lastScreen;
    private CycleButton<ClockConfig.ClockPosition> positionButton;
    private IntSlider xOffsetSlider;
    private IntSlider yOffsetSlider;
    private CycleButton<Boolean> showTimeButton;
    private CycleButton<Boolean> formatButton;
    private CycleButton<Boolean> customFontButton;

    public ClockConfigScreen(Screen lastScreen) {
        super(Component.literal("Clock Configuration"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        this.positionButton = CycleButton.<ClockConfig.ClockPosition>builder(pos -> Component.literal("Position: " + pos.name()))
                .withValues(ClockConfig.ClockPosition.values())
                .withInitialValue(ClockConfig.POSITION.get())
                .create(centerX - 100, startY, 200, 20, Component.literal("Position"), (button, value) -> {
                    ClockConfig.POSITION.set(value);
                    ClockConfig.SPEC.save();
                });

        this.xOffsetSlider = new IntSlider(centerX - 100, startY + 30, 200, 20,
                Component.literal("X Offset: "), Component.empty(),
                -1000, 1000, ClockConfig.X_OFFSET.get(), ClockConfig.X_OFFSET);

        this.yOffsetSlider = new IntSlider(centerX - 100, startY + 60, 200, 20,
                Component.literal("Y Offset: "), Component.empty(),
                -1000, 1000, ClockConfig.Y_OFFSET.get(), ClockConfig.Y_OFFSET);

        this.showTimeButton = CycleButton.<Boolean>builder(show ->
                Component.literal("Show Time: " + (show ? "ON" : "OFF")))
                .withValues(false, true)
                .withInitialValue(ClockConfig.SHOW_TIME.get())
                .create(centerX - 100, startY + 90, 200, 20, Component.literal("Show Time"),
                        (button, value) -> {
                            ClockConfig.SHOW_TIME.set(value);
                            ClockConfig.SPEC.save();
                        });

        this.formatButton = CycleButton.<Boolean>builder(format ->
                Component.literal("Format: " + (format ? "24H" : "12H")))
                .withValues(false, true)
                .withInitialValue(ClockConfig.IS_24_HOUR_FORMAT.get())
                .create(centerX - 100, startY + 120, 200, 20, Component.literal("Time Format"),
                        (button, value) -> {
                            ClockConfig.IS_24_HOUR_FORMAT.set(value);
                            ClockConfig.SPEC.save();
                        });

        this.customFontButton = CycleButton.<Boolean>builder(custom ->
                Component.literal("Custom Font: " + (custom ? "ON" : "OFF")))
                .withValues(false, true)
                .withInitialValue(ClockConfig.USE_CUSTOM_FONT.get())
                .create(centerX - 100, startY + 150, 200, 20, Component.literal("Custom Font"),
                        (button, value) -> {
                            ClockConfig.USE_CUSTOM_FONT.set(value);
                            ClockConfig.SPEC.save();
                        });

        this.addRenderableWidget(positionButton);
        this.addRenderableWidget(xOffsetSlider);
        this.addRenderableWidget(yOffsetSlider);
        this.addRenderableWidget(showTimeButton);
        this.addRenderableWidget(formatButton);
        this.addRenderableWidget(customFontButton);

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            this.minecraft.setScreen(lastScreen);
        }).bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }

    private static class IntSlider extends AbstractSliderButton {
        private final int minValue;
        private final int maxValue;
        private final ForgeConfigSpec.IntValue configValue;
        private final Component prefix;

        public IntSlider(int x, int y, int width, int height, Component prefix, Component suffix,
                        int minValue, int maxValue, int currentValue, ForgeConfigSpec.IntValue configValue) {
            super(x, y, width, height, prefix, (double) (currentValue - minValue) / (maxValue - minValue));
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.configValue = configValue;
            this.prefix = prefix;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            int value = (int) (this.value * (maxValue - minValue) + minValue);
            this.setMessage(Component.literal(prefix.getString() + value));
        }

        @Override
        protected void applyValue() {
            int value = (int) (this.value * (maxValue - minValue) + minValue);
            configValue.set(value);
            ClockConfig.SPEC.save();
        }
    }
}
