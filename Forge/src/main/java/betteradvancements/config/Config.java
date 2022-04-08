package betteradvancements.config;

import betteradvancements.reference.Constants;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;


public class Config {
    public static Config instance = new Config();

    private Config() {

    }

    public static final ForgeConfigSpec CLIENT = ConfigValues.build();

    @SubscribeEvent
    public void onLoad(final ModConfigEvent.Loading configEvent) {
        Constants.log.debug("Loaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        ConfigValues.pushChanges();
    }

    @SubscribeEvent
    public void onFileChange(final ModConfigEvent.Reloading configEvent) {
        Constants.log.debug("Reloaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        ((CommentedFileConfig)configEvent.getConfig().getConfigData()).load();
        ConfigValues.pushChanges();
    }
}
