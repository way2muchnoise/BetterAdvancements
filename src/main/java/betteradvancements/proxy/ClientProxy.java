package betteradvancements.proxy;

import betteradvancements.handler.GuiOpenHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
    }
}
