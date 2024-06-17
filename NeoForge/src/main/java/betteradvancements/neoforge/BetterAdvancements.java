package betteradvancements.neoforge;

import betteradvancements.neoforge.config.Config;
import betteradvancements.neoforge.handler.GuiOpenHandler;
import betteradvancements.common.reference.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.ID, dist = Dist.CLIENT)
public class BetterAdvancements {
        public BetterAdvancements(ModContainer container) {
            container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
            container.getEventBus().register(Config.instance);
            NeoForge.EVENT_BUS.register(GuiOpenHandler.instance);
    }
}
