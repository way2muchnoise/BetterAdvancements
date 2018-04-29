package betteradvancements.advancements;

import betteradvancements.api.IBetterDisplayInfo;
import betteradvancements.util.ColorHelper;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;

public class BetterDisplayInfo implements IBetterDisplayInfo {
    public static int defaultCompletedIconColor;
    public static int defaultCompletedTitleColor;
    public static int defaultUncompletedIconColor;
    public static int defaultUncompletedTitleColor;
    public static boolean defaultDrawDirectLines;
    public static int defaultCompletedLineColor;
    public static int defaultUncompletedLineColor;

    private ResourceLocation id;
    private int completedIconColor, unCompletedIconColor;
    private int completedTitleColor, unCompletedTitleColor;
    private boolean drawDirectLines;
    private int completedLineColor, unCompletedLineColor;

    public BetterDisplayInfo(Advancement advancement) {
        this(advancement.getId());
        if (advancement instanceof IBetterDisplayInfo) {
            parseIBetterDisplayInfo((IBetterDisplayInfo) advancement);
        }
        if (advancement.getDisplay() instanceof IBetterDisplayInfo) {
            parseIBetterDisplayInfo((IBetterDisplayInfo) advancement.getDisplay());
        }
    }

    public BetterDisplayInfo(ResourceLocation id) {
        this.id = id;
        this.defaults();
    }

    public BetterDisplayInfo(ResourceLocation id, JsonObject displayJson) {
        this(id);
        if (displayJson != null) {
            this.parseDisplayJson(displayJson);
        }
    }

    private void defaults() {
        this.completedIconColor = defaultCompletedIconColor;
        this.completedTitleColor = defaultCompletedTitleColor;
        this.unCompletedIconColor = defaultUncompletedIconColor;
        this.unCompletedTitleColor = defaultUncompletedTitleColor;
        this.drawDirectLines = defaultDrawDirectLines;
        this.unCompletedLineColor = defaultUncompletedLineColor;
        this.completedLineColor = defaultCompletedLineColor;
    }

    private void parseDisplayJson(JsonObject displayJson) {
        if (displayJson.has("completed_icon_color")) {
            this.completedIconColor = ColorHelper.RGB(displayJson.get("completed_icon_color").getAsString());
        }
        if (displayJson.has("uncompleted_icon_color")) {
            this.unCompletedIconColor = ColorHelper.RGB(displayJson.get("uncompleted_icon_color").getAsString());
        }
        if (displayJson.has("completed_title_color")) {
            this.completedTitleColor = ColorHelper.RGB(displayJson.get("completed_title_color").getAsString());
        }
        if (displayJson.has("uncompleted_title_color")) {
            this.unCompletedTitleColor = ColorHelper.RGB(displayJson.get("uncompleted_title_color").getAsString());
        }
        if (displayJson.has("draw_direct_lines")) {
            this.drawDirectLines = displayJson.get("draw_direct_lines").getAsBoolean();
        }
        if (displayJson.has("completed_line_color")) {
            this.completedLineColor = ColorHelper.RGB(displayJson.get("completed_line_color").getAsString());
        }
        if (displayJson.has("uncompleted_line_color")) {
            this.unCompletedLineColor = ColorHelper.RGB(displayJson.get("uncompleted_line_color").getAsString());
        }
    }

    private void parseIBetterDisplayInfo(IBetterDisplayInfo betterDisplayInfo) {
        if (betterDisplayInfo.getCompletedIconColor() != -1) {
            this.completedIconColor = betterDisplayInfo.getCompletedIconColor();
        }
        if (betterDisplayInfo.getUnCompletedIconColor() != -1) {
            this.unCompletedIconColor = betterDisplayInfo.getUnCompletedIconColor();
        }
        if (betterDisplayInfo.getCompletedTitleColor() != -1) {
            this.completedTitleColor = betterDisplayInfo.getCompletedTitleColor();
        }
        if (betterDisplayInfo.getUnCompletedTitleColor() != -1) {
            this.unCompletedTitleColor = betterDisplayInfo.getUnCompletedTitleColor();
        }
        if (betterDisplayInfo.drawDirectLines() != null) {
            this.drawDirectLines = betterDisplayInfo.drawDirectLines();
        }
        if (betterDisplayInfo.getCompletedLineColor() != -1) {
            this.completedLineColor = betterDisplayInfo.getCompletedLineColor();
        }
        if (betterDisplayInfo.getUnCompletedLineColor() != -1) {
            this.unCompletedLineColor = betterDisplayInfo.getUnCompletedLineColor();
        }
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public int getCompletedIconColor() {
        return this.completedIconColor;
    }

    public int getUnCompletedIconColor() {
        return this.unCompletedIconColor;
    }

    public int getCompletedTitleColor() {
        return this.completedTitleColor;
    }

    public int getUnCompletedTitleColor() {
        return this.unCompletedTitleColor;
    }
    
    public Boolean drawDirectLines() {
        return this.drawDirectLines;
    }
    
    public int getCompletedLineColor() {
        return this.completedLineColor;
    }
    
    public int getUnCompletedLineColor() {
        return this.unCompletedLineColor;
    }
}
