package betteradvancements.forge.config;

import betteradvancements.common.reference.Constants;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;


public class Config {
    public static Config instance = new Config();

    private Config() {

    }

    public static final ForgeConfigSpec CLIENT = ConfigValues.build();

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
            ((CommentedFileConfig) configEvent.getConfig().getConfigData()).load();
            ConfigValues.pushChanges();
        }
    }
}
