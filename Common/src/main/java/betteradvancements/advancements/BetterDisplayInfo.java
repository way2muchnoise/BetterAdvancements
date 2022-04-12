package betteradvancements.advancements;

import betteradvancements.api.IBetterDisplayInfo;
import betteradvancements.util.ColorHelper;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.resources.ResourceLocation;

public class BetterDisplayInfo implements IBetterDisplayInfo {
    public static final String defaultMinecraftCompletedIconColor = "#DBA213", defaultMinecraftUncompletedIconColor = "#FFFFFF";
    public static final String defaultMinecraftCompletedTitleColor = "#DBA213", defaultMinecraftUncompletedTitleColor = "#0489C1";
    private static final int minecraftCompletedIconColor = ColorHelper.RGB(defaultMinecraftCompletedIconColor);
    private static final int minecraftUncompletedIconColor = ColorHelper.RGB(defaultMinecraftUncompletedIconColor);
    private static final int minecraftCompletedTitleColor = ColorHelper.RGB(defaultMinecraftCompletedTitleColor);
    private static final int minecraftUncompletedTitleColor = ColorHelper.RGB(defaultMinecraftUncompletedTitleColor);
    public static int defaultCompletedIconColor = minecraftCompletedIconColor;
    public static int defaultUncompletedIconColor = minecraftUncompletedIconColor;
    public static int defaultCompletedTitleColor = minecraftCompletedTitleColor;
    public static int defaultUncompletedTitleColor = minecraftUncompletedTitleColor;
    public static boolean defaultDrawDirectLines = false;
    public static int defaultCompletedLineColor = ColorHelper.RGB("#FFFFFF");
    public static int defaultUncompletedLineColor = ColorHelper.RGB("#FFFFFF");
    public static boolean defaultHideLines = false;
    private static final int WHITE = ColorHelper.RGB(1F, 1F, 1F);


    private ResourceLocation id;
    private int completedIconColor, unCompletedIconColor;
    private int completedTitleColor, unCompletedTitleColor;
    private boolean drawDirectLines;
    private int completedLineColor, unCompletedLineColor;
    private Integer posX, posY;
    private boolean hideLines;
    private boolean allowDragging;

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
        this.posX = null;
        this.posY = null;
        this.hideLines = defaultHideLines;
        this.allowDragging = false;
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
        if (displayJson.has("pos_x")) {
            this.posX = displayJson.get("pos_x").getAsInt();
        }
        if (displayJson.has("pos_y")) {
            this.posY = displayJson.get("pos_y").getAsInt();
        }
        if (displayJson.has("hide_lines")) {
            this.hideLines = displayJson.get("hide_lines").getAsBoolean();
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
        if (betterDisplayInfo.getPosX() != null) {
            this.posX = betterDisplayInfo.getPosX();
        }
        if (betterDisplayInfo.getPosY() != null) {
            this.posY = betterDisplayInfo.getPosY();
        }
        if (betterDisplayInfo.hideLines() != null) {
            this.hideLines = betterDisplayInfo.hideLines();
        }
        this.allowDragging = betterDisplayInfo.allowDragging();
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
    
    public Integer getPosX() {
        return this.posX;
    }
    
    public Integer getPosY() {
        return this.posY;
    }
    
    public Boolean hideLines() {
        return this.hideLines;
    }
    
    public boolean allowDragging() {
        return this.allowDragging;
    }

    public boolean hasCustomIconColor() {
        return this.completedIconColor != minecraftCompletedIconColor || this.unCompletedIconColor != minecraftUncompletedIconColor;
    }

    public boolean hasCustomTitleColor() {
        return this.completedTitleColor != minecraftCompletedTitleColor || this.unCompletedTitleColor != minecraftUncompletedTitleColor;
    }

    public int getIconYMultiplier(AdvancementWidgetType state) {
        if (hasCustomIconColor()) {
            return 2;
        }
        return state == AdvancementWidgetType.OBTAINED ? 0 : 1;
    }

    public int getIconColor(AdvancementWidgetType state) {
        if (!hasCustomIconColor()) {
            return WHITE;
        }
        return state == AdvancementWidgetType.OBTAINED ? getCompletedIconColor() : getUnCompletedIconColor();
    }

    public int getTitleYMultiplier(AdvancementWidgetType state) {
        if (hasCustomTitleColor()) {
            return 3;
        }
        return state == AdvancementWidgetType.OBTAINED ? 0 : 1;
    }

    public int getTitleColor(AdvancementWidgetType state) {
        if (!hasCustomIconColor()) {
            return WHITE;
        }
        return state == AdvancementWidgetType.OBTAINED ? getCompletedTitleColor() : getUnCompletedTitleColor();
    }

}
