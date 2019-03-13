package betteradvancements.config;

import betteradvancements.BetterAdvancements;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class Config {
    public static Config instance = new Config();

    private Config() {

    }

    public static final ForgeConfigSpec CLIENT = ConfigValues.build();

    public void loadConfig(ForgeConfigSpec spec, Path path) {
        BetterAdvancements.log.debug("Loading config file {}", path);

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
            .sync()
            .autosave()
            .writingMode(WritingMode.REPLACE)
            .build();

        BetterAdvancements.log.debug("Built TOML config for {}", path.toString());
        configData.load();
        BetterAdvancements.log.debug("Loaded TOML config file {}", path.toString());
        spec.setConfig(configData);
        ConfigValues.pushChanges();
    }

    @SubscribeEvent
    public void onLoad(final ModConfig.Loading configEvent) {
        BetterAdvancements.log.debug("Loaded {} config file {}", BetterAdvancements.ID, configEvent.getConfig().getFileName());
        ConfigValues.pushChanges();
    }

    @SubscribeEvent
    public void onFileChange(final ModConfig.ConfigReloading configEvent) {
        BetterAdvancements.log.debug("Reloaded {} config file {}", BetterAdvancements.ID, configEvent.getConfig().getFileName());
        ConfigValues.pushChanges();
    }
}
