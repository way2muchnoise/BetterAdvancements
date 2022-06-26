package betteradvancements;

import betteradvancements.fabric.config.ConfigFileHandler;
import betteradvancements.reference.Constants;
import net.fabricmc.api.ClientModInitializer;

public class BetterAdvancements implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.log.info("Loaded");
        ConfigFileHandler.readFromConfig();
    }
}
