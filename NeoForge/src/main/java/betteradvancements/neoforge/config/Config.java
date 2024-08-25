package betteradvancements.neoforge.config;

import betteradvancements.common.reference.Constants;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static Config instance = new Config();

    private Config() {

    }

    public static final ModConfigSpec CLIENT = ConfigValues.build();

    @SubscribeEvent
    public void onLoad(final ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getModId().equals(Constants.ID)) {
            Constants.log.debug("Loaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
            ConfigValues.pushChanges();
        }
    }

    @SubscribeEvent
    public void onFileChange(final ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getModId().equals(Constants.ID)) {
            Constants.log.debug("Reloaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
            ((CommentedFileConfig) configEvent.getConfig().getLoadedConfig().config()).load();
            ConfigValues.pushChanges();
        }
    }
}
