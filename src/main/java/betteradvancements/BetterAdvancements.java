package betteradvancements;

import betteradvancements.config.Config;
import betteradvancements.handler.GuiOpenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BetterAdvancements.ID)
public class BetterAdvancements {
    public static final String ID = "betteradvancements";
    public static final Logger log = LogManager.getLogger();

    public BetterAdvancements() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(()-> NetworkConstants.IGNORESERVERONLY, (remote, isServer)-> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
            FMLJavaModLoadingContext.get().getModEventBus().register(Config.instance);
            MinecraftForge.EVENT_BUS.register(GuiOpenHandler.instance);
        }
    }
}
