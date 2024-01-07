package betteradvancements.neoforge;

import betteradvancements.neoforge.config.Config;
import betteradvancements.neoforge.handler.GuiOpenHandler;
import betteradvancements.common.reference.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.ID)
public class BetterAdvancements {
        public BetterAdvancements() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(()-> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (remote, isServer)-> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
            FMLJavaModLoadingContext.get().getModEventBus().register(Config.instance);
            NeoForge.EVENT_BUS.register(GuiOpenHandler.instance);
        }
    }
}
