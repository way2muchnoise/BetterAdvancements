package betteradvancements.fabric.config;

import betteradvancements.common.advancements.BetterDisplayInfo;
import betteradvancements.common.gui.BetterAdvancementTab;
import betteradvancements.common.gui.BetterAdvancementTabType;
import betteradvancements.common.gui.BetterAdvancementsScreen;
import betteradvancements.common.gui.BetterAdvancementsScreenButton;
import betteradvancements.common.reference.Constants;
import betteradvancements.common.util.ColorHelper;
import betteradvancements.common.util.CriteriaDetail;
import betteradvancements.common.util.CriterionGrid;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigFileHandler {
    public static void readFromConfig() {
        JsonObject root = new JsonObject();
        try {
            File configFile = getConfigFile();
            if (!configFile.exists()) {
                writeToConfig();
                configFile = getConfigFile();
            }
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        } catch (IOException e) {
            Constants.log.error(e);
        }

        if (root.has("defaultUncompletedIconColor")) {
            BetterDisplayInfo.defaultUncompletedIconColor = ColorHelper.RGB(root.get("defaultUncompletedIconColor").getAsString());
        }
        if (root.has("defaultUncompletedTitleColor")) {
            BetterDisplayInfo.defaultUncompletedTitleColor = ColorHelper.RGB(root.get("defaultUncompletedTitleColor").getAsString());
        }
        if (root.has("defaultCompletedIconColor")) {
            BetterDisplayInfo.defaultCompletedIconColor = ColorHelper.RGB(root.get("defaultCompletedIconColor").getAsString());
        }
        if (root.has("defaultCompletedTitleColor")) {
            BetterDisplayInfo.defaultCompletedTitleColor = ColorHelper.RGB(root.get("defaultCompletedTitleColor").getAsString());
        }
        if (root.has("doAdvancementsBackgroundFade")) {
            BetterAdvancementTab.doFade = root.get("doAdvancementsBackgroundFade").getAsBoolean();
        }
        if (root.has("showDebugCoordinates")) {
            BetterAdvancementsScreen.showDebugCoordinates = root.get("showDebugCoordinates").getAsBoolean();
        }
        if (root.has("orderTabsAlphabetically")) {
            BetterAdvancementsScreen.orderTabsAlphabetically = root.get("orderTabsAlphabetically").getAsBoolean();
        }
        if (root.has("uiScaling")) {
            BetterAdvancementsScreen.uiScaling = root.get("uiScaling").getAsInt();
        }
        if (root.has("defaultZoom")) {
            BetterAdvancementsScreen.zoom = root.get("defaultZoom").getAsFloat();
        }
        if (root.has("criteriaDetail")) {
            CriterionGrid.detailLevel = CriteriaDetail.fromName(root.get("criteriaDetail").getAsString());
        }
        if (root.has("criteriaDetailRequiresShift")) {
            CriterionGrid.requiresShift = root.get("criteriaDetailRequiresShift").getAsBoolean();
        }
        if (root.has("addInventoryButton")) {
            BetterAdvancementsScreenButton.addToInventory = root.get("addInventoryButton").getAsBoolean();
        }
        if (root.has("defaultDrawDirectLines")) {
            BetterDisplayInfo.defaultDrawDirectLines = root.get("defaultDrawDirectLines").getAsBoolean();
        }
        if (root.has("defaultHideLines")) {
            BetterDisplayInfo.defaultHideLines = root.get("defaultHideLines").getAsBoolean();
        }
        if (root.has("defaultCompletedLineColor")) {
            BetterDisplayInfo.defaultCompletedLineColor = ColorHelper.RGB(root.get("defaultCompletedLineColor").getAsString());
        }
        if (root.has("defaultUncompletedLineColor")) {
            BetterDisplayInfo.defaultUncompletedLineColor = ColorHelper.RGB(root.get("defaultUncompletedLineColor").getAsString());
        }
        if (root.has("onlyUseAboveAdvancementTabs")) {
            BetterAdvancementTabType.onlyUseAbove = root.get("onlyUseAboveAdvancementTabs").getAsBoolean();
        }
    }

    public static void writeToConfig() {
        JsonObject root = new JsonObject();

        root.addProperty("defaultUncompletedIconColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultUncompletedIconColor));
        root.addProperty("defaultUncompletedTitleColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultUncompletedTitleColor));
        root.addProperty("defaultCompletedIconColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultCompletedIconColor));
        root.addProperty("defaultCompletedTitleColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultCompletedTitleColor));
        root.addProperty("doAdvancementsBackgroundFade", BetterAdvancementTab.doFade);
        root.addProperty("showDebugCoordinates", BetterAdvancementsScreen.showDebugCoordinates);
        root.addProperty("orderTabsAlphabetically", BetterAdvancementsScreen.orderTabsAlphabetically);
        root.addProperty("uiScaling", BetterAdvancementsScreen.uiScaling);
        root.addProperty("defaultZoom", BetterAdvancementsScreen.zoom);
        root.addProperty("criteriaDetail", CriterionGrid.detailLevel.getName());
        root.addProperty("criteriaDetailRequiresShift", CriterionGrid.requiresShift);
        root.addProperty("addInventoryButton", BetterAdvancementsScreenButton.addToInventory);
        root.addProperty("defaultDrawDirectLines", BetterDisplayInfo.defaultDrawDirectLines);
        root.addProperty("defaultHideLines", BetterDisplayInfo.defaultHideLines);
        root.addProperty("defaultCompletedLineColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultCompletedLineColor));
        root.addProperty("defaultUncompletedLineColor", ColorHelper.asRGBString(BetterDisplayInfo.defaultUncompletedLineColor));
        root.addProperty("onlyUseAboveAdvancementTabs", BetterAdvancementTabType.onlyUseAbove);

        try (FileWriter file = new FileWriter(getConfigFile())) {
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(root));
            file.flush();
        } catch (IOException e) {
            Constants.log.error(e);
        }
    }

    public static File getConfigFile() throws IOException {
        return FabricLoader.getInstance().getConfigDir().resolve("betteradvancements.json").toFile();
    }
}
