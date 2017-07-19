package betteradvancements;

import betteradvancements.proxy.CommonProxy;
import betteradvancements.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.ID, name = Reference.NAME, guiFactory = Reference.MOD_GUI_FACTORY, version = Reference.VERSION_FULL)
public class BetterAdvancements {
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        proxy.preInit();
    }
}
