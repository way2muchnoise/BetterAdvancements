package betteradvancements.fabric.config;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.gui.BetterAdvancementTab;
import betteradvancements.gui.BetterAdvancementsScreen;
import betteradvancements.gui.BetterAdvancementsScreenButton;
import betteradvancements.util.ColorHelper;
import betteradvancements.util.CriteriaDetail;
import betteradvancements.util.CriterionGrid;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.network.chat.Component;

public class ConfigValues {

    public static ColorEntry defaultUncompletedIconColor;
    public static ColorEntry defaultUncompletedTitleColor;
    public static ColorEntry defaultCompletedIconColor;
    public static ColorEntry defaultCompletedTitleColor;

    public static BooleanListEntry doFade;
    public static BooleanListEntry showDebugCoordinates;
    public static BooleanListEntry orderTabsAlphabetically;
    public static IntegerSliderEntry uiScaling;

    public static DropdownBoxEntry<CriteriaDetail> detailLevel;
    public static BooleanListEntry requiresShift;
    public static BooleanListEntry addToInventory;

    public static BooleanListEntry defaultDrawDirectLines;
    public static BooleanListEntry defaultHideLines;
    public static ColorEntry defaultCompletedLineColor;
    public static ColorEntry defaultUncompletedLineColor;

    public static void build(ConfigCategory category, ConfigEntryBuilder builder) {
        defaultUncompletedIconColor = builder.startAlphaColorField(Component.literal("defaultUncompletedIconColor"), BetterDisplayInfo.defaultUncompletedIconColor)
            .setDefaultValue(ColorHelper.RGB(BetterDisplayInfo.defaultMinecraftUncompletedIconColor))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultUncompletedIconColor = newValue)
            .build();
        category.addEntry(defaultUncompletedIconColor);
        defaultUncompletedTitleColor = builder.startAlphaColorField(Component.literal("defaultUncompletedTitleColor"), BetterDisplayInfo.defaultUncompletedTitleColor)
            .setDefaultValue(ColorHelper.RGB(BetterDisplayInfo.defaultMinecraftUncompletedTitleColor))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultUncompletedTitleColor = newValue)
            .build();
        category.addEntry(defaultUncompletedTitleColor);
        defaultCompletedIconColor = builder.startAlphaColorField(Component.literal("defaultCompletedIconColor"), BetterDisplayInfo.defaultCompletedIconColor)
            .setDefaultValue(ColorHelper.RGB(BetterDisplayInfo.defaultMinecraftCompletedIconColor))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultCompletedIconColor = newValue)
            .build();
        category.addEntry(defaultCompletedIconColor);
        defaultCompletedTitleColor = builder.startAlphaColorField(Component.literal("defaultCompletedTitleColor"), BetterDisplayInfo.defaultCompletedTitleColor)
            .setDefaultValue(ColorHelper.RGB(BetterDisplayInfo.defaultMinecraftCompletedTitleColor))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultCompletedTitleColor = newValue)
            .build();
        category.addEntry(defaultCompletedTitleColor);

        doFade = builder.startBooleanToggle(Component.literal("doAdvancementsBackgroundFade"), BetterAdvancementTab.doFade)
            .setDefaultValue(true)
            .setSaveConsumer(newValue -> BetterAdvancementTab.doFade = newValue)
            .build();
        category.addEntry(doFade);
        showDebugCoordinates = builder.startBooleanToggle(Component.literal("showDebugCoordinates"), BetterAdvancementsScreen.showDebugCoordinates)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> BetterAdvancementsScreen.showDebugCoordinates = newValue)
            .build();
        category.addEntry(showDebugCoordinates);
        orderTabsAlphabetically = builder.startBooleanToggle(Component.literal("orderTabsAlphabetically"), BetterAdvancementsScreen.orderTabsAlphabetically)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> BetterAdvancementsScreen.orderTabsAlphabetically = newValue)
            .build();
        category.addEntry(orderTabsAlphabetically);
        uiScaling = builder.startIntSlider(Component.literal("uiScaling"), BetterAdvancementsScreen.uiScaling, 1, 100)
            .setTooltip(Component.literal("Values below 50% might give odd results, use on own risk ;)"))
            .setDefaultValue(100)
            .setSaveConsumer(newValue -> BetterAdvancementsScreen.uiScaling = newValue)
            .build();
        category.addEntry(uiScaling);

        detailLevel = builder.startDropdownMenu(Component.literal("criteriaDetail"),
            DropdownMenuBuilder.TopCellElementBuilder.of(CriterionGrid.detailLevel, CriteriaDetail::fromName),
            DropdownMenuBuilder.CellCreatorBuilder.of(t -> Component.literal(t.getName()))
        )
            .setSelections(CriteriaDetail.valuesAsList())
            .setDefaultValue(CriteriaDetail.DEFAULT)
            .setSaveConsumer(newValue -> CriterionGrid.detailLevel = newValue)
            .setTooltip(Component.literal(CriteriaDetail.comments()))
            .build();
        category.addEntry(detailLevel);
        requiresShift = builder.startBooleanToggle(Component.literal("criteriaDetailRequiresShift"), CriterionGrid.requiresShift)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> CriterionGrid.requiresShift = newValue)
            .build();
        category.addEntry(requiresShift);
        addToInventory = builder.startBooleanToggle(Component.literal("addInventoryButton"), BetterAdvancementsScreenButton.addToInventory)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> BetterAdvancementsScreenButton.addToInventory = newValue)
            .build();
        category.addEntry(addToInventory);

        defaultDrawDirectLines = builder.startBooleanToggle(Component.literal("defaultDrawDirectLines"), BetterDisplayInfo.defaultDrawDirectLines)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultDrawDirectLines = newValue)
            .build();
        category.addEntry(defaultDrawDirectLines);
        defaultHideLines = builder.startBooleanToggle(Component.literal("defaultHideLines"), BetterDisplayInfo.defaultHideLines)
            .setDefaultValue(false)
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultHideLines = newValue)
            .build();
        category.addEntry(defaultHideLines);
        defaultCompletedLineColor = builder.startAlphaColorField(Component.literal("defaultCompletedLineColor"), BetterDisplayInfo.defaultCompletedLineColor)
            .setDefaultValue(ColorHelper.RGB("#FFFFFF"))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultCompletedLineColor = newValue)
            .build();
        category.addEntry(defaultCompletedLineColor);
        defaultUncompletedLineColor = builder.startAlphaColorField(Component.literal("defaultUncompletedLineColor"), BetterDisplayInfo.defaultUncompletedLineColor)
            .setDefaultValue(ColorHelper.RGB("#FFFFFF"))
            .setSaveConsumer(newValue -> BetterDisplayInfo.defaultUncompletedLineColor = newValue)
            .build();
        category.addEntry(defaultUncompletedLineColor);
    }
}
