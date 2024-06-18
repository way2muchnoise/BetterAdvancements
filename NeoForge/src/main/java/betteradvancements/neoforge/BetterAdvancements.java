package betteradvancements.neoforge;

import betteradvancements.neoforge.config.Config;
import betteradvancements.neoforge.handler.GuiOpenHandler;
import betteradvancements.common.reference.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(value = Constants.ID)
public class BetterAdvancements {
        public BetterAdvancements() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(()-> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (remote, isServer)-> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
            FMLJavaModLoadingContext.get().getModEventBus().register(Config.instance);
            MinecraftForge.EVENT_BUS.register(GuiOpenHandler.instance);
        }
    }
}
