package betteradvancements.forge;

import betteradvancements.forge.config.Config;
import betteradvancements.forge.handler.GuiOpenHandler;
import betteradvancements.common.reference.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.lang.invoke.MethodHandles;

@Mod(Constants.ID)
public class BetterAdvancements {
        public BetterAdvancements(FMLJavaModLoadingContext context) {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(()-> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (remote, isServer)-> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            context.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
            context.getModBusGroup().register(MethodHandles.publicLookup(), Config.instance);
            MinecraftForge.EVENT_BUS.register(GuiOpenHandler.instance);
        }
    }
}
