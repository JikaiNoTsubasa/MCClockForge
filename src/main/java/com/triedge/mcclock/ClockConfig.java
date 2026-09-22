package com.triedge.mcclock;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClockConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.EnumValue<ClockPosition> POSITION;
    public static final ForgeConfigSpec.IntValue X_OFFSET;
    public static final ForgeConfigSpec.IntValue Y_OFFSET;
    public static final ForgeConfigSpec.BooleanValue SHOW_TIME;
    public static final ForgeConfigSpec.BooleanValue IS_24_HOUR_FORMAT;
    public static final ForgeConfigSpec.BooleanValue USE_CUSTOM_FONT;

    static {
        BUILDER.push("Clock Display Settings");

        POSITION = BUILDER
                .comment("Clock position on screen: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT")
                .defineEnum("position", ClockPosition.BOTTOM_RIGHT);

        X_OFFSET = BUILDER
                .comment("Horizontal offset from the corner (in pixels)")
                .defineInRange("xOffset", 10, -1000, 1000);

        Y_OFFSET = BUILDER
                .comment("Vertical offset from the corner (in pixels)")
                .defineInRange("yOffset", 10, -1000, 1000);

        SHOW_TIME = BUILDER
                .comment("Show the time text above the clock")
                .define("showTime", true);

        IS_24_HOUR_FORMAT = BUILDER
                .comment("Display time in 24-hour format (true) or 12-hour format (false)")
                .define("is24HourFormat", false);

        USE_CUSTOM_FONT = BUILDER
                .comment("Use custom bitmap font for time display")
                .define("useCustomFont", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public enum ClockPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
