package betteradvancements.config;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.gui.GuiBetterAdvancementTab;
import betteradvancements.reference.Reference;
import betteradvancements.util.ColorHelper;
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

        BetterDisplayInfo.defaultDrawDirectLines = config.get(Configuration.CATEGORY_GENERAL, "defaultDrawDirectLines", false).getBoolean();
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
