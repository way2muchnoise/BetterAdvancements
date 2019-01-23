package betteradvancements.config;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.gui.GuiBetterAdvancementTab;
import betteradvancements.gui.GuiBetterAdvancementsButton;
import betteradvancements.gui.GuiScreenBetterAdvancements;
import betteradvancements.reference.Reference;
import betteradvancements.util.ColorHelper;
import betteradvancements.util.CriteriaDetail;
import betteradvancements.util.CriterionGrid;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    public static Configuration config;
    private static File configDir;

    public static void init(File configDir) {
        if (ConfigHandler.configDir == null) {
            configDir = new File(configDir, Reference.ID);
            configDir.mkdir();
            ConfigHandler.configDir = configDir;
        }
        if (config == null) {
            config = new Configuration(new File(configDir, Reference.ID + ".cfg"));
            loadConfig();
        }
    }

    public static File getConfigDir() {
        return configDir;
    }

    private static void loadConfig() {
        BetterDisplayInfo.defaultUncompletedIconColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultUncompletedIconColor", "#FFFFFF").getString());
        BetterDisplayInfo.defaultUncompletedTitleColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultUncompletedTitleColor", "#0489C1").getString());
        BetterDisplayInfo.defaultCompletedIconColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultCompletedIconColor", "#DBA213").getString());
        BetterDisplayInfo.defaultCompletedTitleColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultCompletedTitleColor", "#DBA213").getString());

        GuiBetterAdvancementTab.doFade = config.get(Configuration.CATEGORY_GENERAL, "doAdvancementsBackgroundFade", true).getBoolean();
        GuiScreenBetterAdvancements.showDebugCoordinates = config.get(Configuration.CATEGORY_GENERAL, "showDebugCoordinates", false).getBoolean();
        GuiScreenBetterAdvancements.orderTabsAlphabetically = config.get(Configuration.CATEGORY_GENERAL, "orderTabsAlphabetically", false).getBoolean();
        GuiScreenBetterAdvancements.uiScaling = config.get(Configuration.CATEGORY_GENERAL, "uiScaling", 100, "Values below 50% might give odd results, use on own risk ;)", 1, 100).getInt();

        String criteriaDetailString = config.get(Configuration.CATEGORY_GENERAL, "criteriaDetail", CriteriaDetail.DEFAULT.getName(), CriteriaDetail.comments(), CriteriaDetail.names()).getString();
        CriterionGrid.detailLevel = CriteriaDetail.fromName(criteriaDetailString);
        CriterionGrid.requiresShift = config.get(Configuration.CATEGORY_GENERAL, "criteriaDetailRequiresShift", false).getBoolean();
        GuiBetterAdvancementsButton.addToInventory = config.get(Configuration.CATEGORY_GENERAL, "addInventoryButton", false).getBoolean();

        BetterDisplayInfo.defaultDrawDirectLines = config.get(Configuration.CATEGORY_GENERAL, "defaultDrawDirectLines", false).getBoolean();
        BetterDisplayInfo.defaultHideLines = config.get(Configuration.CATEGORY_GENERAL, "defaultHideLines", false).getBoolean();
        BetterDisplayInfo.defaultCompletedLineColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultCompletedLineColor", "#FFFFFF").getString());
        BetterDisplayInfo.defaultUncompletedLineColor = ColorHelper.RGB(config.get(Configuration.CATEGORY_GENERAL, "defaultUncompletedLineColor", "#FFFFFF").getString());

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<>();
        list.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
        return list;
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.ID)) {
            loadConfig();
        }
    }
}
