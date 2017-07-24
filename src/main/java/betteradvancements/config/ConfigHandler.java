package betteradvancements.config;

import betteradvancements.reference.Reference;
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
