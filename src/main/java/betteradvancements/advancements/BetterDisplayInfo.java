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

    private ResourceLocation id;
    private int completedIconColor, unCompletedIconColor;
    private int completedTitleColor, unCompletedTitleColor;

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
}
