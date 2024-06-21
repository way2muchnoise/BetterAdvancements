package betteradvancements.fabric;

import betteradvancements.fabric.config.ConfigFileHandler;
import betteradvancements.common.reference.Constants;
import net.fabricmc.api.ClientModInitializer;

public class BetterAdvancements implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.log.info("Loaded");
        ConfigFileHandler.readFromConfig();
    }
}
