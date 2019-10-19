package betteradvancements;

import betteradvancements.config.Config;
import betteradvancements.handler.GuiOpenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BetterAdvancements.ID)
public class BetterAdvancements {
    public static final String ID = "betteradvancements";
    public static final Logger log = LogManager.getLogger();

    public BetterAdvancements() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);

            FMLJavaModLoadingContext.get().getModEventBus().register(Config.instance);
            MinecraftForge.EVENT_BUS.register(GuiOpenHandler.instance);
        });
    }
}
