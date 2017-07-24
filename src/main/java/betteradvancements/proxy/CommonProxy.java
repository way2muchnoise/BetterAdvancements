package betteradvancements.proxy;

import betteradvancements.config.ConfigHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getModConfigurationDirectory());
    }
}
