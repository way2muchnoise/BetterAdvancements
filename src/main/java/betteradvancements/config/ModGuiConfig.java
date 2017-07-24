package betteradvancements.config;

import betteradvancements.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig(GuiScreen guiScreen) {
        super(guiScreen,
            ConfigHandler.getConfigElements(),
            Reference.ID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }
}
