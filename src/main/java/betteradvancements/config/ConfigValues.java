package betteradvancements.config;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.gui.GuiBetterAdvancementTab;
import betteradvancements.gui.BetterAdvancementsScreenButtonWidget;
import betteradvancements.gui.GuiScreenBetterAdvancements;
import betteradvancements.util.ColorHelper;
import betteradvancements.util.CriteriaDetail;
import betteradvancements.util.CriterionGrid;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigValues {

    public static ForgeConfigSpec.ConfigValue<String> defaultUncompletedIconColor;
    public static ForgeConfigSpec.ConfigValue<String> defaultUncompletedTitleColor;
    public static ForgeConfigSpec.ConfigValue<String> defaultCompletedIconColor;
    public static ForgeConfigSpec.ConfigValue<String> defaultCompletedTitleColor;

    public static ForgeConfigSpec.BooleanValue doFade;
    public static ForgeConfigSpec.BooleanValue showDebugCoordinates;
    public static ForgeConfigSpec.BooleanValue orderTabsAlphabetically;
    public static ForgeConfigSpec.IntValue uiScaling;

    public static ForgeConfigSpec.ConfigValue<String> detailLevel;
    public static ForgeConfigSpec.BooleanValue requiresShift;
    public static ForgeConfigSpec.BooleanValue addToInventory;

    public static ForgeConfigSpec.BooleanValue defaultDrawDirectLines;
    public static ForgeConfigSpec.BooleanValue defaultHideLines;
    public static ForgeConfigSpec.ConfigValue<String> defaultCompletedLineColor;
    public static ForgeConfigSpec.ConfigValue<String>  defaultUncompletedLineColor;

    public static ForgeConfigSpec build() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        defaultUncompletedIconColor = builder.define("defaultUncompletedIconColor", BetterDisplayInfo.defaultMinecraftUncompletedIconColor);
        defaultUncompletedTitleColor = builder.define("defaultUncompletedTitleColor", BetterDisplayInfo.defaultMinecraftUncompletedTitleColor);
        defaultCompletedIconColor = builder.define("defaultCompletedIconColor", BetterDisplayInfo.defaultMinecraftCompletedIconColor);
        defaultCompletedTitleColor = builder.define("defaultCompletedTitleColor", BetterDisplayInfo.defaultMinecraftCompletedTitleColor);

        doFade = builder.define("doAdvancementsBackgroundFade", true);
        showDebugCoordinates = builder.define("showDebugCoordinates", false);
        orderTabsAlphabetically = builder.define("orderTabsAlphabetically", false);
        uiScaling = builder.comment("Values below 50% might give odd results, use on own risk ;)").defineInRange("uiScaling", 100, 1, 100);

        detailLevel = builder.comment(CriteriaDetail.comments()).defineInList("criteriaDetail", CriteriaDetail.DEFAULT.getName(), CriteriaDetail.names());
        requiresShift = builder.define("criteriaDetailRequiresShift", false);
        addToInventory = builder.define("addInventoryButton", false);

        defaultDrawDirectLines = builder.define("defaultDrawDirectLines", false);
        defaultHideLines = builder.define("defaultHideLines", false);
        defaultCompletedLineColor = builder.define("defaultCompletedLineColor", "#FFFFFF");
        defaultUncompletedLineColor = builder.define("defaultUncompletedLineColor", "#FFFFFF");

        return builder.build();
    }

    public static void pushChanges() {
        BetterDisplayInfo.defaultUncompletedIconColor = ColorHelper.RGB(defaultUncompletedIconColor.get());
        BetterDisplayInfo.defaultUncompletedTitleColor = ColorHelper.RGB(defaultUncompletedTitleColor.get());
        BetterDisplayInfo.defaultCompletedIconColor = ColorHelper.RGB(defaultCompletedIconColor.get());
        BetterDisplayInfo.defaultCompletedTitleColor = ColorHelper.RGB(defaultCompletedTitleColor.get());

        GuiBetterAdvancementTab.doFade = doFade.get();
        GuiScreenBetterAdvancements.showDebugCoordinates = showDebugCoordinates.get();
        GuiScreenBetterAdvancements.orderTabsAlphabetically = orderTabsAlphabetically.get();
        GuiScreenBetterAdvancements.uiScaling = uiScaling.get();

        CriterionGrid.detailLevel = CriteriaDetail.fromName(detailLevel.get());
        CriterionGrid.requiresShift = requiresShift.get();
        BetterAdvancementsScreenButtonWidget.addToInventory = addToInventory.get();

        BetterDisplayInfo.defaultDrawDirectLines = defaultDrawDirectLines.get();
        BetterDisplayInfo.defaultHideLines = defaultHideLines.get();
        BetterDisplayInfo.defaultCompletedLineColor = ColorHelper.RGB(defaultCompletedLineColor.get());
        BetterDisplayInfo.defaultUncompletedLineColor = ColorHelper.RGB(defaultUncompletedLineColor.get());
    }
}
