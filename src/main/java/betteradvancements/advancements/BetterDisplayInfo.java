package betteradvancements.advancements;

import betteradvancements.util.ColorHelper;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class BetterDisplayInfo {
    public static int defaultCompletedIconColor;
    public static int defaultCompletedTitleColor;
    public static int defaultUncompletedIconColor;
    public static int defaultUncompletedTitleColor;

    private ResourceLocation id;
    private int completedIconColor, unCompletedIconColor;
    private int completedTitleColor, unCompletedTitleColor;

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
            completedIconColor = ColorHelper.RGB(displayJson.get("completed_icon_color").getAsString());
        }
        if (displayJson.has("uncompleted_icon_color")) {
            unCompletedIconColor = ColorHelper.RGB(displayJson.get("uncompleted_icon_color").getAsString());
        }
        if (displayJson.has("completed_title_color")) {
            completedTitleColor = ColorHelper.RGB(displayJson.get("completed_title_color").getAsString());
        }
        if (displayJson.has("uncompleted_title_color")) {
            unCompletedTitleColor = ColorHelper.RGB(displayJson.get("uncompleted_title_color").getAsString());
        }
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getCompletedIconColor() {
        return completedIconColor;
    }

    public int getUnCompletedIconColor() {
        return unCompletedIconColor;
    }

    public int getCompletedTitleColor() {
        return completedTitleColor;
    }

    public int getUnCompletedTitleColor() {
        return unCompletedTitleColor;
    }
}
